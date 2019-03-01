package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications

class OptionalCallbacks(private val optionalCallbackList: List<OptionalCallback>) {
    operator fun get(index: Int): OptionalCallback = optionalCallbackList[index]
    fun indexOf(optionalCallback: OptionalCallback): Int = optionalCallbackList.indexOf(optionalCallback)
    fun size(): Int = optionalCallbackList.size
}
