package org.elixir_lang.templates;

import com.intellij.ide.fileTemplates.DefaultCreateFromTemplateHandler;
import com.intellij.ide.fileTemplates.FileTemplate;
import org.elixir_lang.ElixirFileType;

public class ElixirCreateFromTemplateHandler extends DefaultCreateFromTemplateHandler {
    public ElixirCreateFromTemplateHandler() {
        super();
    }

    @Override
    public boolean handlesTemplate(FileTemplate fileTemplate) {
        return fileTemplate.isTemplateOfType(ElixirFileType.INSTANCE);
    }
}
