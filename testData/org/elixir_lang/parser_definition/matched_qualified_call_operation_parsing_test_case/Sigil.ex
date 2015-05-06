~x{one}modifiers.relative_identifier(
  ~c|one|,
  key: ~s/one/
)
~x{one}modifiers.relative_identifier(
  ~c|one|,
  key: ~s/one/
)(
  ~r(.*),
  key: ~w'one two'
)
~x{one}modifiers.relative_identifier key: ~c|one|
~x{one}modifiers.relative_identifier unqualified ~c|one|,
                                    key: ~s/one/
~x{one}modifiers.relative_identifier ~c|one|,
                        key: ~s/one/