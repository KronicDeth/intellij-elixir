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

### Both side expression

Right expression is checked first so that noParenthesesManyArgumentsCall arguments are consumed by this level instead
of precedence + 1.  matched<name>Expression is meant to be used when either right or left expression is acceptable.
In general the `expression` suffix should be used for private rules.

```
private matched<name>Expression ::= matched<name>RightExpression |
                                    matched<name(precedence + 1)>Expression
```

### Right-hand side

#### Expression

matched<name>RightExpression only exists to ensure matched<name>RightOperation is used.  matched<name>RightExpression
does not need to be Quotable because matched<name>RightOperation is a left rule and will surround
matched<name>LeftOperand for quoting.  Note, the `+` is crucial as it ensures there is at least one
matched<name>RightOperation and matched<name>RightExpression does not degenerate to just matched<name>LeftOperand, which
would make the right and left expression equivalent.  Additionally with `*` instead of `+`, the identifier in
noParenthesesManyArgumentsCall will be consumed as a noParenthesesNoArgumentsCall.

```
private matched<name>RightExpression ::= matched<name>LeftOperand matched<name>RightOperation+
```

#### Operand

noParenthesesManyArgumentsCall being a valid operand is what separates matched<name>RightOperand from matched<name>LeftOperand.
Because it has no parentheses, noParenthesesManyArgumentsCall can only appear on the right-hand side of an expression to
prevent ambiguity with commas.

```
private matched<name>RightOperand ::= noParenthesesManyArgumentsCall | matched<name>LeftOperand
```

#### Operation

matched<name>RightOperation exists to ensure that the right operand really is a right operand that accepts
noParenthesesManyArgumentsCall.  It is a left rule so that it consume the left operand and the final PsiElement contains
(leftOperand, operator, rightOperand) when the fixity is infix.

```
left matched<name>RightOperation ::= <name><fixity>Operator matched<name>RightOperand
                                     { implements = "org.elixir_lang.psi.<fixity>Operation" methods = [quote] }
```

### Left-hand side

The left-hand side can function as either the left- or right-hand side of an operation, but importantly, it excludes
right-hand side only constructs like noParenthesesManyArgumentsCall.

#### Expression

matched<name>LeftExpression exists to ensure that matched<name>LeftOperation is used.  matched<name>LeftExpression
does not need to be Quotable because matched<name>LeftOperation is a left rule and will surround
matched<name>LeftOperand for quoting.  It should be noted that matched<name>LeftExpression uses `*` on its operation
unlike matched<name>RightExpression uses `+` on its operation.  This is because it is ok for left expressions to
degenerate to the higher precedence operand.

```
private matched<name>LeftExpression ::= matched<name>LeftOperand matched<name>LeftOperation*
```

#### Operand

matched<name>LeftOperand is complex because due to the nature of prefix operation parsing in Elixir's native LALR
grammar, any prefix operation of a lower precedence can appear as a valid operand for a higher precedence as a
right operand in GrammarKit's LL grammar.  Rechecking the prefix operators emulates that the prefix operator would have
already consumed the right operand in LALR since the R means right-most deriviation.

```
private matched<name>LeftOperand ::= <for name(p_l) with lower precedence and prefix fixity:
                                        matched<name(p_l)>LeftOperation> |
                                     matched<name(precedence + 1)>LeftExpression
```

#### Operation

matched<name>LeftOperation uses matched<name>LeftOperand for its right operand because it is assumed
matched<name>LeftOperation is being used from matched<name>LeftExpression.  As see above
in matched<name>LeftOperand, matched<name>LeftExpression will be used as matched<name(precedence +1)>LeftExpression in
matched<name(precedence -1)>LeftOperand to ensure the left-ness and therefore, noParenthesesManyArgumentsCall exclusion
is propagated down the rules.

```
left matched<name>LeftOperation ::= <name><fixity>Operator matched<name>LeftOperand
```

### Higher-level patterns between sides

#### Expression

You'll notice that matched<name>RightExpression and matched<name>LeftExpression follow the same higher level pattern

```
private matched<name><side>Expression> ::= matched<name>LeftOperand matched<name><side>Operation*
```

Both use matched<name>LeftOperand for the left operand while they allow the right operand to be side specific.

#### Operand

There is no commonality between matched<name>RightOperand and matched<name>LeftOperand besides the name
matched<name><side>Operand.  It can actually be argued that the whole right vs left distinction in Expression and
Operation is only there so the different Operands can be used.  In a grammar that allows for the sideness to be passed
as an argument, the Expression and Operation rules for the two sides could be combined and just pass the side.

#### Operation

If you make side variable, then matched<name>RightOperation and matched<name>LeftOperation follow the exact same
pattern.

```
left matched<name><side>Operation ::= <name><fixity>Operator matched<fixity><side>Operand
```

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

### Both side expression

Right expression is checked first so that noParenthesesManyArgumentsCall arguments are consumed by this level instead
of precedence + 1.  matched<name>Expression is meant to be used when either right or left expression is acceptable.
In general the `expression` suffix should be used for private rules.

```
private matched<name>Expression ::= matched<name>RightExpression |
                                    matched<name(precedence + 1)>Expression
```

### Right-hand side

#### Expression

Unlike with infix, which consumes to the left, prefix operations consume to the right, so the
matched<name>RightExpression just is matched<name>RightOperation

```
private matched<name>RightExpression ::= matched<name>RightOperation
```

#### Operand

matched<name>RightOperand for prefix operator follow the same pattern as for infix operators

```
private matched<name>RightOperand ::= noParenthesesManyArgumentsCall | matched<name>LeftOperand
```

### Operation

As mentioned in Expression, infix Operation is not a left rule, so it contains both the operator and operand rule for
prefix operators

```
matched<name>RightOperation ::= <name>PrefixOperator matched<name>RightOperand
```

### Left-hand side

#### Expression

As with the RightExpression, due to prefix operations consuming to the right, the LeftExpression is composed of the
LeftOperation and LeftOperand

```
private matched<name>LeftExpression ::= matched<name>LeftOperation | matched<name>LeftOperand
```

#### Operand

The left operand for prefix operators follows almost the same pattern as for infix operators, but the for loop over
prefix operator will include this operator again, so the operand in a prefix operation can be another of the same
prefix operation, which allows for stuff like `&&function`.

```
private matched<name>LeftOperand ::= <for name(p_l) with equal or lower precedence and prefix fixity:
                                        matched<name(p_l)>LeftOperation> |
                                     matched<name(precedence + 1)>LeftExpression
```

#### Operation

The left operation follows the same pattern as the right operation.

```
matched<name>LeftOperation ::= <name>PrefixOperator matched<name>LeftOperand
```