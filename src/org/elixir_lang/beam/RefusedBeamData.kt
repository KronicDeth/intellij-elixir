package org.elixir_lang.beam

/**
 * A BEAM whose contents do not add up - a count or length its chunk cannot hold, a term no real
 * module puts there, a chunk header asking for an allocation no real module needs.
 *
 * Distinct from returning null, which means "there is nothing here" and which callers are entitled
 * to cache as a definitive absence.
 */
class RefusedBeamData(message: String) : Exception(message)
