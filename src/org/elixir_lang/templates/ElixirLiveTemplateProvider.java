package org.elixir_lang.templates;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.Nullable;

public class ElixirLiveTemplateProvider implements DefaultLiveTemplatesProvider {
    public static final String[] TEMPLATE_FILES = {"liveTemplates/Elixir"};

    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return TEMPLATE_FILES;
    }

    @Nullable
    @Override
    public String[] getHiddenLiveTemplateFiles() {
        return ArrayUtil.EMPTY_STRING_ARRAY;
    }
}
