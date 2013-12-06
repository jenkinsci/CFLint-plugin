package org.jenkinsci.plugins.cflint.parser;


import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;

import org.jenkinsci.plugins.cflint.Messages;
import org.jenkinsci.plugins.cflint.parser.LintAnnotation;
import org.jenkinsci.plugins.cflint.parser.LintParser;
import org.jenkinsci.plugins.cflint.parser.Location;

public class LintParserTest extends TestCase {

    private static final String MODULE_NAME = "test";

    // No explanations should be available for any issues
    public void testParser_pre_r21() throws Exception {
        List<LintAnnotation> annotations = parseResults("lint-results_r20.xml");
        assertEquals(4, annotations.size());

        LintAnnotation a = annotations.get(0);
        assertNull(a.getMessage());
        assertEquals(Priority.HIGH, a.getPriority());
        assertEquals(LintParser.FILENAME_UNKNOWN, a.getFileName());
        assertEquals("UnknownId", a.getType());
        assertUnknownIssue(a);
        

        a = annotations.get(1);
        assertEquals("Call requires API level 8 (current min is 7): "
                + "android.view.MotionEvent#getActionIndex",
                a.getMessage());
        assertEquals(Priority.HIGH, a.getPriority());
        assertEquals("bin/classes/InputObject.class", a.getFileName());
        assertEquals("NewApi", a.getType());
        assertUnknownIssue(a);
        assertEquals("x=123",a.getErrorLines().get(0));

        a = annotations.get(2);
        assertEquals("The &lt;activity&gt; MonitoredActivity is not registered in the manifest",
                a.getMessage());
        assertEquals(Priority.NORMAL, a.getPriority());
        assertEquals("bin/classes/MonitoredActivity.class", a.getFileName());
        assertEquals("Registered", a.getType());
        assertUnknownIssue(a);

        a = annotations.get(3);
        assertEquals(Priority.LOW, a.getPriority());
        assertEquals("Avoid using &quot;px&quot; as units; use &quot;dp&quot; instead",
                a.getMessage());
        assertEquals("res/layout/foo.xml", a.getFileName());
        assertEquals(19, a.getPrimaryLineNumber());
        assertUnknownIssue(a);
    }


    // Asserts that an issue has the pre-r21 'unknown issue' explanations
    private static void assertUnknownIssue(LintAnnotation a) {
        assertEquals(Messages.CFLint_Parser_UnknownCategory(), a.getCategory());
        assertEquals(Messages.CFLint_Parser_UnknownExplanation(a.getType()),
                a.getExplanation());
    }

    private List<LintAnnotation> parseResults(String filename) throws InvocationTargetException {
        LintParser parser = new LintParser("UTF-8");
        InputStream stream = getClass().getResourceAsStream(filename);
        List<LintAnnotation> list = new ArrayList<LintAnnotation>();
        for (FileAnnotation a : parser.parse(stream, MODULE_NAME)) {
            assertEquals(MODULE_NAME, a.getModuleName());
            list.add((LintAnnotation) a);
        }
        return list;
    }

}
