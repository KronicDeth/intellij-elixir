package org.elixir_lang.heex.file;

import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.LanguageSubstitutors;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.templateLanguages.ConfigurableTemplateLanguageFileViewProvider;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import com.intellij.psi.tree.IElementType;
import gnu.trove.THashSet;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.heex.HeexLanguage;
import org.elixir_lang.heex.element_type.HTMLEmbeddedElixir;
import org.elixir_lang.heex.file.psi.TemplateData;
import org.elixir_lang.heex.html.HeexHTMLLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.elixir_lang.heex.file.Type.onlyTemplateDataFileType;

// See https://github.com/JetBrains/intellij-plugins/blob/500f42337a87f463e0340f43e2411266fcfa9c5f/handlebars/src/com/dmarcotte/handlebars/file/HbFileViewProvider.java
public class ViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider
        implements ConfigurableTemplateLanguageFileViewProvider {
    private static final ConcurrentMap<String, IElementType> ELEMENT_TYPE_BY_LANGUAGE_ID = new ConcurrentHashMap<>();
    @NotNull
    private final com.intellij.lang.Language baseLanguage;
    @NotNull
    private final com.intellij.lang.Language templateDataLanguage;

    public ViewProvider(@NotNull PsiManager manager,
                        @NotNull VirtualFile file,
                        boolean physical,
                        @NotNull com.intellij.lang.Language baseLanguage,
                        @NotNull com.intellij.lang.Language templateLanguage) {
        super(manager, file, physical);
        this.baseLanguage = baseLanguage;
        this.templateDataLanguage = templateLanguage;
    }

    public ViewProvider(@NotNull PsiManager psiManager,
                        @NotNull VirtualFile virtualFile,
                        boolean physical,
                        @NotNull com.intellij.lang.Language baseLanguage) {
        this(psiManager, virtualFile, physical, baseLanguage, templateDataLanguage(psiManager, virtualFile));
    }

    private static IElementType elementType(com.intellij.lang.Language language) {
        return ELEMENT_TYPE_BY_LANGUAGE_ID.computeIfAbsent(
                language.getID(),
                languageID -> {
                    if (language == ElixirLanguage.INSTANCE) {
                        return new HTMLEmbeddedElixir();
                    } else {
                        return TemplateData.INSTANCE;
                    }
                }
        );
    }

    private static com.intellij.lang.Language templateDataLanguage(@NotNull PsiManager psiManager,
                                                                   @NotNull VirtualFile virtualFile) {
        Project project = psiManager.getProject();
        com.intellij.lang.Language templateDataLanguage =
                TemplateDataLanguageMappings.getInstance(project).getMapping(virtualFile);

        if (templateDataLanguage == null) {
            templateDataLanguage = onlyTemplateDataFileType(virtualFile)
                    .filter(LanguageFileType.class::isInstance)
                    .map(LanguageFileType.class::cast)
                    .map(LanguageFileType::getLanguage)
                    .orElse(null);
        }

        if (templateDataLanguage == null) {
            templateDataLanguage = HeexLanguage.defaultTemplateLanguageFileType().getLanguage();
        }

        com.intellij.lang.Language substituteLang =
                LanguageSubstitutors.getInstance().substituteLanguage(templateDataLanguage, virtualFile, project);

        // only use a substituted language if it's templateable
        if (TemplateDataLanguageMappings.getTemplateableLanguages().contains(substituteLang)) {
            templateDataLanguage = substituteLang;
        }

        return templateDataLanguage;
    }

    @Nullable
    @Override
    protected PsiFile createFile(@NotNull com.intellij.lang.Language language) {
        ParserDefinition parserDefinition;
        PsiFileImpl psiFileImpl;

        if (language.isKindOf(HTMLLanguage.INSTANCE)) {
            parserDefinition = getDefinition(HeexHTMLLanguage.INSTANCE);
        } else {
            parserDefinition = getDefinition(language);
        }

        if (parserDefinition == null) {
            psiFileImpl = null;
        } else if (language.isKindOf(getBaseLanguage())) {
            psiFileImpl = (PsiFileImpl) parserDefinition.createFile(this);
        } else {
            psiFileImpl = (PsiFileImpl) parserDefinition.createFile(this);
            psiFileImpl.setContentElementType(elementType(language));
        }

        return psiFileImpl;
    }

    @Nullable
    private ParserDefinition getDefinition(@NotNull com.intellij.lang.Language language) {
        com.intellij.lang.Language baseLanguage = getBaseLanguage();

        if (language.isKindOf(baseLanguage)) {
            language = baseLanguage;
        }

        return LanguageParserDefinitions.INSTANCE.forLanguage(language);
    }

    @NotNull
    @Override
    public com.intellij.lang.Language getBaseLanguage() {
        return baseLanguage;
    }

    @NotNull
    @Override
    public Set<com.intellij.lang.Language> getLanguages() {
        return Set.of(getTemplateDataLanguage(), getBaseLanguage(), ElixirLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public com.intellij.lang.Language getTemplateDataLanguage() {
        if (templateDataLanguage == HTMLLanguage.INSTANCE) {
            return HeexHTMLLanguage.INSTANCE;
        }

        return templateDataLanguage;
    }

    @Override
    protected @NotNull MultiplePsiFilesPerDocumentFileViewProvider cloneInner(@NotNull VirtualFile fileCopy) {
        return new ViewProvider(getManager(), fileCopy, false, baseLanguage, templateDataLanguage);
    }

    @Override
    public boolean supportsIncrementalReparse(@NotNull com.intellij.lang.Language rootLanguage) {
        return false;
    }
}
