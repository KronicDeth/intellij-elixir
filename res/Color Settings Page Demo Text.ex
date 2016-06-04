# Numbers
0b0101011
1234 ; 0x1A ; 0xbeef ; 0763 ; 0o123
3.14 ; 5.0e21 ; 0.5e-12
100_000_000

# these are not valid numbers
0b012 ; 0xboar ; 0o888
0B01 ; 0XAF ; 0O123

# Characters
?a ; ?1 ; ?<escape-sequence>\n</escape-sequence> ; ?<escape-sequence>\s</escape-sequence> ; ?<escape-sequence>\c</escape-sequence> ; ? ; ?,
?<escape-sequence>\x{12}</escape-sequence> ; ?<escape-sequence>\x{abcd}</escape-sequence>
?<escape-sequence>\x34</escape-sequence> ; ?<escape-sequence>\xF</escape-sequence>

# these show that only the first digit is part of the character
?<escape-sequence>\1</escape-sequence>23 ; ?<escape-sequence>\1</escape-sequence>2 ; <escape-sequence>?\7</escape-sequence>

# Atoms
:this ; :that
:'complex atom'
:"with' <escape-sequence>\"</escape-sequence><escape-sequence>\"</escape-sequence> 'quotes"
:" multi
 line ' <escape-sequence>\s</escape-sequence> <escape-sequence>\1</escape-sequence>23 <escape-sequence>\xff</escape-sequence>
atom"
:... ; :<<>> ; :%{} ; :% ; :{}
:++; :--; :*; :~~~; :::
:% ; :. ; :<-

# Strings
"Hello world"
"Interspersed <escape-sequence>\x{ff}</escape-sequence> codes <escape-sequence>\7</escape-sequence> <escape-sequence>\8</escape-sequence> <escape-sequence>\6</escape-sequence>5 <escape-sequence>\0</escape-sequence>16 and <escape-sequence>\t</escape-sequence><escape-sequence>\s</escape-sequence><escape-sequence>\\</escape-sequence>s<escape-sequence>\z</escape-sequence><escape-sequence>\+</escape-sequence> <escape-sequence>\\</escape-sequence> escapes"
"Quotes ' inside <escape-sequence>\"</escape-sequence> <escape-sequence>\1</escape-sequence>23 the <escape-sequence>\"</escape-sequence><escape-sequence>\"</escape-sequence> <escape-sequence>\xF</escape-sequence> <escape-sequence>\\</escape-sequence>xF string <escape-sequence>\\</escape-sequence><escape-sequence>\"</escape-sequence> end"
"Multiline
   string"

# Char lists
'this is a list'
'escapes <escape-sequence>\'</escape-sequence> <escape-sequence>\t</escape-sequence> <escape-sequence>\\</escape-sequence><escape-sequence>\'</escape-sequence>'
'Multiline
    char
  list
'

# Binaries
<<1, 2, 3>>
<<"hello"::binary, c :: utf8, x:: 4 * 2>> = "hello™1"

# Sigils
~r/this + i<escape-sequence>\s</escape-sequence> "a" regex/
~R'this + i\s "a" regex too'
~w(hello #{ ["has" <> "123", '<escape-sequence>\c</escape-sequence><escape-sequence>\d</escape-sequence>', "<escape-sequence>\1</escape-sequence>23 interpol" | []] } world)s
~W(hello #{no "123" \c\d \123 interpol} world)s

~s{Escapes terminators <escape-sequence>\{</escape-sequence> and <escape-sequence>\}</escape-sequence>, but no {balancing} # outside of sigil here }

~S"No escapes \s\t\n and no #{interpolation}"

:"atoms work #{"to" <> "o"}"

# Operators
x = 1 + 2.0 * 3
y = true and false; z = false or true
... = 144
... == !x && y || z
"hello" |> String.upcase |> String.downcase()
{^z, a} = {true, x}

# Free operators (added in 1.0.0)
p  ~>> f  = bind(p, f)
p1 ~>  p2 = pair_right(p1, p2)
p1 <~  p2 = pair_left(p1, p2)
p1 <~> p2 = pair_both(p1, p2)
p  |~> f  = map(p, f)
p1 <|> p2 = either(p1, p2)

# Lists, tuples, maps, keywords
[1, :a, 'hello'] ++ [2, 3]
[:head | [?t, ?a, ?i, ?l]]

{:one, 2.0, "three"}

[...: "this", <<>>: "is", %{}: "a keyword", %: "list", {}: "too"]
["this is an atom too": 1, "so is this": 2]
[option: "value", key: :word]
[++: "operator", ~~~: :&&&]

map = %{shortcut: "syntax"}
%{map | "update" => "me"}
%{ 12 => 13, :weird => ['thing'] }

# Comprehensions
for x <- 1..10, x < 5, do: {x, x}
pixels = "12345678"
for << <<r::4, g::4, b::4, a::size(4)>> <- pixels >> do
  [r, {g, %{"b" => a}}]
end

# String interpolation
"String #{inspect "interpolation"} is quite #{1+4+7} difficult"

# Identifiers
abc_123 = 1
_018OP = 2
A__0 == 3

# Modules
defmodule Long.Module.Name do
  <documentation-module-attribute>@moduledoc</documentation-module-attribute> "<documentation-text>Simple module docstring</documentation-text>"

  <documentation-module-attribute>@doc</documentation-module-attribute> """
  <documentation-text>Multiline docstring
  "with quotes"
  and </documentation-text>#{ inspect %{"interpolation" => "in" <> "action"} }<documentation-text>
  now with </documentation-text>#{ {:a, 'tuple'} }<documentation-text>
  and </documentation-text>#{ inspect {
      :tuple,
      %{ with: "nested #{ inspect %{ :interpolation => %{} } }" }
  } }
  """
  defstruct [:a, :name, :height]

  <documentation-module-attribute>@doc</documentation-module-attribute> ~S'''
  <documentation-text>No #{interpolation} of any kind.
  \000 \x{ff}

  \n #{\x{ff}}</documentation-text>
  '''
  def func(a, b \\ []), do: :ok

  <documentation-module-attribute>@doc</documentation-module-attribute> false
  def __before_compile__(_) do
    :ok
  end
end

# Structs
defmodule Second.Module do
  s = %Long.Module.Name{name: "Silly"}
  %Long.Module.Name{s | height: {192, :cm}}
  ".. #{%Long.Module.Name{s | height: {192, :cm}}} .."
end

# Types, pseudo-vars, attributes
defmodule M do
  @custom_attr :some_constant

  @before_compile Long.Module.Name

  <documentation-module-attribute>@typedoc</documentation-module-attribute> "<documentation-text>This is a type</documentation-text>"
  @type typ :: integer

  <documentation-module-attribute>@typedoc</documentation-module-attribute> """
  <documentation-text>Another type</documentation-text>
  """
  @opaque typtyp :: 1..10

  @spec func(typ, typtyp) :: :ok | :fail
  def func(a, b) do
    a || b || :ok || :fail
    Path.expand("..", __DIR__)
    IO.inspect __ENV__
    __NOTAPSEUDOVAR__ = 11
    __MODULE__.func(b, a)
  end

  defmacro m() do
    __CALLER__
  end
end

# Functions
anon = fn x, y, z ->
  fn(a, b, c) ->
    &(x + y - z * a / &1 + b + div(&2, c))
  end
end

&Set.put(&1, &2) ; & Set.put(&1, &2) ; &( Set.put(&1, &1) )

# Function calls
anon.(1, 2, 3); self; hd([1,2,3])
Kernel.spawn(fn -> :ok end)
IO.ANSI.black

# Control flow
if :this do
  :that
else
  :otherwise
end

pid = self
receive do
  {:EXIT, _} -> :done
  {^pid, :_} -> nil
  after 100 -> :no_luck
end

case __ENV__.line do
  x when is_integer(x) -> x
  x when x in 1..12 -> -x
end

cond do
  false -> "too bad"
  4 > 5 -> "oops"
  true -> nil
end

# Lexical scope modifiers
import Kernel, except: [spawn: 1, +: 2, /: 2, Unless: 2]
alias Long.Module.Name, as: N0men123_and4
use Bitwise

4 &&& 5
2 <<< 3

# Protocols
defprotocol Useless do
  def func1(this)
  def func2(that)
end

defimpl Useless, for: Atom do
end

# Exceptions
defmodule NotAnError do
  defexception [:message]
end

raise NotAnError, message: "This is not an error"
