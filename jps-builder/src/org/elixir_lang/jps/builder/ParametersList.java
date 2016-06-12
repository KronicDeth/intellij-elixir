/*
 * Copyright 2016 Luke Imhoff
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.elixir_lang.jps.builder;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathMacros;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.EnvironmentUtil;
import com.intellij.util.execution.ParametersListUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ParametersList implements Cloneable {
    private static final Logger LOG = Logger.getInstance("#org.elixir_lang.jps.builder.ParametersList");

    private List<String> myParameters = new ArrayList<String>();
    private Map<String, String> myMacroMap = null;
    private List<ParamsGroup> myGroups = new ArrayList<ParamsGroup>();

    @NotNull
    public String[] getArray() {
        return ArrayUtil.toStringArray(getList());
    }

    @NotNull
    public List<String> getList() {
        if (myGroups.isEmpty()) {
            return Collections.unmodifiableList(myParameters);
        }

        final List<String> params = new ArrayList<String>();
        params.addAll(myParameters);
        for (ParamsGroup group : myGroups) {
            params.addAll(group.getParameters());
        }
        return Collections.unmodifiableList(params);
    }

    public void add(@NonNls final String parameter) {
        myParameters.add(expandMacros(parameter));
    }

    public List<String> getParameters() {
        return Collections.unmodifiableList(myParameters);
    }

    public void set(int ind, final @NonNls String value) {
        myParameters.set(ind, value);
    }

    public String get(int ind) {
        return myParameters.get(ind);
    }

    public void add(@NonNls final String name, @NonNls final String value) {
        add(name);
        add(value);
    }

    public void addAll(final String... parameters) {
        addAll(Arrays.asList(parameters));
    }

    public void addAll(final List<String> parameters) {
        // Don't use myParameters.addAll(parameters) , it does not call expandMacros(parameter)
        for (String parameter : parameters) {
            add(parameter);
        }
    }

    @Override
    public ParametersList clone() {
        try {
            final ParametersList clone = (ParametersList)super.clone();
            clone.myParameters = new ArrayList<String>(myParameters);
            clone.myGroups = new ArrayList<ParamsGroup>(myGroups.size() + 1);
            for (ParamsGroup group : myGroups) {
                clone.myGroups.add(group.clone());
            }
            return clone;
        }
        catch (CloneNotSupportedException e) {
            LOG.error(e);
            return null;
        }
    }

    /**
     * @see ParametersListUtil#join(java.util.List)
     */
    @NotNull
    public static String join(@NotNull final List<String> parameters) {
        return ParametersListUtil.join(parameters);
    }

    /**
     * @see ParametersListUtil#parseToArray(String)
     */
    @NotNull
    public static String[] parse(@NotNull final String string) {
        return ParametersListUtil.parseToArray(string);
    }

    public String expandMacros(String text) {
        final Map<String, String> macroMap = getMacroMap();
        final Set<String> set = macroMap.keySet();
        for (final String from : set) {
            final String to = macroMap.get(from);
            text = StringUtil.replace(text, from, to, true);
        }
        return text;
    }

    private Map<String, String> getMacroMap() {
        if (myMacroMap == null) {
            // the insertion order is important for later iterations, so LinkedHashMap is used
            myMacroMap = new LinkedHashMap<String, String>();

            // ApplicationManager.getApplication() will return null if executed in ParameterListTest
            final Application application = ApplicationManager.getApplication();
            if (application != null) {
                final PathMacros pathMacros = PathMacros.getInstance();
                if (pathMacros != null) {
                    for (String name : pathMacros.getUserMacroNames()) {
                        final String value = pathMacros.getValue(name);
                        if (value != null) {
                            myMacroMap.put("${" + name + "}", value);
                        }
                    }
                }
                final Map<String, String> env = EnvironmentUtil.getEnvironmentMap();
                for (String name : env.keySet()) {
                    final String key = "${" + name + "}";
                    if (!myMacroMap.containsKey(key)) {
                        myMacroMap.put(key, env.get(name));
                    }
                }
            }
        }
        return myMacroMap;
    }

    @Override
    public String toString() {
        return myParameters.toString();
    }

}
