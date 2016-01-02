package org.elixir_lang.psi.module_attribute;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by limhoff on 1/1/16.
 */
public class NamesValidator implements com.intellij.lang.refactoring.NamesValidator {
    /*
     * CONSTANTS
     */

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("@[a-z_][0-9a-zA-z_]*[?!]?");

    /*
     * Public Instance Methods
     */

    /**
     * Checks if the specified string is a keyword in the custom language.
     *
     * @param name    the string to check.
     * @param project the project in the context of which the check is done.
     * @return true if the string is a keyword, false otherwise.
     */
    @Override
    public boolean isKeyword(@NotNull String name, Project project) {
        return false;
    }

    /**
     * Checks if the specified string is a valid identifier in the custom language.
     *
     * @param name    the string to check.
     * @param project the project in the context of which the check is done.
     * @return true if the string is a valid identifier, false otherwise.
     */
    @Override
    public boolean isIdentifier(@NotNull String name, Project project) {
        Matcher matcher = IDENTIFIER_PATTERN.matcher(name);

        return matcher.matches();
    }
}
