package org.elixir_lang.eex;


import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

// See https://github.com/JetBrains/intellij-plugins/blob/500f42337a87f463e0340f43e2411266fcfa9c5f/handlebars/src/com/dmarcotte/handlebars/parsing/HbElementType.java
public class ElementType extends IElementType {
    public ElementType(@NotNull String debugName) {
        super(debugName, Language.INSTANCE);
    }
}
