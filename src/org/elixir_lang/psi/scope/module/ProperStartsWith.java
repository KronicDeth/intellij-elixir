package org.elixir_lang.psi.scope.module;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;

class ProperStartsWith {
    static Condition<String> properStartsWith(String prefix) {
        return Conditions.and(new StartsWith(prefix), new Longer(prefix.length()));
    }
}
