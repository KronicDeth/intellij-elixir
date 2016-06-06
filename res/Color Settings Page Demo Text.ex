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
?<escape-sequence>\1</escape-sequence><error><valid-digit>23</valid-digit></error> ; ?<escape-sequence>\1</escape-sequence><error><valid-digit>2</valid-digit></error> ; <escape-sequence>?\7</escape-sequence>

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
<<"hello"::<type>binary</type>, c :: <type>utf8</type>, x:: 4 * 2>> = "hello™1"

# Sigils
~r/this + i<escape-sequence>\s</escape-sequence> "a" regex/
~R'this + i\s "a" regex too'
~w(hello #{ ["has" <> "123", '<escape-sequence>\c</escape-sequence><escape-sequence>\d</escape-sequence>', "<escape-sequence>\1</escape-sequence>23 interpol" | []] } world)s
~W(hello #{no "123" \c\d \123 interpol} world)s

~s{Escapes terminators <escape-sequence>\{</escape-sequence> and <escape-sequence>\}</escape-sequence>, but no {balancing} # outside of sigil here }

~S"No escapes \s\t\n and no #{interpolation}"

:"atoms work #{"to" <> "o"}"

# Operators
<variable>x</variable> = 1 + 2.0 * 3
<variable>y</variable> = true and false; <variable>z</variable> = false or true
<variable>...</variable> = 144
<variable>...</variable> == !<variable>x</variable> && <variable>y</variable> || <variable>z</variable>
"hello" |> <alias>String</alias>.upcase |> <alias>String</alias>.downcase()
{^<variable>z</variable>, <variable>a</variable>} = {true, <variable>x</variable>}

# Free operators (added in 1.0.0)
<variable>p</variable>  ~>> <variable>f</variable>  = bind(p, f)
<variable>p1</variable> ~>  <variable>p2</variable> = pair_right(p1, p2)
<variable>p1</variable> <~  <variable>p2</variable> = pair_left(<variable>p1</variable>, <variable>p2</variable>)
<variable>p1</variable> <~> <variable>p2</variable> = pair_both(<variable>p1</variable>, <variable>p2</variable>)
<variable>p1</variable> <|> <variable>p2</variable> = either(<variable>p1</variable>, <variable>p2</variable>)

# Lists, tuples, maps, keywords
[1, :a, 'hello'] ++ [2, 3]
[:head | [?t, ?a, ?i, ?l]]

{:one, 2.0, "three"}

[<atom>...:</atom> "this", <atom><<>></atom>: "is", <atom>%{}:</atom> "a keyword", <atom>%:</atom> "list", <atom>{}:</atom> "too"]
["this is an atom too": 1, "so is this": 2]
[<atom>option:</atom> "value", <atom>key:</atom> :word]
[<atom>++:</atom> "operator", <atom>~~~:</atom> :&&&]

<variable>map</variable> = <map>%{</map><atom>shortcut:</atom> "syntax"<map>}</map>
<map>%{</map><variable>map</variable> | "update" => "me"<map>}</map>
<map>%{</map> 12 => 13, :weird => ['thing'] <map>}</map>

# Comprehensions
<predefined-call><macro-call>for</macro-call></predefined-call> x <- 1..10, x < 5, <atom>do:</atom> {x, x}
<variable>pixels</variable> = "12345678"
<predefined-call><macro-call>for</macro-call></predefined-call> << <<<parameter>r</parameter>::4, <parameter>g</parameter>::4, <parameter>b</parameter>::4, <parameter>a</parameter>::size(4)>> <- <variable>pixels</variable> >> do
  [<parameter>r</parameter>, {<parameter>g</parameter>, <map>%{</map>"b" => <parameter>a</parameter><map>}</map>}]
end

# String interpolation
"String #{inspect "interpolation"} is quite #{1+4+7} difficult"

# Identifiers
<variable>abc_123</variable> = 1
<variable>_018OP</variable> = 2
<alias>A__0</alias> == 3

# Modules
defmodule <alias>Long.Module.Name</alias> do
  <documentation-module-attribute>@moduledoc</documentation-module-attribute> "<documentation-text>Simple module docstring</documentation-text>"

  <documentation-module-attribute>@doc</documentation-module-attribute> """
  <documentation-text>Multiline docstring
  "with quotes"
  and </documentation-text>#{ inspect %{"interpolation" => "in" <> "action"} }<documentation-text>
  now with </documentation-text>#{ {:a, 'tuple'} }<documentation-text>
  and </documentation-text>#{ inspect {
      :tuple,
      <map>%{</map> <atom>with:</atom> "nested #{ inspect <map>%{</map> :interpolation => <map>%{</map><map>}</map> <map>}</map> }" <map>}</map>
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
defmodule <alias>Second.Module</alias> do
  <variable>s</variable> = <struct>%</struct><alias>Long.Module.Name</alias>{<atom>name:</atom> "Silly"<struct>}</struct>
  <struct>%</struct><alias>Long.Module.Name</alias><struct>{</struct><variable>s</variable> | <atom>height:</atom> {192, :cm}<struct>}</struct>
  ".. #{<struct>%</struct><alias>Long.Module.Name</alias>{<variable>s</variable> | <atom>height:</atom> {192, :cm}<struct>}</struct>} .."
end

# Types, pseudo-vars, attributes
defmodule <alias>M</alias> do
  @custom_attr :some_constant

  @before_compile <alias>Long.Module.Name</alias>

  <documentation-module-attribute>@typedoc</documentation-module-attribute> "<documentation-text>This is a type</documentation-text>"
  @type <type>typ</type> :: <type>integer</type>

  <documentation-module-attribute>@typedoc</documentation-module-attribute> """
  <documentation-text>Another type</documentation-text>
  """
  @opaque <type>typtyp</type> :: 1..10

  @spec func(<type>typ</type>, <type>typtyp</type>) :: :ok | :fail
  def func(a, b) do
    a || b || :ok || :fail
    <alias>Path</alias>.expand("..", __DIR__)
    <alias>IO</alias>.inspect __ENV__
    <variable>__NOTAPSEUDOVAR__</variable> = 11
    __MODULE__.func(b, a)
  end

  defmacro m() do
    __CALLER__
  end
end

# Functions
<variable>anon</variable> = fn x, y, z ->
  fn(a, b, c) ->
    &(x + y - z * a / &1 + b + div(&2, c))
  end
end

&<alias>set</alias>.put(&1, &2) ; & <alias>Set</alias>.put(&1, &2) ; &( <alias>Set</alias>.put(&1, &1) )

# Function calls
<variable>anon</variable>.(1, 2, 3); self; hd([1,2,3])
<alias>Kernel</alias>.spawn(fn -> :ok end)
<alias>IO.ANSI</alias>.black

# Control flow
if :this do
  :that
else
  :otherwise
end

<variable>pid</variable> = self
receive do
  {:EXIT, _} -> :done
  {^<variable>pid</variable>, :_} -> nil
  after 100 -> :no_luck
end

case __ENV__.line do
  <variable>x</variable> when is_integer(<variable>x</variable>) -> <variable>x</variable>
  <variable>x</variable> when <variable>x</variable> in 1..12 -> -<variable>x</variable>
end

cond do
  false -> "too bad"
  4 > 5 -> "oops"
  true -> nil
end

# Lexical scope modifiers
import <alias>Kernel</alias>, <atom>except:</atom> [<atom>spawn:</atom> 1, <atom>+:</atom> 2, <atom>/:</atom> 2, <atom>unless:</atom> 2]
alias <alias>Long.Module.Name</alias>, <atom>as:</atom> <alias>N0men123_and4</alias>
use <alias>Bitwise</alias>

4 &&& 5
2 <<< 3

# Protocols
defprotocol <alias>Useless</alias> do
  def func1(this)
  def func2(that)
end

defimpl <alias>Useless</alias>, <atom>for:</atom> <alias>Atom</alias> do
end

# Exceptions
defmodule <alias>NotAnError</alias> do
  defexception [:message]
end

raise <alias>NotAnError</alias>, <atom>message:</atom> "This is not an error"
