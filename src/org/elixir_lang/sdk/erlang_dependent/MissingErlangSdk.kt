package org.elixir_lang.sdk.erlang_dependent

import com.intellij.openapi.projectRoots.Sdk

class MissingErlangSdk(elixirSdk: Sdk) : Exception("Elixir SDK `${elixirSdk.name}` is missing Erlang SDK")
