package org.elixir_lang.code_insight.line_marker_provider;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.SeparatorPlacement;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.previousSiblingExpression;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange;

public class CallDefinition implements LineMarkerProvider {
    /*
     * Fields
     */

    private final DaemonCodeAnalyzerSettings daemonCodeAnalyzerSettings;
    private final EditorColorsManager editorColorsManager;

    /*
     * Constructors
     */

    public CallDefinition(DaemonCodeAnalyzerSettings daemonCodeAnalyzerSettings,
                          EditorColorsManager editorColorsManager) {
        this.daemonCodeAnalyzerSettings = daemonCodeAnalyzerSettings;
        this.editorColorsManager = editorColorsManager;
    }

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
        assert elements != null;
        // do nothing
    }

    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        LineMarkerInfo lineMarkerInfo = null;

        if (element instanceof Call) {
            lineMarkerInfo = getLineMarkerInfo((Call) element);
        }

        return lineMarkerInfo;
    }

    /*
     * Private Instance Methods
     */

    @Nullable
    private LineMarkerInfo getLineMarkerInfo(@NotNull Call call) {
        LineMarkerInfo lineMarkerInfo = null;

        if (daemonCodeAnalyzerSettings.SHOW_METHOD_SEPARATORS && CallDefinitionClause.is(call)) {
            Call previous = previousCallDefinitionClause(call);
            boolean firstClause;

            if (previous == null) {
                firstClause = true;
            } else {
                Pair<String, IntRange> callNameArityRange = nameArityRange(call);

                if (callNameArityRange != null) {
                    Pair<String, IntRange> previousNameArityRange = nameArityRange(previous);

                    firstClause = previousNameArityRange == null || !previousNameArityRange.equals(callNameArityRange);
                } else {
                    firstClause = true;
                }
            }

            if (firstClause) {
                lineMarkerInfo = new LineMarkerInfo<Call>(
                        call,
                        call.getTextRange(),
                        null,
                        Pass.UPDATE_ALL,
                        null,
                        null,
                        GutterIconRenderer.Alignment.RIGHT
                );
                EditorColorsScheme editorColorsScheme = editorColorsManager.getGlobalScheme();
                lineMarkerInfo.separatorColor = editorColorsScheme.getColor(CodeInsightColors.METHOD_SEPARATORS_COLOR);
                lineMarkerInfo.separatorPlacement = SeparatorPlacement.TOP;
            }
        }

        return lineMarkerInfo;
    }

    @Nullable
    private Call previousCallDefinitionClause(@NotNull PsiElement element) {
        PsiElement expression = element;
        Call previous = null;

        while (expression != null) {
            expression = previousSiblingExpression(expression);

            if (expression instanceof Call) {
                Call call = (Call) expression;

                if (CallDefinitionClause.is(call)) {
                    previous = call;
                    break;
                }
            }
        }

        return previous;
    }
}
