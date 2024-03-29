# Source code recreated from a .beam file by IntelliJ Elixir
defmodule EEx do
  @moduledoc ~S"""
  EEx stands for Embedded Elixir. It allows you to embed
  Elixir code inside a string in a robust way.

      iex> EEx.eval_string("foo <%= bar %>", bar: "baz")
      "foo baz"

  ## API

  This module provides 3 main APIs for you to use:

    1. Evaluate a string (`eval_string/3`) or a file (`eval_file/3`)
       directly. This is the simplest API to use but also the
       slowest, since the code is evaluated and not compiled before.

    2. Define a function from a string (`function_from_string/5`)
       or a file (`function_from_file/5`). This allows you to embed
       the template as a function inside a module which will then
       be compiled. This is the preferred API if you have access
       to the template at compilation time.

    3. Compile a string (`compile_string/2`) or a file (`compile_file/2`)
       into Elixir syntax tree. This is the API used by both functions
       above and is available to you if you want to provide your own
       ways of handling the compiled template.

  ## Options

  All functions in this module accept EEx-related options.
  They are:

    * `:file` - the file to be used in the template. Defaults to the given
      file the template is read from or to "nofile" when compiling from a string.
    * `:line` - the line to be used as the template start. Defaults to 1.
    * `:indentation` - (since v1.11.0) an integer added to the column after every
      new line. Defaults to 0.
    * `:engine` - the EEx engine to be used for compilation.
    * `:trim` - if true, trims whitespace left/right of quotation tags up until
      newlines. At least one newline is retained. Defaults to false.

  ## Engine

  EEx has the concept of engines which allows you to modify or
  transform the code extracted from the given string or file.

  By default, `EEx` uses the `EEx.SmartEngine` that provides some
  conveniences on top of the simple `EEx.Engine`.

  ### Tags

  `EEx.SmartEngine` supports the following tags:

      <% Elixir expression - inline with output %>
      <%= Elixir expression - replace with result %>
      <%% EEx quotation - returns the contents inside %>
      <%# Comments - they are discarded from source %>

  All expressions that output something to the template
  **must** use the equals sign (`=`). Since everything in
  Elixir is an expression, there are no exceptions for this rule.
  For example, while some template languages would special-case
  `if` clauses, they are treated the same in EEx and
  also require `=` in order to have their result printed:

      <%= if true do %>
        It is obviously true
      <% else %>
        This will never appear
      <% end %>

  To escape an EEx expression in EEx use `<%% content %>`. For example:

      <%%= x + 3 %>

  will be rendered as `<%= x + 3 %>`.

  Note that different engines may have different rules
  for each tag. Other tags may be added in future versions.

  ### Macros

  `EEx.SmartEngine` also adds some macros to your template.
  An example is the `@` macro which allows easy data access
  in a template:

      iex> EEx.eval_string("<%= @foo %>", assigns: [foo: 1])
      "1"

  In other words, `<%= @foo %>` translates to:

      <%= {:ok, v} = Access.fetch(assigns, :foo); v %>

  The `assigns` extension is useful when the number of variables
  required by the template is not specified at compilation time.

  """

  # Macros

  defmacro function_from_file(x0, x1, x2) do
    super(x0, x1, x2, [], [])
  end

  defmacro function_from_file(x0, x1, x2, x3) do
    super(x0, x1, x2, x3, [])
  end

  @doc ~S"""
  Generates a function definition from the file contents.

  The kind (`:def` or `:defp`) must be given, the
  function name, its arguments and the compilation options.

  This function is useful in case you have templates but
  you want to precompile inside a module for speed.

  ## Examples

      # sample.eex
      <%= a + b %>

      # sample.ex
      defmodule Sample do
        require EEx
        EEx.function_from_file(:def, :sample, "sample.eex", [:a, :b])
      end

      # iex
      Sample.sample(1, 2)
      #=> "3"


  """
  defmacro function_from_file(kind, name, file, args, options) do
    {:__block__, [], [{:=, [], [{:args, [line: 162], EEx}, args]}, {:=, [], [{:file, [line: 162], EEx}, file]}, {:=, [], [{:kind, [line: 162], EEx}, kind]}, {:=, [], [{:name, [line: 162], EEx}, name]}, {:=, [], [{:options, [line: 162], EEx}, options]}, {:__block__, [], [{:=, [], [{:info, [], EEx}, {{:".", [], [{:__aliases__, [alias: false], [:"Keyword"]}, :merge]}, [], [{:options, [], EEx}, [file: {:file, [], EEx}, line: 1]]}]}, {:=, [], [{:args, [], EEx}, {{:".", [], [{:__aliases__, [alias: false], [:"Enum"]}, :map]}, [], [{:args, [], EEx}, {:fn, [], [{:"->", [], [[{:arg, [], EEx}], {:"{}", [], [{:arg, [], EEx}, [line: 1], nil]}]}]}]}]}, {:=, [], [{:compiled, [], EEx}, {{:".", [], [{:__aliases__, [alias: false], [:"EEx"]}, :compile_file]}, [], [{:file, [], EEx}, {:info, [], EEx}]}]}, {:@, [context: EEx, import: Kernel], [{:external_resource, [context: EEx], [{:file, [], EEx}]}]}, {:@, [context: EEx, import: Kernel], [{:file, [context: EEx], [{:file, [], EEx}]}]}, {:case, [], [{:kind, [], EEx}, [do: [{:"->", [], [[:def], {:def, [context: EEx, import: Kernel], [{{:unquote, [], [{:name, [], EEx}]}, [context: EEx], [{:unquote_splicing, [], [{:args, [], EEx}]}]}, [do: {:unquote, [], [{:compiled, [], EEx}]}]]}]}, {:"->", [], [[:defp], {:defp, [context: EEx, import: Kernel], [{{:unquote, [], [{:name, [], EEx}]}, [context: EEx], [{:unquote_splicing, [], [{:args, [], EEx}]}]}, [do: {:unquote, [], [{:compiled, [], EEx}]}]]}]}]]]}]}]}
  end

  defmacro function_from_string(x0, x1, x2) do
    super(x0, x1, x2, [], [])
  end

  defmacro function_from_string(x0, x1, x2, x3) do
    super(x0, x1, x2, x3, [])
  end

  @doc ~S"""
  Generates a function definition from the string.

  The kind (`:def` or `:defp`) must be given, the
  function name, its arguments and the compilation options.

  ## Examples

      iex> defmodule Sample do
      ...>   require EEx
      ...>   EEx.function_from_string(:def, :sample, "<%= a + b %>", [:a, :b])
      ...> end
      iex> Sample.sample(1, 2)
      "3"


  """
  defmacro function_from_string(kind, name, source, args, options) do
    {:__block__, [], [{:=, [], [{:args, [line: 124], EEx}, args]}, {:=, [], [{:kind, [line: 124], EEx}, kind]}, {:=, [], [{:name, [line: 124], EEx}, name]}, {:=, [], [{:options, [line: 124], EEx}, options]}, {:=, [], [{:source, [line: 124], EEx}, source]}, {:__block__, [], [{:=, [], [{:info, [], EEx}, {{:".", [], [{:__aliases__, [alias: false], [:"Keyword"]}, :merge]}, [], [[file: {{:".", [], [{:__ENV__, [], EEx}, :file]}, [no_parens: true], []}, line: {{:".", [], [{:__ENV__, [], EEx}, :line]}, [no_parens: true], []}], {:options, [], EEx}]}]}, {:=, [], [{:args, [], EEx}, {{:".", [], [{:__aliases__, [alias: false], [:"Enum"]}, :map]}, [], [{:args, [], EEx}, {:fn, [], [{:"->", [], [[{:arg, [], EEx}], {:"{}", [], [{:arg, [], EEx}, [line: {{:".", [], [Access, :get]}, [], [{:info, [], EEx}, :line]}], nil]}]}]}]}]}, {:=, [], [{:compiled, [], EEx}, {{:".", [], [{:__aliases__, [alias: false], [:"EEx"]}, :compile_string]}, [], [{:source, [], EEx}, {:info, [], EEx}]}]}, {:case, [], [{:kind, [], EEx}, [do: [{:"->", [], [[:def], {:def, [context: EEx, import: Kernel], [{{:unquote, [], [{:name, [], EEx}]}, [context: EEx], [{:unquote_splicing, [], [{:args, [], EEx}]}]}, [do: {:unquote, [], [{:compiled, [], EEx}]}]]}]}, {:"->", [], [[:defp], {:defp, [context: EEx, import: Kernel], [{{:unquote, [], [{:name, [], EEx}]}, [context: EEx], [{:unquote_splicing, [], [{:args, [], EEx}]}]}, [do: {:unquote, [], [{:compiled, [], EEx}]}]]}]}]]]}]}]}
  end

  # Functions

  def __info__(p0) do
    # body not decompiled
  end

  def compile_file(x0) do
    super(x0, [])
  end

  @doc ~S"""
  Gets a `filename` and generate a quoted expression
  that can be evaluated by Elixir or compiled to a function.

  """
  def compile_file(filename, options) do
    (
      options = Keyword.merge(options, file: filename, line: 1)
      compile_string(File.read!(filename), options)
      )
  end

  def compile_string(x0) do
    super(x0, [])
  end

  @doc ~S"""
  Gets a string `source` and generate a quoted expression
  that can be evaluated by Elixir or compiled to a function.

  """
  def compile_string(source, options) do
    EEx.Compiler.compile(source, options)
  end

  def eval_file(x0) do
    super(x0, [], [])
  end

  def eval_file(x0, x1) do
    super(x0, x1, [])
  end

  @doc ~S"""
  Gets a `filename` and evaluate the values using the `bindings`.

  ## Examples

      # sample.eex
      foo <%= bar %>

      # iex
      EEx.eval_file("sample.eex", bar: "baz")
      #=> "foo baz"


  """
  def eval_file(filename, bindings, options) do
    (
      options = Keyword.put(options, :file, filename)
      compiled = compile_file(filename, options)
      do_eval(compiled, bindings, options)
      )
  end

  def eval_string(x0) do
    super(x0, [], [])
  end

  def eval_string(x0, x1) do
    super(x0, x1, [])
  end

  @doc ~S"""
  Gets a string `source` and evaluate the values using the `bindings`.

  ## Examples

      iex> EEx.eval_string("foo <%= bar %>", bar: "baz")
      "foo baz"


  """
  def eval_string(source, bindings, options) do
    (
      compiled = compile_string(source, options)
      do_eval(compiled, bindings, options)
      )
  end

  def module_info() do
    # body not decompiled
  end

  def module_info(p0) do
    # body not decompiled
  end

  # Private Functions

  defp do_eval(compiled, bindings, options) do
    (
      {result, _} = Code.eval_quoted(compiled, bindings, options)
      result
      )
  end
end
