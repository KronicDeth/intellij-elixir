@one.relative_identifier(
  @two,
  key: @three
)
@one.relative_identifier(
  @two,
  key: @three
)(
  @four,
  key: @five
)
@one.relative_identifier key: @two
@one.relative_identifier unqualified @two,
                                     key: @three
@one.relative_identifier @two,
                         key: @three