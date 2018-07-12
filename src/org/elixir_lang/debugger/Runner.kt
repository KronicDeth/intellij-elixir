/*
 * Copyright 2012-2014 Sergey Ignatov
 * Copyright 2017 Jake Becker
 * Copyright 2018 Luke Imhoff
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

package org.elixir_lang.debugger

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.xdebugger.XDebugProcessStarter
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager

class Runner : GenericProgramRunner<RunnerSettings>() {
    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor =
            XDebuggerManager
                    .getInstance(environment.project)
                    .startSession(
                            environment,
                            object : XDebugProcessStarter() {
                                override fun start(session: XDebugSession): com.intellij.xdebugger.XDebugProcess =
                                        Process(session, environment)
                            }
                    )
                    .runContentDescriptor

    override fun getRunnerId(): String = ELIXIR_DEBUG_RUNNER_ID

    override fun canRun(executorId: String, profile: RunProfile): Boolean =
            DefaultDebugExecutor.EXECUTOR_ID == executorId &&
                    (profile is org.elixir_lang.elixir.Configuration ||
                            profile is org.elixir_lang.exunit.Configuration ||
                            profile is org.elixir_lang.iex.Configuration ||
                            profile is org.elixir_lang.mix.Configuration)
}

private const val ELIXIR_DEBUG_RUNNER_ID = "ElixirDebugRunner"
