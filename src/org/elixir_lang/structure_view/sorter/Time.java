package org.elixir_lang.structure_view.sorter;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import org.elixir_lang.structure_view.element.Timed;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Comparator;

/**
 * Sorts element by their {@code #time()}, which indicates whether the element works at
 */
public class Time implements Sorter {
    /*
     * CONSTANTS
     */

    public static final Sorter INSTANCE = new Time();

    @NonNls
    public static final String TIME_SORTER_ID = "TIME_COMPARATOR";

    /*
     * Private
     */

    /**
     * Maps an element to a sort rank based on its {@link Timed.Time}:
     * <ul>
     *   <li>{@code -1} - compile-time ({@link Timed.Time#COMPILE})</li>
     *   <li>{@code  0} - not {@link Timed} (no time classification)</li>
     *   <li>{@code  1} - run-time ({@link Timed.Time#RUN})</li>
     * </ul>
     */
    private static int timeRank(Object o) {
        if (o instanceof Timed timed) {
            return switch (timed.time()) {
                case COMPILE -> -1;
                case RUN -> 1;
                // default branch is required: without it, javac emits an implicit reference to
                // java.lang.MatchException which the IntelliJ Plugin Verifier cannot resolve.
                //noinspection UnnecessaryDefault
                default -> 0;
            };
        }
        return 0;
    }

    /*
     * Instance Methods
     */

    /**
     * Returns the comparator used for comparing nodes in the tree.
     *
     * @return the comparator for comparing nodes.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Comparator getComparator() {
        return Comparator.comparingInt(Time::timeRank);
    }

    /**
     * Returns a unique identifier for the action.
     *
     * @return the action identifier.
     */
    @NotNull
    @Override
    public String getName() {
        return TIME_SORTER_ID;
    }

    /**
     * Returns the presentation for the action.
     *
     * @return the action presentation.
     * @see ActionPresentationData#ActionPresentationData(String, String, Icon)
     */
    @NotNull
    @Override
    public ActionPresentation getPresentation() {
        return new ActionPresentationData(
                "Sort by Time",
                "Sort into Compile Time, mixed, and Runtime groups",
                // TODO make an icon that is arrow + (compile + run) on side like SortByType does with type symbols
                AllIcons.ObjectBrowser.SortByType
        );
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
