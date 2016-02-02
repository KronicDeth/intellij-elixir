package org.elixir_lang.navigation.item_presentation;

import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Parent {
    /**
     * Combines {@link #getLocationString()} with {@link #getPresentableText()} for when this is the parent of
     * an {@link com.intellij.navigation.ItemPresentation} and needs to act as the
     * {@link ItemPresentation#getLocationString()}.
     *
     * @return {@link #getLocationString()} + "." + {@link #getPresentableText()} if {@link #getLocationString()} is not
     *   {@code null}; otherwise, {@link #getPresentableText()}.
     */
    @Contract(pure = true)
    @NotNull
    String getLocatedPresentableText();

    /**
     *
     * @return
     * @see ItemPresentation#getLocationString()
     */
    @Nullable
    String getLocationString();

    /**
     *
     * @return
     * @see ItemPresentation#getPresentableText()
     */
    @NotNull
    String getPresentableText();
}
