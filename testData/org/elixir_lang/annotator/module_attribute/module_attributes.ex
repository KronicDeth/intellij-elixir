# Preferences > Editors > Colors & Fonts > Elixir setting name in comments above each paragraph
defmodule ModuleAttributes do
  # `@moduledoc` - Documention Module Attributes
  # `@moduledoc` heredoc - Documention Text
  @moduledoc """
  String Heredocs are highlighted as Documentation Text
  """

  # `@doc` - Documentation Module Attributes
  # `@doc` string - Documentation Text
  # `@callback` - Module Attributes
  # `callback_name` - Specification
  # `atom` - Type
  @doc "A callback"
  @callback callback_name(atom) :: {:ok, atom} | :error

  # `@macrocallback` - Module Attributes
  # `macro_callback_name` - Spcification
  # `list`, `tuple` - Type
  @macrocallback macro_callback_name(list) :: tuple

  # Types

  # `@typedoc` - Documentation Module Attributes
  # `@typedoc` string - Documentation Text
  # `@opaque` - Module Attributes
  # `opaque_type` - Type
  @typedoc "Don't think about how this works"
  @opaque opaque_type :: [1, 2]

  # `@type` - Module Attributes
  # `type_with_parameters` - Type
  # `a`, `b` - Type Parameter
  @type type_with_parameters(a, b) :: {a, b}

  @type type_name :: {:ok, list}

  # `@spec` - Module Attributes
  # `do_it` - Specificaton
  # `any` - Type
  @spec do_it({:ok, any}) :: :ok
  def do_it({:ok, _}), do: :ok

  # `@spec` - Module Attributes
  # `do_nothing` - Specification
  # `result` - Type Parameter
  # `any` - Type
  @spec do_nothing(result) :: result when result: {:error, any}
  def do_nothing(result = {:error, _}), do: result
end
