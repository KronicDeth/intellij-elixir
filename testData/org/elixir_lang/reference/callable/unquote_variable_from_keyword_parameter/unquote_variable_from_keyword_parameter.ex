defmodule FromKeywordParameter do
  @moduledoc false

  @callback docs_uri() :: binary()
  defmacro __using__(_opts, docs_uri: docs_uri) do
    quote do
      @moduledoc unquote(module_doc)
      unquote(docs_uri)
    end
  end
end
