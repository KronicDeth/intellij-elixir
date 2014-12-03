# matchedExpressionCaptureOperation
identifier &capture, first_keyword_key: first_keyword_value

# matchedExpressionInMatchOperation
identifier first_argument \\ nil, first_keyword_key: first_keyword_value

# matchedExpressionWhenOperation
identifier first_argument when true, first_keyword_key: first_keyword_value

# matchedExpressionPipeOperation
identifier first_argument | update, first_keyword_key: first_keyword_value

# matchedExpressionMatchOperation
identifier first_argument = first_value, first_keyword_key: first_keyword_value

# matchedExpressionOrOperation
identifier first_value || second_value, first_keyword_key: first_keyword_value

# matchedExpressionAndOperation
identifier first_value &&& second_value, first_keyword_key: first_keyword_value

# matchedExpressionComparisonOperation
identifier first_value != second_value, first_keyword_key: first_keyword_value

# matchedExpressionRelationalOperation
identifier first_value < second_value, first_keyword_key: first_keyword_value

# matchedExpressionArrowOperation
identifier first_value |> second_function, first_keyword_key: first_keyword_value

# matchedExpressionInOperation
identifier first_argument in range_or_list, first_keyword_key: first_keyword_value

# matchedExpressionTwoOperation
identifier first_list ++ second_list, first_keyword_key: first_keyword_value

# matchedExpressionAdditionOperation
identifier first_value + second_value, first_keyword_key: first_keyword_value

# matchedExpressionMultiplicationOperation
identifier first_value * second_value, first_keyword_key: first_keyword_value

# matchedExpressionHatOperation
identifier first_value ^^^ second_value, first_keyword_key: first_keyword_value

# matchedExpressionUnaryOperation
identifier -first_value, first_keyword_key: first_keyword_value

# matchedExpressionDotAlias
identifier One.Two, first_keyword_key: first_keyword_value

# matchedExpressionDotIdentifier
identifier first_argument, first_keyword_key: first_keyword_value

# matchedExpressionAtOperation
identifier @attribute, first_keyword_key: first_keyword_value

# identifierExpression
identifier first_argument, first_keyword_key: first_keyword_value

# numberCaptureOperation
identifier &1, first_keyword_key: first_keyword_value

# numberUnaryOperation
identifier -1, first_keyword_key: first_keyword_value

# numberAtOperation
identifier @0, first_keyword_key: first_keyword_value

# OPENING_PARENTHESIS EOL* SEMICOLON EOL* CLOSING_PARENTHESIS |
identifier (;), first_keyword_key: first_keyword_value

# CHAR_TOKEN
identifier ?a, first_keyword_key: first_keyword_value

# number
identifier 1e-1, first_keyword_key: first_keyword_value

# list
identifier [], first_keyword_key: first_keyword_value

# string
identifier "string", first_keyword_key: first_keyword_value

# stringHeredoc
identifier """
           string
           heredoc
           """,
           first_keyword_key: first_keyword_value

# charList
identifier 'charList', first_keyword_key: first_keyword_value

# charListHereDoc
identifier '''
           charList
           heredoc
           ''',
           first_keyword_key: first_keyword_value

# sigil
identifier ~r{sigil}, first_keyword_key: first_keyword_value

# FALSE
identifier false, first_keyword_key: first_keyword_value

# NIL
identifier nil, first_keyword_key: first_keyword_value

# TRUE
identifier true, first_keyword_key: first_keyword_value

# atom
identifier :atom, first_keyword_key: first_keyword_value

# ALIAS
identifier Alias, first_keyword_key: first_keyword_value
