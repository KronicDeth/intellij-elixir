/*
 * Copyright 2016 Luke Imhoff
 * Copyright 2000-2009 JetBrains s.r.o.
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

public class ProcessNotCreatedException extends ExecutionException {
    private final GeneralCommandLine myCommandLine;

    public ProcessNotCreatedException(final String s, final Throwable cause, final GeneralCommandLine commandLine) {
        super(s, cause);
        myCommandLine = commandLine;
    }

    @SuppressWarnings("unused")
    public GeneralCommandLine getCommandLine() {
        return myCommandLine;
    }
}
