defmodule Absinthe.Schema.Notation.Writer do
  defmacro __before_compile__(env) do
    info = build_info(env)

    errors        = Macro.escape info.errors
    exports       = Macro.escape info.exports
    type_map      = Macro.escape info.type_map
    implementors  = Macro.escape info.implementors
    directive_map = Macro.escape info.directive_map

    [
      quote do
        def __absinthe_types__, do: unquote(type_map)
      end,
      info.type_functions,
      quote do
        def __absinthe_type__(_), do: nil
      end,
      quote do
        def __absinthe_<caret>directives__, do: unquote(directive_map)
      end,
      info.directive_functions,
      quote do
        def __absinthe_directive__(_), do: nil
      end,
      quote do
        def __absinthe_errors__, do: unquote(errors)
        def __absinthe_interface_implementors__, do: unquote(implementors)
        def __absinthe_exports__, do: unquote(exports)
      end,
    ]
  end
end
