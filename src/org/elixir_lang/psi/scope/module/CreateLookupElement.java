package org.elixir_lang.psi.scope.module;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.Function;

class CreateLookupElement implements Function<String, LookupElement> {
    public static final CreateLookupElement INSTANCE = new CreateLookupElement();

    @Override
    public LookupElement fun(String lookupElementName) {
        return LookupElementBuilder.create(lookupElementName);
    }
}
