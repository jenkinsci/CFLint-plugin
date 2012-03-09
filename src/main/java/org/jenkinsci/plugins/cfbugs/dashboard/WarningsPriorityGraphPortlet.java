package org.jenkinsci.plugins.cfbugs.dashboard;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.plugins.analysis.core.AbstractProjectAction;
import hudson.plugins.analysis.dashboard.AbstractWarningsGraphPortlet;
import hudson.plugins.analysis.graph.BuildResultGraph;
import hudson.plugins.analysis.graph.PriorityGraph;
import hudson.plugins.view.dashboard.DashboardPortlet;

import org.jenkinsci.plugins.cfbugs.CFBugsDescriptor;
import org.jenkinsci.plugins.cfbugs.LintProjectAction;
import org.jenkinsci.plugins.cfbugs.Messages;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A portlet that shows the warnings trend graph by priority.
 *
 * @author Ulli Hafner
 */
public final class WarningsPriorityGraphPortlet extends AbstractWarningsGraphPortlet {
    /**
     * Creates a new instance of {@link WarningsPriorityGraphPortlet}.
     *
     * @param name
     *            the name of the portlet
     * @param width
     *            width of the graph
     * @param height
     *            height of the graph
     * @param dayCountString
     *            number of days to consider
     */
    @DataBoundConstructor
    public WarningsPriorityGraphPortlet(final String name, final String width, final String height, final String dayCountString) {
        super(name, width, height, dayCountString);

        configureGraph(getGraphType());
    }

    /** {@inheritDoc} */
    @Override
    protected Class<? extends AbstractProjectAction<?>> getAction() {
        return LintProjectAction.class;
    }

    /** {@inheritDoc} */
    @Override
    protected String getPluginName() {
        return CFBugsDescriptor.PLUGIN_NAME;
    }

    /** {@inheritDoc} */
    @Override
    protected BuildResultGraph getGraphType() {
        return new PriorityGraph();
    }

    /**
     * Extension point registration.
     *
     * @author Ulli Hafner
     */
    @Extension(optional = true)
    public static class WarningsGraphDescriptor extends Descriptor<DashboardPortlet> {
        @Override
        public String getDisplayName() {
            return Messages.Portlet_WarningsPriorityGraph();
        }
    }
}

