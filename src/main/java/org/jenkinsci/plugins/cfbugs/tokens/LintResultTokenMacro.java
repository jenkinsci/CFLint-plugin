package org.jenkinsci.plugins.cfbugs.tokens;

import hudson.plugins.analysis.tokens.AbstractResultTokenMacro;

import org.jenkinsci.plugins.cfbugs.LintMavenResultAction;
import org.jenkinsci.plugins.cfbugs.LintResultAction;

/** Provides a token that evaluates to the CFBugs build result. */
// @Extension(optional = true)
public class LintResultTokenMacro extends AbstractResultTokenMacro {

    @SuppressWarnings("unchecked")
    public LintResultTokenMacro() {
        super("ANDROID_LINT_RESULT", LintResultAction.class, LintMavenResultAction.class);
    }

}
