defmodule MyAppWeb.PageLive do
  defdelegate delegated_function(), to: MyAppWeb.PageLive.Helper
end
