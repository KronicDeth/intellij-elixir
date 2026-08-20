package org.elixir_lang.util

data class AccumulatorContinue<out R>(val accumulator: R, val `continue`: Boolean)
