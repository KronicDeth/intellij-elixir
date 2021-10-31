package org.elixir_lang.ecto.query

import org.elixir_lang.psi.call.Call

// `Ecto.Query.API` in Elixir
object API: Nested(
        "Ecto.Query.API",
        mapOf(
                "!=" to 2..2,
                "*" to 2..2,
                "+" to 2..2,
                "-" to 2..2,
                "/" to 2..2,
                "<" to 2..2,
                "<=" to 2..2,
                "==" to 2..2,
                ">" to 2..2,
                ">=" to 2..2,
                "ago" to 2..2,
                "all" to 2..2,
                "and" to 2..2,
                "any" to 2..2,
                "as" to 1..1,
                "avg" to 1..1,
                "coalesce" to 2..2,
                "count" to 0..2,
                "date_add" to 3..3,
                "datetime_add" to 3..3,
                "exists" to 1..1,
                "field" to 2..2,
                "filter" to 2..2,
                "from_now" to 2..2,
                "ilike" to 2..2,
                "in" to 2..2,
                "is_nil" to 1..1,
                "json_extract_path" to 2..2,
                "like" to 2..2,
                "map" to 2..2,
                "max" to 1..1,
                "merge" to 2..2,
                "min" to 1..1,
                "not" to 1..1,
                "or" to 2..2,
                "parent_as" to 1..1,
                "struct" to 2..2,
                "sum" to 1..1,
                "type" to 2..2
        )
) {
    override fun hasNameArity(call: Call, functionName: String): Boolean =
        functionName == FRAGMENT || super.hasNameArity(call, functionName)

    private const val FRAGMENT = "fragment"
}
