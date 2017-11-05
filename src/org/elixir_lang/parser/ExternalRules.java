package org.elixir_lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.mock.MockProjectEx;
import com.intellij.openapi.project.Project;
import org.elixir_lang.sdk.elixir.Release;
import org.elixir_lang.sdk.elixir.Type;
import org.jetbrains.annotations.NotNull;

public class ExternalRules {
    static boolean ifVersion(@NotNull PsiBuilder psiBuilder,
                             @SuppressWarnings("unused") int level,
                             @NotNull String operatorString,
                             @SuppressWarnings("SameParameterValue") @NotNull String releaseString) {
        Project project = psiBuilder.getProject();
        Release release = Type.getRelease(project);

        assert !(release == null && project instanceof MockProjectEx) :
                "Release MUST be set during testing of ifVersion rules:\n" +
                        "Call `setProjectSdkFromEbinDirectory();` to setup the Release.";

        Release limit = Release.fromString(releaseString);

        assert limit != null;

        boolean keepParsing;

        switch (Operator.valueOf(operatorString)) {
            case LT:
                // assume UNKNOWN release is GE limit and so rule DOES NOT run
                keepParsing = release != null && release.compareTo(limit) < 0;

                break;
            case GE:
                // assume UNKNOWN release is GE limit and so rule DOES run
                keepParsing = release == null || release.compareTo(limit) >= 0;

                break;
            default:
                keepParsing = true;

                break;
        }

        return keepParsing;
    }

    public enum Operator {
        LT, GE
    }
}
