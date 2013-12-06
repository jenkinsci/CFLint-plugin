package org.jenkinsci.plugins.cflint.tokens;

import hudson.plugins.analysis.tokens.AbstractResultTokenMacro;

import org.jenkinsci.plugins.cflint.LintMavenResultAction;
import org.jenkinsci.plugins.cflint.LintResultAction;

/** Provides a token that evaluates to the CFLint build result. */
// @Extension(optional = true)
public class LintResultTokenMacro extends AbstractResultTokenMacro {

    @SuppressWarnings("unchecked")
    public LintResultTokenMacro() {
        super("ANDROID_LINT_RESULT", LintResultAction.class, LintMavenResultAction.class);
    }

}
