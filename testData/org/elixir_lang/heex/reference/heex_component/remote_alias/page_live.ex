defmodule MyAppWeb.PageLive do
  alias MyAppWeb.CoreComponents, as: Widgets

  def mount(_params, _session, socket) do
    {:ok, socket}
  end
end
