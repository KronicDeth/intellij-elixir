One.relative_identifier(
  1 when 2,
  key: 3 when 4
) when Two.relative_identifier(
  2 when 1,
  key: 4 when 3
)
One.relative_identifier(
  1 when 2,
  key: 3 when 4
)(
  4 when 5,
  key: 6 when 7
) when Two.relative_identifier(
  2 when 1,
  key: 4 when 3
)(
  5 when 4,
  key: 7 when 6
)
One.relative_identifier key: 1 when 2
One.relative_identifier unqualified 1 when 2,
                                    key: 3 when 4
One.relative_identifier 1 when 2,
                        key: 3 when 4