package org.elixir_lang;

import com.google.common.base.CaseFormat;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ElementTypeFactory {
    /*
     * Static Methods
     */

    @NotNull
    public static IElementType factory(@NotNull String name) {
        return factory("org.elixir_lang.psi.stub.type", name);
    }

    @NotNull
    public static IElementType factory(@NotNull String packageName, @NotNull String name) {
        String relativeClassName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
        Class<?> clazz = null;

        try {
            clazz = Class.forName(packageName + "." + relativeClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        IElementType type = null;

        if (clazz != null) {
            Constructor<?> constructor = null;

            try {
                constructor = clazz.getConstructor(String.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            if (constructor != null) {
                try {
                    type = (IElementType) constructor.newInstance(name);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        //noinspection ConstantConditions
        return type;
    }

    /*
     * Constructors
     */

    private ElementTypeFactory() {
    }
}
