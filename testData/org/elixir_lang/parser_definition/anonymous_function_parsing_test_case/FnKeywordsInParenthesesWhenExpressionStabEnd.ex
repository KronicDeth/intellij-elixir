fn (key_one: value_one, key_two: value_two) when () -> end
fn (key_one: value_one, key_two: value_two) when function positional, key: value -> end
fn (key_one: value_one, key_two: value_two) when &one -> end
fn (key_one: value_one, key_two: value_two) when one \\ default -> end
fn (key_one: value_one, key_two: value_two) when one when key: value -> end
fn (key_one: value_one, key_two: value_two) when one when guard -> end
fn (key_one: value_one, key_two: value_two) when one :: type -> end
fn (key_one: value_one, key_two: value_two) when one | two -> end
fn (key_one: value_one, key_two: value_two) when one = two -> end
fn (key_one: value_one, key_two: value_two) when one || two -> end
fn (key_one: value_one, key_two: value_two) when one && two -> end
fn (key_one: value_one, key_two: value_two) when one != two -> end
fn (key_one: value_one, key_two: value_two) when one < two -> end
fn (key_one: value_one, key_two: value_two) when one + two -> end
fn (key_one: value_one, key_two: value_two) when one * two -> end
fn (key_one: value_one, key_two: value_two) when one <|> two -> end
fn (key_one: value_one, key_two: value_two) when one ^^^ two -> end
fn (key_one: value_one, key_two: value_two) when !one -> end
fn (key_one: value_one, key_two: value_two) when not one -> end
fn (key_one: value_one, key_two: value_two) when Module.function positional, key: value -> end
fn (key_one: value_one, key_two: value_two) when @function positional, key: value -> end
fn (key_one: value_one, key_two: value_two) when function positional, key: value -> end
fn (key_one: value_one, key_two: value_two) when One.Two[key] -> end
fn (key_one: value_one, key_two: value_two) when Module.function[key] -> end
fn (key_one: value_one, key_two: value_two) when Module.function() -> end
fn (key_one: value_one, key_two: value_two) when Module.function -> end
fn (key_one: value_one, key_two: value_two) when @variable[key] -> end
fn (key_one: value_one, key_two: value_two) when @variable -> end
fn (key_one: value_one, key_two: value_two) when function positional, key: value -> end
fn (key_one: value_one, key_two: value_two) when variable[key] -> end
fn (key_one: value_one, key_two: value_two) when variable -> end
fn (key_one: value_one, key_two: value_two) when @1 -> end
fn (key_one: value_one, key_two: value_two) when &1 -> end
fn (key_one: value_one, key_two: value_two) when !1 -> end
fn (key_one: value_one, key_two: value_two) when fn -> end -> end
fn (key_one: value_one, key_two: value_two) when (;) -> end
fn (key_one: value_one, key_two: value_two) when 1 -> end
fn (key_one: value_one, key_two: value_two) when [] -> end
fn (key_one: value_one, key_two: value_two) when "one" -> end
fn (key_one: value_one, key_two: value_two) when """
                                                 one
                                                 """ -> end
fn (key_one: value_one, key_two: value_two) when 'one' -> end
fn (key_one: value_one, key_two: value_two) when '''
                                                 one
                                                 ''' -> end
fn (key_one: value_one, key_two: value_two) when ~x{sigil}modifiers -> end
fn (key_one: value_one, key_two: value_two) when true -> end
fn (key_one: value_one, key_two: value_two) when :atom -> end
fn (key_one: value_one, key_two: value_two) when Alias -> end
