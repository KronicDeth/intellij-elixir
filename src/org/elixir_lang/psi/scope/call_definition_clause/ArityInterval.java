package org.elixir_lang.psi.scope.call_definition_clause;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.ResolveState;
import gnu.trove.THashMap;
import org.apache.commons.lang.math.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static org.elixir_lang.psi.call.name.Module.KERNEL_SPECIAL_FORMS;
import static org.elixir_lang.psi.scope.CallDefinitionClause.MODULAR_CANONICAL_NAME;

/**
 * While an arity range for a normal function or macro is represented as an
 * {@link org.apache.commons.lang.math.IntRange}, some special forms have no fixed arity when used because although
 * defined in {@code Kernel.SpecialForms} as 1-arity, that one argument is effectively a splat of all the arguments, so
 * there needs to be a way to represent that half-open interval.
 */
class ArityInterval {
    /*
     * CONSTANTS
     */

    private static final ArityInterval ONE = new ArityInterval(1);
    private static final Map<String, ArityInterval> KERNEL_SPECIAL_FORM_ARITY_INTERVAL_BY_NAME =
            new THashMap<String, ArityInterval>();

    static {
        KERNEL_SPECIAL_FORM_ARITY_INTERVAL_BY_NAME.put("__aliases__", ONE);
        KERNEL_SPECIAL_FORM_ARITY_INTERVAL_BY_NAME.put("__block__", ONE);
        KERNEL_SPECIAL_FORM_ARITY_INTERVAL_BY_NAME.put("for", ONE);
        KERNEL_SPECIAL_FORM_ARITY_INTERVAL_BY_NAME.put("with", ONE);
    }

    /*
     * Static Methods
     */

    static ArityInterval arityInterval(@NotNull Pair<String, IntRange> nameArityRange,
                                       @NotNull ResolveState resolveState) {
        @Nullable String modularCanonicalName = resolveState.get(MODULAR_CANONICAL_NAME);

        IntRange arityRange = nameArityRange.second;
        int arityRangeMinimum = arityRange.getMinimumInteger();
        int arityRangeMaximum = arityRange.getMaximumInteger();
        ArityInterval arityInterval = null;

        if (modularCanonicalName != null && modularCanonicalName.equals(KERNEL_SPECIAL_FORMS)) {
            String name = nameArityRange.first;

            arityInterval = KERNEL_SPECIAL_FORM_ARITY_INTERVAL_BY_NAME.get(name);
        }

        if (arityInterval == null) {
            arityInterval = new ArityInterval(arityRangeMinimum, arityRangeMaximum);
        }

        return arityInterval;
    }

    /*
     * Fields
     */

    @NotNull
    private final Integer minimum;
    @Nullable
    private final Integer maximum;

    /*
     * Constructors
     */

    /**
     * Unlike {@link org.apache.commons.lang.math.IntRange#IntRange(int)}}, where the argument becomes the minimum AND
     * the maximum, here the argument is ONLY the minimum and the interval has an infinite maximum.
     *
     * @param minimum minimum arity
     */
    private ArityInterval(int minimum) {
        this.minimum = minimum;
        this.maximum = null;
    }

    private ArityInterval(int minimum, int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    /*
     * Instance Methods
     */

    boolean containsInteger(int candidate) {
        return minimum <= candidate && (maximum == null || candidate <= maximum);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("ArityInterval(").append(minimum);

        if (maximum != null) {
            stringBuilder.append(", ").append(maximum);
        }

        stringBuilder.append(")");

        return stringBuilder.toString();
    }
}
