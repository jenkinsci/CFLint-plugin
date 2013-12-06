package org.jenkinsci.plugins.cflint.tokens;

import hudson.plugins.analysis.tokens.AbstractAnnotationsCountTokenMacro;

import org.jenkinsci.plugins.cflint.LintMavenResultAction;
import org.jenkinsci.plugins.cflint.LintResultAction;

/** Provides a token that evaluates to the number of CFBugs warnings. */
// @Extension(optional = true)
public class LintWarningCountTokenMacro extends AbstractAnnotationsCountTokenMacro {

    @SuppressWarnings("unchecked")
    public LintWarningCountTokenMacro() {
        super("ANDROID_LINT_COUNT", LintResultAction.class, LintMavenResultAction.class);
    }

}
