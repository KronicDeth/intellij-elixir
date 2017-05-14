@one.()
@one.(function positional, key: value)
@one.(key_one: value_one, key_two: value_two)
@one.(
  &one,
  one <- two,
  one when two,
  one | two,
  one = two,
  one || two,
  one && two,
  one != two,
  one < two,
  one |> two,
  one in two,
  one ++ two,
  one + two,
  one ^^^ two,
  !one,
  not one,
  one.(),
  Two.Three,
  @one,
  one,
  @1,
  &1,
  !1,
  (;),
  1,
  [],
  "StringLine",
  """
  String
  Heredoc
  """,
  'CharListLine',
  '''
  CharList
  Heredoc
  ''',
  ~x{sigil}modifiers,
  nil,
  :atom,
  Alias
)
@one.(
  one,
  key: value
)
@one.(
  one
)(
  two
)
