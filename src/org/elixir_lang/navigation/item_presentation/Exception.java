package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.psi.ElixirAtom;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Exception implements ItemPresentation {
    /*
     * Fields
     */

    @NotNull
    private final Map<PsiElement, PsiElement> defaultValueElementByKeyElement;
    @NotNull
    private final Module module;

    /*
     * Constructors
     */

    public Exception(@NotNull Module module, @NotNull Map<PsiElement, PsiElement> defaultValueElementByKeyElement) {
        this.defaultValueElementByKeyElement = defaultValueElementByKeyElement;
        this.module = module;
    }

    /*
     * Instance Methods
     */

    /**
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    @NotNull
    @Override
    public Icon getIcon(boolean unused) {
        return ElixirIcons.EXCEPTION;
    }

    /**
     * Returns the location of the object (for example, the package of a class). The location
     * string is used by some renderers and usually displayed as grayed text next to the item name.
     *
     * @return the location description, or null if none is applicable.
     */
    @Nullable
    @Override
    public String getLocationString() {
        String locationString = null;

        if (module != null) {
            String moduleLocationString = module.getLocationString();
            String modulePresentableText = module.getPresentableText();

            if (moduleLocationString != null) {
                locationString = moduleLocationString + "." + modulePresentableText;
            } else {
                locationString = modulePresentableText;
            }
        }

        return locationString;
    }

    /**
     * Returns the name of the object to be presented in most renderers across the program.
     *
     * @return the object name.
     */
    @Nullable
    @Override
    public String getPresentableText() {
        SortedMap<String, String> defaultValueNameByKeyName = new TreeMap<String, String>();

        for (Map.Entry<PsiElement, PsiElement> entry : defaultValueElementByKeyElement.entrySet()) {
            PsiElement keyElement = entry.getKey();
            String keyName;

            if (keyElement instanceof ElixirAtom) {
                ElixirAtom atom = (ElixirAtom) keyElement;
                String atomText = atom.getText();
                keyName = atomText.substring(1);
            } else {
                keyName = keyElement.getText();
            }

            PsiElement valueElement = entry.getValue();
            String valueName;

            if (valueElement != null) {
                valueName = valueElement.getText();
            } else {
                valueName = "nil";
            }

            defaultValueNameByKeyName.put(keyName, valueName);
        }

        StringBuilder presentableTextBuilder = new StringBuilder("defexception [");

        boolean first = true;

        for (Map.Entry<String, String> entry : defaultValueNameByKeyName.entrySet()) {
            if (first) {
                first = false;
            } else {
                presentableTextBuilder.append(", ");
            }

            presentableTextBuilder.append(entry.getKey());
            presentableTextBuilder.append(": ");
            presentableTextBuilder.append(entry.getValue());
        }

        presentableTextBuilder.append("]");

        return presentableTextBuilder.toString();
    }
}
