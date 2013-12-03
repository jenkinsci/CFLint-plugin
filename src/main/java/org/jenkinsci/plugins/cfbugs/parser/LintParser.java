package org.jenkinsci.plugins.cfbugs.parser;

import hudson.plugins.analysis.core.AbstractAnnotationParser;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringEscapeUtils;
import org.jenkinsci.plugins.cfbugs.Messages;
import org.xml.sax.SAXException;

/** A parser for CFBugs XML files. */
public class LintParser extends AbstractAnnotationParser {

	/**
	 * Magic value used to denote annotations which have on associated location.
	 */
	public static final String FILENAME_UNKNOWN = "(none)";

	private static final String SEVERITY_FATAL = "Fatal";
	
	private static final String SEVERITY_ERROR = "Error";
	
	private static final String SEVERITY_INFORMATIONAL = "Information";
	private static final String SEVERITY_INFO = "Info";
	private static final String SEVERITY_WARNING = "Warning";
	private static final String SEVERITY_WARN = "Warn";
	

	private static final long serialVersionUID = 7110868408124058985L;

	/**
	 * Creates a parser for CFBugs files.
	 * 
	 * @param defaultEncoding
	 *            The encoding to use when reading files.
	 */
	public LintParser(final String defaultEncoding) {
		super(defaultEncoding);
	}

	@Override
	public Collection<FileAnnotation> parse(final InputStream file, final String moduleName)
			throws InvocationTargetException {
		try {
			final Digester digester = new Digester();
			digester.setValidating(false);
			digester.setClassLoader(LintParser.class.getClassLoader());

			final List<LintIssue> issues = new ArrayList<LintIssue>();
			digester.push(issues);

			final String issueXPath = "issues/issue";
			digester.addObjectCreate(issueXPath, LintIssue.class);
			digester.addSetProperties(issueXPath);
			digester.addSetNext(issueXPath, "add");

			final String locationXPath = issueXPath + "/location";
			digester.addObjectCreate(locationXPath, Location.class);
			digester.addSetNestedProperties(locationXPath, "Expression", "expression");
			digester.addSetProperties(locationXPath);
			digester.addSetNext(locationXPath, "addLocation", Location.class.getName());

			digester.parse(file);

			return convert(issues, moduleName);
		} catch (final IOException exception) {
			throw new InvocationTargetException(exception);
		} catch (final SAXException exception) {
			throw new InvocationTargetException(exception);
		}
	}

	/**
	 * Converts the Lint object structure to that of the analysis-core API.
	 * 
	 * @param issues
	 *            The parsed Lint issues.
	 * @param moduleName
	 *            Name of the maven module, if any.
	 * @return A collection of the discovered issues.
	 */
	private Collection<FileAnnotation> convert(final List<LintIssue> issues, final String moduleName) {
		final ArrayList<FileAnnotation> annotations = new ArrayList<FileAnnotation>();

		for (final LintIssue issue : issues) {
			// Get filename of first location, if available
			// final Map<String,List<Location>> fileLocations =
			// groupIssueLocationsByFileName(issue.getLocations());
			// final Location[] locations = issue.getLocations().toArray(new
			// Location[0]);
			// for(Entry<String, List<Location>> locationEntry :
			// fileLocations.entrySet()){
			final List<Location> locations = issue.getLocations();
			final int locationCount = locations.size();
			final String filename;
			final int lineNumber;
			if (locationCount == 0) {
				filename = FILENAME_UNKNOWN;
				lineNumber = 0;
			} else {
				// TODO: Ideally, we would expand relative paths (like
				// ParserResult does later)
				filename = locations.get(0).getFile();
				lineNumber = locations.get(0).getLine();
				issue.setErrorLine1(locations.get(0).getExpression());
			}

			final Priority priority = getPriority(issue.getSeverity());
			String category = issue.getCategory();
			String explanation = StringEscapeUtils.escapeHtml(issue.getExplanation());

			// If category is missing the file is from pre-r21 Lint, so show an
			// explanation
			if (category == null) {
				category = Messages.CFBugs_Parser_UnknownCategory();
				explanation = Messages.CFBugs_Parser_UnknownExplanation(issue.getId());
			}

			if (explanation == null) {
				explanation = StringEscapeUtils.escapeHtml(locations.get(0).getMessage());
			}

			// Create annotation
			final LintAnnotation annotation = new LintAnnotation(priority, StringEscapeUtils.escapeHtml(issue
					.getMessage()), category, issue.getId(), lineNumber);
			annotation.setExplanation(explanation);
			annotation.setErrorLines(StringEscapeUtils.escapeHtml(issue.getErrorLine1()),
					StringEscapeUtils.escapeHtml(issue.getErrorLine2()));
			annotation.setModuleName(moduleName);
			annotation.setFileName(filename);

			// Generate a hash to uniquely identify this issue and its context
			// (i.e. source code),
			// so that we can detect in later builds whether this issue still
			// exists, or was fixed
			if (lineNumber == 0) {
				// This issue is for a non-source file, so use the issue type
				// and filename
				final int hashcode = String.format("%s:%s", filename, issue.getId()).hashCode();
				annotation.setContextHashCode(hashcode);
			} else {
				// This is a source file (i.e. Java or XML), so use a few lines
				// of context
				// surrounding the line on which the issue first occurs, so that
				// we can detect
				// whether this issue still exists later, even if the line
				// numbers have changed
				try {
					annotation.setContextHashCode(createContextHashCode(filename, lineNumber));
				} catch (final IOException e) {
					// Filename is probably not relative to the workspace root,
					// so we can't read out
					// the surrounding context of this issue. Nothing we can do
					// about this
				}
			}

			// Add additional locations for this the issue, if any
			if (locations.size() > 1) {
				final Iterator<Location> locIterator = locations.iterator();
				locIterator.next();// Not the first one
				while (locIterator.hasNext()) {
					annotation.addLocation(locIterator.next());
				}
			}

			annotations.add(annotation);
			// }
		}

		return annotations;
	}

	private Map<String, List<Location>> groupIssueLocationsByFileName(final List<Location> locations) {
		final Map<String, List<Location>> retMap = new HashMap<String, List<Location>>();
		for (final Location l : locations) {
			if (!retMap.containsKey(l.getFile())) {
				retMap.put(l.getFile(), new ArrayList<Location>());
			}
			retMap.get(l.getFile()).add(l);
		}
		return retMap;
	}

	/**
	 * Maps a Lint issue severity to an analysis-core priority value.
	 * 
	 * @param severity
	 *            Issue severity value read from XML.
	 * @return Corresponding priority value.
	 */
	private Priority getPriority(final String severity) {
		if (SEVERITY_FATAL.equalsIgnoreCase(severity) || SEVERITY_ERROR.equalsIgnoreCase(severity)) {
			return Priority.HIGH;
		}
		if (SEVERITY_WARNING.equalsIgnoreCase(severity) || SEVERITY_WARN.equalsIgnoreCase(severity)) {
			return Priority.NORMAL;
		}
		if (SEVERITY_INFORMATIONAL.equalsIgnoreCase(severity) || SEVERITY_INFO.equalsIgnoreCase(severity)) {
			return Priority.LOW;
		}
		return Priority.NORMAL;
	}

}
