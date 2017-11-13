package org.elixir_lang.eex;

import com.google.common.collect.Iterables;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx;
import com.intellij.openapi.fileTypes.impl.FileTypeAssocTable;
import com.intellij.openapi.fileTypes.impl.FileTypeConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.FakeVirtualFile;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import org.elixir_lang.ElixirFileType;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.eex.file.Type;
import org.elixir_lang.eex.psi.Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.elixir_lang.eex.file.Type.onlyTemplateDataFileType;

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
                type = language.getAssociatedFileType();
            }

            if (type == null) {
                type = onlyTemplateDataFileType(virtualFile).orElse(null);
            }

            if (type == null) {
                type = Language.defaultTemplateLanguageFileType();
            }
        }

        SyntaxHighlighter dataHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(type, project, virtualFile);

        registerLayer(Types.DATA, new LayerDescriptor(dataHighlighter, ""));

        SyntaxHighlighter elixirHighligher = SyntaxHighlighterFactory.getSyntaxHighlighter(ElixirFileType.INSTANCE, project, virtualFile);

        registerLayer(Types.ELIXIR, new LayerDescriptor(elixirHighligher, ""));
    }
}
