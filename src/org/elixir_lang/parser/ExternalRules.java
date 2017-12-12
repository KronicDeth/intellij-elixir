package org.elixir_lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.elixir_lang.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

import static org.elixir_lang.file.LevelPropertyPusher.VIRTUAL_FILE;
import static org.elixir_lang.file.LevelPropertyPusher.level;

public class ExternalRules {
    static boolean ifVersion(@NotNull PsiBuilder psiBuilder,
                             @SuppressWarnings("unused") int depth,
                             @NotNull Operator operator,
                             @SuppressWarnings("SameParameterValue") @NotNull Level targetLevel) {
        Project project = psiBuilder.getProject();
        VirtualFile virtualFile = psiBuilder.getUserData(VIRTUAL_FILE);
        Level virtualFileLevel = level(project, virtualFile);

        return operator.keepParsing(virtualFileLevel, targetLevel);
    }

    public enum Operator {
        LT((virtualFileLevel, targetLevel) -> virtualFileLevel.compareTo(targetLevel) < 0),
        GE((virtualFileLevel, targetLevel) -> virtualFileLevel.compareTo(targetLevel) >= 0);

        @NotNull
        private final BiFunction<Level, Level, Boolean> operation;

        Operator(@NotNull BiFunction<Level, Level, Boolean> operation) {
            this.operation = operation;
        }

        public boolean keepParsing(@NotNull Level virtualFileLevel, @NotNull Level targetLevel) {
            return operation.apply(virtualFileLevel, targetLevel);
        }
    }
}
