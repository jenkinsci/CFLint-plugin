package org.jenkinsci.plugins.cfbugs.tokens;

import hudson.plugins.analysis.tokens.AbstractFixedAnnotationsTokenMacro;

import org.jenkinsci.plugins.cfbugs.LintMavenResultAction;
import org.jenkinsci.plugins.cfbugs.LintResultAction;

/** Provides a token that evaluates to the number of fixed CFBugs warnings. */
// @Extension(optional = true)
public class FixedLintWarningsTokenMacro extends AbstractFixedAnnotationsTokenMacro {

    @SuppressWarnings("unchecked")
    public FixedLintWarningsTokenMacro() {
        super("ANDROID_LINT_FIXED", LintResultAction.class, LintMavenResultAction.class);
    }

}
