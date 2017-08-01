defmodule Absinthe.Schema.Notation.Writer do
  defp type_functions(definition) do
    ast = build(:type, definition)
    identifier = definition.identifier
    name = definition.attrs[:name]

    result = [
      quote do: def __absinthe<caret>_type__(unquote(name)), do: __absinthe_type__(unquote(identifier))
    ]

    if definition.builder == Absinthe.Type.Object do
      [
        quote do
          def __absinthe_type__(unquote(identifier)) do
            unquote(ast)
          end
        end,
        result
      ]
    else
      [
        quote do
          def __absinthe_type__(unquote(identifier)), do: unquote(ast)
        end,
        result
      ]
    end
  end
end
