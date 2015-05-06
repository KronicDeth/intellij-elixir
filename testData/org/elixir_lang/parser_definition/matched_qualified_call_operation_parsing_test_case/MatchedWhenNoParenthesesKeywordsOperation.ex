One.relative_identifier(
  one when one: 1
) when two: Two.relative_identifier(
  three when three: 3
)
One.relative_identifier(
  one when one: 1
)(
  two when two: 2
) when three: Two.relative_identifier(
  four when four: 4
)(
  five when five: 5
)
One.relative_identifier key: one when one: 1
One.relative_identifier unqualified 1,
                                    key: two when two: 2
One.relative_identifier one,
                        key: two when two: 2