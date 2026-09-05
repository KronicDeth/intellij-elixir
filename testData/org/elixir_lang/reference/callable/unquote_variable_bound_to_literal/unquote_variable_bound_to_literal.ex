defmodule BoundToLiteral do
  @moduledoc false

  @callback docs_uri() :: binary()
  defmacro __using__(_opts) do
    docs_uri = "https://example.com/docs"

    quote do
      @moduledoc unquote(module_doc)
      unquote(docs_uri)
    end
  end
end
