defmodule EEx do
  defmacro function_from_file(kind, name, file, args \\ [], options \\ []) do
    quote do
      unquote(kind)
      unquote(name)
      unquote(file)
      unquote(args)
      unquote(options)
    end
  end

  defmacro function_from_string(kind, name, source, args \\ [], options \\ []) do
    quote do
      unquote(kind)
      unquote(name)
      unquote(source)
      unquote(args)
      unquote(options)
    end
  end
end
