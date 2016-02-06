package org.elixir_lang.structure_view.sorter;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.structure_view.element.Timed;
import org.elixir_lang.structure_view.element.Visible;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Comparator;

/**
 * Sorts element by their {@code #time()}, which indicates whether the element works at
 */
public class Visibility implements Sorter {
    /*
     * CONSTANTS
     */

    public static final Sorter INSTANCE = new Visibility();

    @NonNls
    public static final String VISIBILITY_SORTER_ID = "VISIBILITY_COMPARATOR";

    /*
     * Constructors
     */

    /**
     * Returns the comparator used for comparing nodes in the tree.
     *
     * @return the comparator for comparing nodes.
     */
    @Override
    public Comparator getComparator() {
        return new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                int comparison = 2;
                Visible.Visibility visibility1 = null;
                Visible.Visibility visibility2 = null;

                if (o1 instanceof Visible) {
                    Visible visible1 = (Visible) o1;
                    visibility1 = visible1.visibility();
                }

                if (o2 instanceof Visible) {
                    Visible visible2 = (Visible) o2;
                    visibility2 = visible2.visibility();
                }

                if (visibility1 == visibility2) {
                    comparison = 0;
                } else if (visibility1 == Visible.Visibility.PUBLIC) {
                    comparison = -1;
                } else if (visibility1 == null) {
                    if (visibility2 == Visible.Visibility.PUBLIC) {
                        comparison = 1;
                    } else if (visibility2 == Visible.Visibility.PRIVATE) {
                        comparison = -1;
                    }
                } else if (visibility1 == Visible.Visibility.PRIVATE) {
                    comparison = 1;
                }

                assert comparison != 2;

                return comparison;
            }
        };
    }

    /*
     * Instance Methods
     */

    /**
     * Returns a unique identifier for the action.
     *
     * @return the action identifier.
     */
    @NotNull
    @Override
    public String getName() {
        return VISIBILITY_SORTER_ID;
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
                "Sort by Visibility",
                "Sort into public, unknown, and private",
                AllIcons.ObjectBrowser.VisibilitySort
        );
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
