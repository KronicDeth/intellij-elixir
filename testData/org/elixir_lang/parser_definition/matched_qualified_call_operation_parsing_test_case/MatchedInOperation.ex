One.relative_identifier(
  1 in 2,
  key: 3 in 4
) in Two.relative_identifier(
  2 in 1,
  key: 4 in 3
)
One.relative_identifier(
  1 in 2,
  key: 3 in 4
)(
  4 in 5,
  key: 6 in 7
) in Two.relative_identifier(
  2 in 1,
  key: 4 in 3
)(
  5 in 4,
  key: 7 in 6
)
One.relative_identifier key: 1 in 2
One.relative_identifier unqualified 1 in 2,
                                    key: 3 in 4
One.relative_identifier 1 in 2,
                        key: 3 in 4