# matchedExpressionInMatchOperation
function positional, keyword_key: keyword_value \\ nil

# matchedExpressionWhenOperation
function positional, keyword_key: keyword_value when guard

# matchedExpressionTypeOperation
function positional, keyword_key: keyword_value :: guard

# matchedExpressionPipeOperation
function positional, keyword_key: keyword_value | update

# matchedExpressionMatchOperation
function positional, keyword_key: keyword_value = match_value

# matchedExpressionOrOperation
function positional, keyword_key: first_value or second_value
function positional, keyword_key: first_value ||| second_value

# matchedExpressionAndOperation
function positional, keyword_key: first_value and second_value
function positional, keyword_key: first_value &&& second_value

# matchedExpressionComparisonOperation
function positional, keyword_key: first_value == second_value

# matchedExpressionRelationalOperation
function positional, keyword_key: first_value > second_value

# matchedExpressionArrowOperation
first_function positional, keyword_key: first_value |> second_function

# matchedExpressionInOperation
function positional, keyword_key: value in array_or_range

# matchedExpressionTwoOperation
function positional, keyword_key: first_list ++ second_list

# matchedExpressionAdditionOperation
function positional, keyword_key: first_value + second_value
