package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications

class Callbacks(private val callbackList: List<Callback>) {
    operator fun get(index: Int) = callbackList.get(index)
    fun indexOf(callback: Callback): Int = callbackList.indexOf(callback)
    fun size() = callbackList.size
}
