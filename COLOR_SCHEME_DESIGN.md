# Color Scheme Design

The color design for `additionalTextAttributes` needs to be similar to color Palettes for Preferences > Editor > Colors & Fonts > Language Defaults, but more obvious, contrasting colors should be chosen when Language Defaults use no attributes or shared attributes for multiple Text Attribute Keys.  This is to ensure that different parts of the language are easier to distinguish visually and so that user stop reporting that IntelliJ Elixir doesn't have syntax highlighting, as was the case before using `additionalTextAttributes`.
 
## Categories

Certain Text Attribute Keys, even when not nested in the Preferences > Editor > Colors & Fonts > Elixir share similar hues to show they are related logical concepts.

Throughout all the categories, red hues are avoided, as red is used by the error highlighting by default.

### Atom

"Atom" uses the "Language Defaults > Classes > Instance field" to match intellij-erlang.

### Braces and Operators

The "Braces and Operators" category name and subcategory names are inherited from "Language Defaults".  In "Language Defaults", only "Comma" and "Semicolon" have text attributes and all other Text Attribute Keys have no text attributes.  The syntax highlighting not being obvious to users (and them opening bugs) has been a common issue for IntelliJ Elixir, so all braces and operators have unique text attributes.  Thankfully, the HSB (Hue Saturation Brightness) format of colors allowed to make each Text Attribute Key in this category be unique by using a fixed offset (30°) as Hue is represented in degrees (0-360).

The Hue difference isn't exact because the color is stored in RGB, which doesn't always cleanly convert back to a 30° multiple.


| Text Attribute Key                    | Hue  |
|---------------------------------------|------|
| Braces and Operators > Bit            | 230° |
| Braces and Operators > Braces         | 197° |
| Braces and Operators > Brackets       | 180° |
| Braces and Operators > Dot            | 149° |
| Braces and Operators > Interpolation  | 120° |
| Braces and Operators > Operation Sign | 90°  |
| Braces and Parentheses > Parentheses  | 60°  |                   

#### Map and Structs

Maps `%{}` and Structs `%Alias{}` both use braces `{}`, but it is helpful to be able to visually distinguish nested maps, structs, and tuples (which use braces).  Since Maps and Structs use `{` and `}`, the color needs to be closely related to the color for "Braces and Operators > Braces".

| Text Attribute Key                               | Hue  | Relation to other Text Attribute Keys    |
|--------------------------------------------------|------|------------------------------------------|
| Braces and Operators > Maps And Structs > Maps   | 210° | "Braces and Operators > Braces" Hue +10° |
| Braces and Operators > Braces                    | 197° |                                          |
| Braces and Operators > Map and Structs > Structs | 190° | "Braces and Operators > Braces" Hue -10° |

### Calls

Currently, the Calls category is used to annotate `Kernel` function and macros; and `Kernel.SpecialForms` macros.  A lot of other editors use their "Keywords" color for these function calls, but I don't want developers confusing these predefined functions and macros as Elixir keywords, so "Calls > Predefined" is set to inherit from "Language Defaults > Identifiers > Predefined Symbols", but the color is match the "Keywords".

Because "Calls > Function" and "Calls > Macro" will eventually be used for all function and macro calls, they need to not use the Foreground attribute as it was being used by "Calls > Predefined" already.  Therefore, "Calls > Function" is Italic while "Calls > Macro" is "Bold Italic" as it is a compiled-time function call. 

### Direct Inheritors of Language Defaults

A few Text Attribute Keys use the same text attributes as in "Language Defaults".  These were chosen subjectively when their color didn't conflict with the other colors that were customized.

* Braces and Operators > Comma
* Braces and Operators > Semicolon
* Comment
* Keywords

### Documentation

"Language Defaults" assumes documentation is part of comments, so the design is C and C++ (for doxygen) and Java (for javadoc) focused, so the specific category names could not be used, but their text attributes could be used.

| Text Attribute Key                       | "Language Default" Text Attribute Keys |
|------------------------------------------|----------------------------------------|
| Module Attributes > Documentation        | Comments > Doc Comment > Tag           |
| Module Attributes > Documentation > Text | Comments > Doc Comment > Tag value     |

### Number

All valid number parts inherit from "Language Default > Number" while all invalid number part inherit from "Language Default > Bad Character".

### Predefined

All Predefined category Text Attribute keys use the orange hue from Keyword in "Language Default".

| Text Attribute Key(s)          | Part of Language                                            | Predefinition level   |
|--------------------------------|-------------------------------------------------------------|-----------------------|
| Calls > Predefined             | `Kernel` functions and macros, `Kernel.SpecialForms` macros | `import`ed by default |
| Keywords                       | `after`, `catch`, `do`, `else`,  `end`, `fn`, `rescue`      | Lexer                 |
| Merge of "Atom" and "Keywords" | `false`, `nil`, `true`                                      | Lexer                 |

### Textual

For the textual container (Character List and String), String is brighter than "Character List" to show that String is the newer, more preferred format over "Character List".   Importantly, the Hue is exactly the same.

| Text Attribute Key | Hue  | Saturation | Brightness | Relation to other Text Attribute Keys                                                                                                                                                                                                                                                                                                                                    |
|--------------------|------|------------|------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Character List     | 97°  | 34%        | 52%        | A darker, less saturated form of String                                                                                                                                                                                                                                                                                                                                  |
| String             | 97°  | 60%        | 70%        | A brighter, more saturated form of "Character List"                                                                                                                                                                                                                                                                                                                      |
| Escape Sequence    | 277° | 47%        | 61%        | Escape tokens need to contrast heavily with both "Character List" and "String", so the Hue is +180° while the Saturation and Brightness is the average of the two.  Finally, it is Bold to make it stand out when used in Sigils.                                                                                                                                        |
| Sigil              | 187° | 60%        | 70%        | Sigil's Hue is +90° from "Character List" and "String", so that it contrasts with "Character List", String, and "Escape Sequence".  It has the same Saturation and Brightness and "String" because it's a new, preferred format like "String". Because Sigil is an injection of a small DSL, it also has a Background color to emphasize it's not exactly Elixir syntax. |

### Type-like

All Type-like Text Attribute Keys have variations on the "Module Attributes > Types > Type" text attributes. 

| Text Attribute Key                         | Description        | Relation to Other Text Attribute Keys                                     |
|--------------------------------------------|--------------------|---------------------------------------------------------------------------|
| Alias                                      | Bold Bright Yellow | 100% brightness, more saturated "Module Attributes > Types > Type"        |
| Module Attributes > Types > Callback       | Bold Gold          | X                                                                         |
| Module Attributes > Types > Specification  | Gold               | Non-bold "Module Attributes > Types > Callback"                           |
| Module Attributes > Types > Type           | Yellow             | Base around other Text Attribute Keys are varied                          |
| Module Attributes > Types > Type Parameter | Pink/Yellow blend  | Average of "Module Attributes > Types > Type" and "Variables > Parameter" |

#### Variables

| Text Attribute Key(s)                      | Hue  | Saturation | Brightness | Relation to other Text Attribute Keys                                                                                                                                                                                                                                                                            |
|--------------------------------------------|------|------------|------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Variables > Ignored                        | 0°   | 0%         | 44%        | Grey because it shouldn't be used like how UI elements are greyed out                                                                                                                                                                                                                                            |
| Variables > Parameter                      | 290° | 72%        | 100%       | Hue is +30° from "Variables > Variable", so it is easy to differentiate a parameter and a variable that is rebinding that parameter name.  This is also far into the Pink Hues, so much higher degrees would put it into the Red Hues reserved for marking errors.                                               |
| Variables > Variable                       | 260° | 72%        | 100%       | Hue is +30° from closest "Braces and Operator", which is "Braces and Operators > Maps and Structs > Bit", which has a Hue of 230°, so that contrast is high enough for bit string composition and matches.                                                                                                       |
| Module Attributes > Types > Type Parameter | 352° | 50%        | 80%        | Hue is average of "Module Attributes > Types > Type" and "Variables > Parameter", but that put it near 170°, which was between "Braces and Operators > Braces" and "Braces and Operators > Brackets", so add +180°.  It is in Red Hues, so the Saturation is lowered to keep it pinker than Error Highlight Red. |
