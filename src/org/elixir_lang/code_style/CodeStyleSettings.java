package org.elixir_lang.code_style;

import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

public class CodeStyleSettings extends CustomCodeStyleSettings {
    public boolean ALIGN_BOOLEAN_OPERANDS = true;
    public boolean ALIGN_PIPE_OPERANDS = true;
    public boolean ALIGN_TWO_OPERANDS = true;
    public boolean ALIGN_TYPE_DEFINITION_TO_RIGHT_OF_OPERATOR = true;
    public int ALIGN_UNMATCHED_CALL_DO_BLOCKS = UnmatchedCallDoBlockAlignment.LINE.value;
    public boolean SPACE_AFTER_CAPTURE_OPERATOR = false;
    public boolean SPACE_AROUND_AND_OPERATORS = true;
    public boolean SPACE_AROUND_ARROW_OPERATORS = true;
    public boolean SPACE_AROUND_ASSOCIATION_OPERATOR = true;
    public boolean SPACE_AROUND_IN_MATCH_OPERATORS = true;
    public boolean SPACE_AROUND_OR_OPERATORS = true;
    public boolean SPACE_AROUND_PIPE_OPERATOR = true;
    public boolean SPACE_AROUND_RANGE_OPERATOR = false;
    public boolean SPACE_AROUND_THREE_OPERATOR = true;
    public boolean SPACE_AROUND_TWO_OPERATORS = true;
    public boolean SPACE_AROUND_TYPE_OPERATOR = true;
    public boolean SPACE_WITHIN_BITS = false;

    public CodeStyleSettings(com.intellij.psi.codeStyle.CodeStyleSettings container) {
        super("ElixirCodeStyleSettings", container);
    }

    public enum UnmatchedCallDoBlockAlignment {
        CALL("Call", 1),
        LINE("Line", 2);

        public static final String[] NAMES = ContainerUtil.map(
                values(),
                (unmatchedCallDoBlockAlignment -> unmatchedCallDoBlockAlignment.name),
                new String[0]
        );
        public static final int[] VALUES;

        static {
            UnmatchedCallDoBlockAlignment[] values = values();

            VALUES = new int[values.length];
            // because generics don't work on primitives, so can't mak with ContainerUtil.map
            VALUES[0] = values[0].value;
            VALUES[1] = values[1].value;
        }

        public final String name;
        public final int value;

        UnmatchedCallDoBlockAlignment(@NotNull final String name, final int value) {
            this.name = name;
            this.value = value;
        }
    }
}
