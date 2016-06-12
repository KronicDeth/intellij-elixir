/*
 * Copyright 2016 Luke Imhoff
 * Copyright 2000-2014 JetBrains s.r.o.
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

import com.intellij.execution.CommandLineUtil;
import com.intellij.ide.IdeBundle;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.EnvironmentUtil;
import com.intellij.util.PlatformUtils;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.text.CaseInsensitiveStringHashingStrategy;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * OS-independent way of executing external processes with complex parameters.
 * <p/>
 * Main idea of the class is to accept parameters "as-is", just as they should look to an external process, and quote/escape them
 * as required by the underlying platform.
 *
 * @see com.intellij.execution.process.OSProcessHandler
 */
public class GeneralCommandLine implements UserDataHolder {
    private static final Logger LOG = Logger.getInstance("#org.elixir_lang.jps.builders.GeneralCommandLine");

    private String myExePath = null;
    private File myWorkDirectory = null;
    private final Map<String, String> myEnvParams = new MyTHashMap();
    private final ParametersList myProgramParams = new ParametersList();
    private Map<Object, Object> myUserData = null;

    public GeneralCommandLine() { }

    @SuppressWarnings("unused")
    public String getExePath() {
        return myExePath;
    }

    public void setExePath(@NotNull @NonNls final String exePath) {
        myExePath = exePath.trim();
    }

    public File getWorkDirectory() {
        return myWorkDirectory;
    }

    @NotNull
    public GeneralCommandLine withWorkDirectory(@Nullable final String path) {
        return withWorkDirectory(path != null ? new File(path) : null);
    }

    @NotNull
    public GeneralCommandLine withWorkDirectory(@Nullable final File workDirectory) {
        myWorkDirectory = workDirectory;
        return this;
    }

    /**
     * Note: the map returned is forgiving to passing null values into putAll().
     */
    @NotNull
    public Map<String, String> getEnvironment() {
        return myEnvParams;
    }

    /**
     * @deprecated use {@link #getEnvironment()} (to remove in IDEA 14)
     */
    @SuppressWarnings("unused")
    public Map<String, String> getEnvParams() {
        return getEnvironment();
    }

    /**
     * @deprecated use {@link #getEnvironment()} (to remove in IDEA 14)
     */
    @SuppressWarnings("unused")
    public void setEnvParams(@Nullable Map<String, String> envParams) {
        myEnvParams.clear();
        if (envParams != null) {
            myEnvParams.putAll(envParams);
        }
    }

    /**
     * @return unmodifiable map of the parent environment, that will be passed to the process if isPassParentEnvironment() == true
     */
    @NotNull
    public Map<String, String> getParentEnvironment() {
        return PlatformUtils.isAppCode() ? System.getenv() // Temporarily fix for OC-8606
                : EnvironmentUtil.getEnvironmentMap();
    }

    public void addParameters(final String... parameters) {
        for (String parameter : parameters) {
            addParameter(parameter);
        }
    }

    public void addParameters(@NotNull final List<String> parameters) {
        for (final String parameter : parameters) {
            addParameter(parameter);
        }
    }

    public void addParameter(@NotNull @NonNls final String parameter) {
        myProgramParams.add(parameter);
    }

    @SuppressWarnings("unused")
    public ParametersList getParametersList() {
        return myProgramParams;
    }

    /**
     * Returns string representation of this command line.<br/>
     * Warning: resulting string is not OS-dependent - <b>do not</b> use it for executing this command line.
     *
     * @return single-string representation of this command line.
     */
    public String getCommandLineString() {
        return getCommandLineString(null);
    }

    /**
     * Returns string representation of this command line.<br/>
     * Warning: resulting string is not OS-dependent - <b>do not</b> use it for executing this command line.
     *
     * @param exeName use this executable name instead of given by {@link #setExePath(String)}
     * @return single-string representation of this command line.
     */
    public String getCommandLineString(@Nullable final String exeName) {
        return ParametersList.join(getCommandLineList(exeName));
    }

    public List<String> getCommandLineList(@Nullable final String exeName) {
        final List<String> commands = new ArrayList<String>();
        if (exeName != null) {
            commands.add(exeName);
        }
        else if (myExePath != null) {
            commands.add(myExePath);
        }
        else {
            commands.add("<null>");
        }
        commands.addAll(myProgramParams.getList());
        return commands;
    }

    @SuppressWarnings("UnresolvedPropertyKey")
    @NotNull
    public Process createProcess() throws ExecutionException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing [" + getCommandLineString() + "]");
        }

        List<String> commands;
        try {
            checkWorkingDirectory();

            if (StringUtil.isEmptyOrSpaces(myExePath)) {
                throw new ExecutionException(IdeBundle.message("run.configuration.error.executable.not.specified"));
            }

            commands = CommandLineUtil.toCommandLine(myExePath, myProgramParams.getList());
        }
        catch (ExecutionException e) {
            LOG.info(e);
            throw e;
        }

        try {
            return startProcess(commands);
        }
        catch (IOException e) {
            LOG.info(e);
            throw new ProcessNotCreatedException(e.getMessage(), e, this);
        }
    }

    @NotNull
    protected Process startProcess(@NotNull List<String> commands) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(commands);
        setupEnvironment(builder.environment());
        builder.directory(myWorkDirectory);
        builder.redirectErrorStream(false);
        return builder.start();
    }

    @SuppressWarnings("UnresolvedPropertyKey")
    private void checkWorkingDirectory() throws ExecutionException {
        if (myWorkDirectory == null) {
            return;
        }
        if (!myWorkDirectory.exists()) {
            throw new ExecutionException(
                    IdeBundle.message("run.configuration.error.working.directory.does.not.exist", myWorkDirectory.getAbsolutePath()));
        }
        if (!myWorkDirectory.isDirectory()) {
            throw new ExecutionException(IdeBundle.message("run.configuration.error.working.directory.not.directory"));
        }
    }

    protected void setupEnvironment(@NotNull Map<String, String> environment) {
        environment.clear();
        environment.putAll(getParentEnvironment());

        if (!myEnvParams.isEmpty()) {
            if (SystemInfo.isWindows) {
                THashMap<String, String> envVars = new THashMap<String, String>(CaseInsensitiveStringHashingStrategy.INSTANCE);
                envVars.putAll(environment);
                envVars.putAll(myEnvParams);
                environment.clear();
                environment.putAll(envVars);
            }
            else {
                environment.putAll(myEnvParams);
            }
        }
    }

    @Override
    public String toString() {
        return myExePath + " " + myProgramParams;
    }

    @Override
    public <T> T getUserData(@NotNull final Key<T> key) {
        if (myUserData != null) {
            @SuppressWarnings({"UnnecessaryLocalVariable", "unchecked"}) final T t = (T)myUserData.get(key);
            return t;
        }
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull final Key<T> key, @Nullable final T value) {
        if (myUserData == null) {
            myUserData = ContainerUtil.newHashMap();
        }
        myUserData.put(key, value);
    }

    private static class MyTHashMap extends THashMap<String, String> {
        @Override
        public String put(String key, String value) {
            if (key == null || value == null) {
                LOG.error(new Exception("Nulls are not allowed"));
                return null;
            }
            if (key.isEmpty()) {
                // Windows: passing an environment variable with empty name causes "CreateProcess error=87, The parameter is incorrect"
                LOG.warn("Skipping environment variable with empty name, value: " + value);
                return null;
            }
            return super.put(key, value);
        }

        @Override
        public void putAll(Map<? extends String, ? extends String> map) {
            if (map != null) {
                super.putAll(map);
            }
        }
    }
}
