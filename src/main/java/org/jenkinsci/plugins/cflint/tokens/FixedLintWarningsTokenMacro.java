package org.jenkinsci.plugins.cflint.tokens;

import hudson.plugins.analysis.tokens.AbstractFixedAnnotationsTokenMacro;

import org.jenkinsci.plugins.cflint.LintMavenResultAction;
import org.jenkinsci.plugins.cflint.LintResultAction;

/** Provides a token that evaluates to the number of fixed CFLint warnings. */
// @Extension(optional = true)
public class FixedLintWarningsTokenMacro extends AbstractFixedAnnotationsTokenMacro {

    @SuppressWarnings("unchecked")
    public FixedLintWarningsTokenMacro() {
        super("ANDROID_LINT_FIXED", LintResultAction.class, LintMavenResultAction.class);
    }

}
