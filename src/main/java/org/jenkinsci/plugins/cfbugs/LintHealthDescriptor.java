package org.jenkinsci.plugins.cfbugs;

import hudson.plugins.analysis.core.AbstractHealthDescriptor;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.util.model.AnnotationProvider;

import org.jenkinsci.plugins.cfbugs.Messages;
import org.jvnet.localizer.Localizable;

/** A health descriptor for CFBugs build results. */
public class LintHealthDescriptor extends AbstractHealthDescriptor {

    private static final long serialVersionUID = -5172234332792441306L;

    /**
     * Creates a new instance of {@link LintHealthDescriptor} based on given descriptor.
     *
     * @param healthDescriptor
     *            The descriptor to copy the values from.
     */
    public LintHealthDescriptor(final HealthDescriptor healthDescriptor) {
        super(healthDescriptor);
    }

    @Override
    protected Localizable createDescription(final AnnotationProvider result) {
        return Messages._CFBugs_ResultAction_HealthReport(result.getNumberOfAnnotations());
    }

}
