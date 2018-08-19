package org.elixir_lang.eex.file;

import com.google.common.collect.Iterables;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.vfs.VirtualFile;
import org.elixir_lang.eex.Icons;
import org.elixir_lang.eex.Language;
import org.elixir_lang.eex.TemplateHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// See https://github.com/JetBrains/intellij-plugins/blob/500f42337a87f463e0340f43e2411266fcfa9c5f/handlebars/src/com/dmarcotte/handlebars/file/HbFileType.java
public class Type extends LanguageFileType implements TemplateLanguageFileType {
    private static final String DEFAULT_EXTENSION = "eex";
    public static final LanguageFileType INSTANCE = new Type();

    private Type() {
        this(Language.INSTANCE);
    }

    protected Type(com.intellij.lang.Language lang) {
        super(lang);

        FileTypeEditorHighlighterProviders.INSTANCE.addExplicitExtension(
                this,
                (project, fileType, virtualFile, editorColorsScheme) ->
                        new TemplateHighlighter(project, virtualFile, editorColorsScheme)
        );
    }

    private static Set<FileType> templateDataFileTypeSet(@NotNull VirtualFile virtualFile) {
        String path = virtualFile.getPath();
        int pathLength = path.length();
        FileTypeManager fileTypeManager = FileTypeManager.getInstance();

        return fileTypeManager
                .getAssociations(Type.INSTANCE)
                .stream()
                .filter(ExtensionFileNameMatcher.class::isInstance)
                .map(ExtensionFileNameMatcher.class::cast)
                .map(ExtensionFileNameMatcher::getExtension)
                .map(extension -> '.' + extension)
                .filter(path::endsWith)
                .map(dotExtension -> path.substring(0, pathLength - dotExtension.length()))
                .map(fileTypeManager::getFileTypeByFileName)
                .collect(Collectors.toSet());
    }

    public static Optional<FileType> onlyTemplateDataFileType(@NotNull VirtualFile virtualFile) {
        Set<FileType> typeSet = templateDataFileTypeSet(virtualFile);
        Optional<FileType> optionalType;

        if (typeSet.size() == 1) {
            FileType type = Iterables.getOnlyElement(typeSet);

            if (type == FileTypes.UNKNOWN) {
                optionalType = Optional.empty();
            } else {
                optionalType = Optional.of(type);
            }
        } else {
            optionalType = Optional.empty();
        }

        return optionalType;
    }

    @NotNull
    @Override
    public String getName() {
        return "EEx";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Embedded Elixir";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FILE;
    }
}
