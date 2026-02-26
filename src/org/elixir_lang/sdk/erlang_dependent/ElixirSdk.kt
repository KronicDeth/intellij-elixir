@file:JvmName("ElixirSdkExtensions")
package org.elixir_lang.sdk.erlang_dependent

import com.intellij.openapi.projectRoots.Sdk

val Sdk.elixirAdditionalData: SdkAdditionalData?
    get() = sdkAdditionalData as? SdkAdditionalData

fun Sdk.getErlangSdk(): Sdk? = ErlangSdkResolver.getInstance().resolveErlangSdk(this)
