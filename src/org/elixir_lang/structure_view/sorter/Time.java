package org.elixir_lang.structure_view.sorter;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import org.apache.commons.lang.NotImplementedException;
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
                int comparison;

                if (o1 instanceof Timed && o2 instanceof Timed) {
                    Timed timed1 = (Timed) o1;
                    Timed timed2 = (Timed) o2;

                    Timed.Time time1 = timed1.time();
                    Timed.Time time2 = timed2.time();

                    if (time1 == time2) {
                        comparison = 0;
                    } else if (time1 == Timed.Time.COMPILE && time2 == Timed.Time.RUN) {
                        comparison = -1;
                    } else if (time1 == Timed.Time.RUN && time2 == Timed.Time.COMPILE) {
                        comparison = 1;
                    } else {
                        throw new NotImplementedException("Only COMPILE and RUN time are expected");
                    }
                } else if (o1 instanceof Timed && !(o2 instanceof Timed)) {
                    Timed timed1 = (Timed) o1;
                    Timed.Time time1 = timed1.time();

                    switch (time1) {
                        case COMPILE:
                            comparison = -1;
                            break;
                        case RUN:
                            comparison = 1;
                            break;
                        default:
                            throw new NotImplementedException("Only COMPILE and RUN time are expected");
                    }
                } else if (!(o1 instanceof Timed) && o2 instanceof Timed) {
                    Timed timed2 = (Timed) o2;
                    Timed.Time time2 = timed2.time();

                    switch (time2) {
                        case COMPILE:
                            comparison = 1;
                            break;
                        case RUN:
                            comparison = -1;
                            break;
                        default:
                            throw new NotImplementedException("Only COMPILE and RUN time are expected");
                    }
                } else {
                    assert !(o1 instanceof Timed) && !(o2 instanceof Timed);

                    comparison = 0;
                }

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
