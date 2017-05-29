# Unmatched At Unqualified No Parentheses Calls

## matched at unqualified no parentheses call
@one @two 3 do
end

## matched dot call
@one two . () do
end

## matched qualified no arguments call
@one Two . three do
end

## matched qualified no parentheses call
@one Two . three 4 do
end

## matched qualified parentheses call
@one Two . three() do
end

## matched unqualified no arguments call
@one two do
end

## matched unqualified no parentheses call
@one two 3 do
end

## matched unqualified parentheses call
@one two() do
end

# Unmatched dot calls

## matched at unqualified no parentheses call
one . (@two 3) do
end

## matched dot call
one . (two . ()) do
end

## matched qualified no arguments call
one . (Two . three) do
end

## matched qualified no parentheses call
one . (Two . three 4) do
end

## matched qualified parentheses call
one . (Two . three()) do
end

## matched unqualified no arguments call
one . (two) do
end

## matched unqualified no parentheses call
one . (two 3) do
end

## matched unqualified parentheses call
one . (two()) do
end

# Unmatched At Unqualified No Arguments Call

One . two do
end

# Unmatched Qualified No Parentheses Calls

## matched at unqualified no parentheses call
One . two @three 4 do
end

## matched dot call
One . two three . () do
end

## matched qualified no arguments call
One . two Three . four do
end

## matched qualified no parentheses call
One . two Three . four 5 do
end

## matched qualified parentheses call
One . two Three . four() do
end

## matched unqualified no arguments call
One . two three do
end

## matched unqualified no parentheses call
One . two three 4 do
end

## matched unqualified parentheses call
One . two three() do
end


# Unmatched Qualified Parentheses Calls

## matched at unqualified no parentheses call
One . two(@three 4) do
end

##(matched) dot call
One . two(three . ()) do
end

## matched qualified no arguments call
One . two(Three . four) do
end

## matched qualified no parentheses call
One . two(Three . four 5) do
end

## matched qualified parentheses call
One . two(Three . four()) do
end

## matched unqualified no arguments call
One . two(three) do
end

## matched unqualified no parentheses call
One . two(three 4) do
end

## matched unqualified parentheses call
One . two(three()) do
end
