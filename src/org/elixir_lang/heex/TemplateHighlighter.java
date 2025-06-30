package org.elixir_lang.heex;

import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import org.elixir_lang.ElixirFileType;
import org.elixir_lang.heex.html.HeexHTMLLanguage;
import org.elixir_lang.heex.psi.Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.heex.file.Type.onlyTemplateDataFileType;

// See https://github.com/JetBrains/intellij-plugins/blob/500f42337a87f463e0340f43e2411266fcfa9c5f/handlebars/src/com/dmarcotte/handlebars/HbTemplateHighlighter.java
public class TemplateHighlighter extends LayeredLexerEditorHighlighter {
    public TemplateHighlighter(@Nullable Project project,
                               @Nullable VirtualFile virtualFile,
                               @NotNull EditorColorsScheme editorColorsScheme) {
        // create main highlighter
        super(new Highlighter(), editorColorsScheme);

        // highlighter for outer lang
        FileType type = null;

        if (project == null || virtualFile == null) {
            type = FileTypes.PLAIN_TEXT;
        } else {
            com.intellij.lang.Language language =
                    TemplateDataLanguageMappings.getInstance(project).getMapping(virtualFile);

            if (language != null) {
                if (language.is(HTMLLanguage.INSTANCE)) {
                    language = HeexHTMLLanguage.INSTANCE;
                }

                type = language.getAssociatedFileType();
            }

            if (type == null) {
                type = onlyTemplateDataFileType(virtualFile).orElse(null);
            }

            if (type == null) {
                type = HeexLanguage.defaultTemplateLanguageFileType();
            }
        }

        SyntaxHighlighter dataHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(type, project, virtualFile);
        registerLayer(Types.DATA, new LayerDescriptor(dataHighlighter, ""));

        SyntaxHighlighter elixirHighligher = SyntaxHighlighterFactory.getSyntaxHighlighter(ElixirFileType.INSTANCE, project, virtualFile);
        registerLayer(Types.ELIXIR, new LayerDescriptor(elixirHighligher, ""));
    }

    @Override
    protected boolean updateLayers() {
        return true;
    }
}
