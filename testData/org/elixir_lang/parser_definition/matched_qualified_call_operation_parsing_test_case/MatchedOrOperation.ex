One.relative_identifier(
  1 || 2,
  key: 3 or 4
) || Two.relative_identifier(
  2 ||| 1,
  key: 4 || 3
)
One.relative_identifier(
  1 or 2,
  key: 3 || 4
)(
  4 ||| 5,
  key: 6 or 7
) or Two.relative_identifier(
  2 || 1,
  key: 4 or 3
)(
  5 ||| 4,
  key: 7 or 6
)
One.relative_identifier key: 1 || 2
One.relative_identifier unqualified 1 || 2,
                                    key: 3 or 4
One.relative_identifier 1 || 2,
                        key: 3 ||| 4
