((unquote_splicing([1, 2, 3])) -> :ok )
((1, unquote_splicing([2, 3])) -> :ok )

# same as above, just for no parentheses
( unquote_splicing([1, 2, 3]) -> :ok )
( 1, unquote_splicing([2, 3]) -> :ok )
