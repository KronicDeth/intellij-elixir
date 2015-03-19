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
function positional, keyword_key: first_value ||| second_value

# matchedExpressionAndOperation
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

# matchedExpressionDotAlias
function positional, keyword_key: One.Two

# matchedExpressionDotIdentifier
function positional, keyword_key: Alias.identifier

# OPENING_PARENTHESIS EOL* SEMICOLON EOL* CLOSING_PARENTHESIS
function positional, keyword_key: (;)

# CHAR_TOKEN
function positional, keyword_key: ?c

# number
function positional, keyword_key: 1.0e-1

# list
function positional, keyword_key: []

# string
function positional, keyword_key: "string"

# stringHeredoc
function positional, keyword_key: """
                                  string
                                  heredoc
                                  """
# charList
function positional, keyword_key: 'charList'

# charListHeredoc
function positional, keyword_key: '''
                                  charList
                                  heredoc
                                  '''
# sigil
function position, keyword_key: ~r{sigil}

# FALSE
function position, keyword_key: false

# NIL
function position, keyword_key: nil

# TRUE
function position, keyword_key: true

# atom
function position, keyword_key: :atom

# ALIAS
function position, keyword_key: Alias

# emptyParentheses
function position, keyword_key: ()
