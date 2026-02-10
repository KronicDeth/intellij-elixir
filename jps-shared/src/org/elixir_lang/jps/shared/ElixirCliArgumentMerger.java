package org.elixir_lang.jps.shared;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ElixirCliArgumentMerger {
    private ElixirCliArgumentMerger() {
    }

    @Nullable
    public static List<String> mergeArguments(@NotNull List<String> baseArguments,
                                              @NotNull List<String> erlArgumentList) {
        int extraIndex = baseArguments.indexOf("-extra");
        if (extraIndex == -1) {
            return null;
        }

        List<String> adjustedBaseArguments = new ArrayList<>(baseArguments);
        if (shouldAddAnsiEnabled(baseArguments, erlArgumentList)) {
            adjustedBaseArguments.addAll(extraIndex, Arrays.asList("-elixir", "ansi_enabled", "true"));
        }

        List<String> merged = new ArrayList<>(erlArgumentList.size() + adjustedBaseArguments.size());
        merged.addAll(erlArgumentList);
        merged.addAll(adjustedBaseArguments);
        return merged;
    }

    private static boolean shouldAddAnsiEnabled(@NotNull List<String> baseArguments,
                                                @NotNull List<String> erlArgumentList) {
        List<String> ansiEnabled = Arrays.asList("-elixir", "ansi_enabled", "true");
        return lacksSequence(baseArguments, ansiEnabled) && lacksSequence(erlArgumentList, ansiEnabled);
    }

    private static boolean lacksSequence(@NotNull List<String> tokens, @NotNull List<String> sequence) {
        if (sequence.isEmpty() || sequence.size() > tokens.size()) {
            return true;
        }
        for (int i = 0; i <= tokens.size() - sequence.size(); i++) {
            if (tokens.subList(i, i + sequence.size()).equals(sequence)) {
                return false;
            }
        }
        return true;
    }
}
