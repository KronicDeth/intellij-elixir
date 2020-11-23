package org.elixir_lang;

import com.intellij.facet.FacetType;
import com.intellij.framework.detection.FacetBasedFrameworkDetector;
import com.intellij.framework.detection.FileContentPattern;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.patterns.ElementPattern;
import com.intellij.util.indexing.FileContent;
import org.elixir_lang.facet.Configuration;
import org.elixir_lang.facet.Type;
import org.jetbrains.annotations.NotNull;

public class FrameworkDetector extends FacetBasedFrameworkDetector<Facet, Configuration> {
    private static final String ID = "Elixir";

    public FrameworkDetector() {
        super(ID);
    }

    @NotNull
    @Override
    public FacetType<Facet, Configuration> getFacetType() {
        return FacetType.findInstance(Type.class);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ElixirScriptFileType.INSTANCE;
    }

    @NotNull
    @Override
    public ElementPattern<FileContent> createSuitableFilePattern() {
        return FileContentPattern.fileContent().withName("mix.exs");
    }
}
