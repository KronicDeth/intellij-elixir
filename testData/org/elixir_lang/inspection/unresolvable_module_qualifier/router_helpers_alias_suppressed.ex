defmodule RouterHelpersAliasSuppressedTest do
  alias IcWeb.Router.Helpers

  Helpers.device_url(Endpoint, :index, tag_id: 123)
end
