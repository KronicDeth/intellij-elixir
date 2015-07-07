/*
 * Copyright 2012-2014 Sergey Ignatov
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

package org.elixir_lang.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.*;

public final class ExtProcessUtil {
  private ExtProcessUtil() {
  }

  @NotNull
  public static ExtProcessOutput execAndGetFirstLine(long timeout, @NotNull String... command) {
    try {
      final Process cmdRunner = new GeneralCommandLine(command).createProcess();
      ExecutorService singleTreadExecutor = Executors.newSingleThreadExecutor();
      try {
        Future<ExtProcessOutput> cmdRunnerFuture = singleTreadExecutor.submit(new Callable<ExtProcessOutput>() {
          @Override
          public ExtProcessOutput call() throws Exception {
            cmdRunner.waitFor();
            String stdOut = readLine(cmdRunner.getInputStream());
            String stdErr = readLine(cmdRunner.getErrorStream());
            return new ExtProcessOutput(stdOut, stdErr);
          }
        });

        try {
          return cmdRunnerFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) { // Suppress
        }

        cmdRunnerFuture.cancel(true);
      } finally {
        singleTreadExecutor.shutdown();
      }
    } catch (ExecutionException e) { // Suppress
    }
    return new ExtProcessOutput("", "");
  }

  @NotNull
  private static String readLine(InputStream stream) {
    BufferedReader errReader = new BufferedReader(new InputStreamReader(stream));
    try {
      return StringUtil.notNullize(errReader.readLine());
    } catch (IOException ignore) {
    } finally {
      try {
        errReader.close();
      } catch (IOException ignore) {
      }
    }
    return "";
  }

  public static class ExtProcessOutput {
    private final String myStdOut;
    private final String myStdErr;

    public ExtProcessOutput(@NotNull String stdOut, @NotNull String stdErr) {
      myStdOut = stdOut;
      myStdErr = stdErr;
    }

    @NotNull
    public String getStdOut() {
      return myStdOut;
    }

    @NotNull
    public String getStdErr() {
      return myStdErr;
    }
  }
}
