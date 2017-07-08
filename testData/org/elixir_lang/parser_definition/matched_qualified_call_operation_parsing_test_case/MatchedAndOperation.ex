One.relative_identifier(
  1 and 2,
  key: 3 && 4
) &&& Two.relative_identifier(
  2 and 1,
  key: 4 && 3
)
One.relative_identifier(
  1 &&& 2,
  key: 3 and 4
)(
  4 && 5,
  key: 6 &&& 7
) and Two.relative_identifier(
  2 and 1,
  key: 4 && 3
)(
  5 &&& 4,
  key: 7 and 6
)
One.relative_identifier key: 1 && 2
One.relative_identifier unqualified 1 &&& 2,
                                    key: 3 and 4
One.relative_identifier 1 && 2,
                        key: 3 &&& 4
