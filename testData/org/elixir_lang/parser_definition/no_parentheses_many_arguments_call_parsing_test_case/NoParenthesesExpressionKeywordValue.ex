# A noParenthesesExpression can't itself have a noParenthesesExpression as a keyword value according to
# elixir_parser.yrl, but it is allowed so that {@link: org.elixir_lang.inspection.NoParenthesesManyStrict} can mark it
# as an error
first_function first_positional,
               # `second_function second_position, ambiguous_keyword_key: ambigious_keyword_value` is the errant
               # nested noParenthesesExpression, but the actual error will appear on the first `,`.
               first_keyword_key: second_function second_positional,
                  ambiguous_keyword_key: ambiguous_keyword_value
