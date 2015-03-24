## Pattern for Infix Operators

```
<name>InfixOperator := <prefix-eols?> <operator-tokens> EOL*
                       {
                         implements = "org.elixir_lang.psi.Operator"
                         methods = [
                           operatorTokenSet
                           quote
                         ]
                       }
```

### Expression

matched<name>Operation uses `*` so that when there is no operator, the match on `matched<name>Operand` does not need to
be backtracked.

```
private matched<name>Expression ::= matched<name>Operand matched<name>Operation*
```

### Operand

Lower precedence prefix operators need to be checked because they consume everything to the right.

```
private matched<name>Operand ::= <for name(p_l) with equal or lower precedence and prefix fixity:
                                   matched<name(p_l)>RightOperation> |
                                 matched<name(precedence +1)>Expression
```

### Operation

The operation is a left rule so that left-associativity works correctly.

```
left matched<name>Operation ::= <name>InfixOperator matched<name>Operand
                                { implements = "org.elixir_lang.psi.InfixOperation" methods = [quote] }
```

### Terminating condition

The right and left sides can only be combined because noParenthesesManyArgumentsCall is checked before
noParenthesesNoArgumentsCall.  This ordering is necessary because noParenthesesNoArgumentsCall will always parse a
prefix of noParenthesesManyArgumentsCall.

## Pattern for Prefix Operators

```
<name>PrefixOperator ::= <operator-tokens> EOL*
                         {
                           implements = "org.elixir_lang.psi.Operator"
                           methods = [
                             operatorTokenSet
                             quote
                           ]
                         }
```

### Expression

Because the operator appears first in matched<name>Operation, if matched<name>Operation doesn't matched, then it needs
to backtrack to matched<name>Expression and try just the operand

```
private matched<name>Expression ::= matched<name>Operation |
                                    matched<name>Operand
```

This could be rewritten as to make the operator optional, but it would require matching matched<name>Expression not
private to allow for quoting when there is an operator.  By not being private, the node would also always appear in
the parse tree, so backtracking is favored.

### Operand

The operand is identical for prefix or infix operators.  It should be noted that due to the "equal or lower precedence",
a prefix operation can take a nested copy of itself as an operand, which doesn't occur for infix operations since the
for loop is only over prefix operators.

```
private matched<name>Operand ::= <for name(p_l) with equal or lower precedence and prefix fixity:
                                   matched<name(p_l)>RightOperation> |
                                 matched<name(precedence +1)>Expression
```

### Operation

The operation for prefix operator is the same as for infix operator except prefix operators don't use the `left`
modifier.

```
matched<name>Operation ::= <name>PrefixOperator matched<name>Operand
                           { implements = "org.elixir_lang.psi.PrefixOperation" methods = [quote] }
```

### Terminating condition

The right and left sides can only be combined because noParenthesesManyArgumentsCall is checked before
noParenthesesNoArgumentsCall.  This ordering is necessary because noParenthesesNoArgumentsCall will always parse a
prefix of noParenthesesManyArgumentsCall.
