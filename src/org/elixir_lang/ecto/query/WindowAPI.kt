package org.elixir_lang.ecto.query

// `Ecto.Query.WindowAPI` in Elixir
object WindowAPI : Nested(
        "Ecto.Query.WindowAPI",
        mapOf(
                "avg" to 1..1,
                "count" to 0..1,
                "cume_dist" to 0..0,
                "dense_rank" to 0..0,
                "filter" to 2..2,
                "first_value" to 1..1,
                "lag" to 1..3,
                "last_value" to 1..1,
                "lead" to 1..3,
                "max" to 1..1,
                "min" to 1..1,
                "nth_value" to 2..2,
                "ntile" to 1..1,
                "over" to 1..2,
                "percent_rank" to 0..0,
                "rank" to 0..0,
                "row_number" to 0..0,
                "sum" to 1..1
        )
)
