fn (positional, key: value) when guard -> () end
fn (positional, key: value) when guard -> function positional, key: value end
fn (positional, key: value) when guard -> &one end
fn (positional, key: value) when guard -> one \\ default end
fn (positional, key: value) when guard -> one when key: value end
fn (positional, key: value) when guard -> one when guard end
fn (positional, key: value) when guard -> one :: type end
fn (positional, key: value) when guard -> one | two end
fn (positional, key: value) when guard -> one = two end
fn (positional, key: value) when guard -> one || two end
fn (positional, key: value) when guard -> one && two end
fn (positional, key: value) when guard -> one != two end
fn (positional, key: value) when guard -> one < two end
fn (positional, key: value) when guard -> one + two end
fn (positional, key: value) when guard -> one * two end
fn (positional, key: value) when guard -> one <|> two end
fn (positional, key: value) when guard -> one ^^^ two end
fn (positional, key: value) when guard -> !one end
fn (positional, key: value) when guard -> not one end
fn (positional, key: value) when guard -> Module.function positional, key: value end
fn (positional, key: value) when guard -> @function positional, key: value end
fn (positional, key: value) when guard -> function positional, key: value end
fn (positional, key: value) when guard -> One.Two[key] end
fn (positional, key: value) when guard -> Module.function[key] end
fn (positional, key: value) when guard -> Module.function() end
fn (positional, key: value) when guard -> Module.function end
fn (positional, key: value) when guard -> @variable[key] end
fn (positional, key: value) when guard -> @variable end
fn (positional, key: value) when guard -> function positional, key: value end
fn (positional, key: value) when guard -> variable[key] end
fn (positional, key: value) when guard -> variable end
fn (positional, key: value) when guard -> @1 end
fn (positional, key: value) when guard -> &1 end
fn (positional, key: value) when guard -> !1 end
fn (positional, key: value) when guard -> fn (positional, key: value) when guard -> end end
fn (positional, key: value) when guard -> (;) end
fn (positional, key: value) when guard -> 1 end
fn (positional, key: value) when guard -> [] end
fn (positional, key: value) when guard -> "one" end
fn (positional, key: value) when guard -> """
      one
      """ end
fn (positional, key: value) when guard -> 'one' end
fn (positional, key: value) when guard -> '''
      one
      ''' end
fn (positional, key: value) when guard -> ~x{sigil}modifiers end
fn (positional, key: value) when guard -> true end
fn (positional, key: value) when guard -> :atom end
fn (positional, key: value) when guard -> Alias end
