defmodule RouterHelpersAsSuppressedTest do
  alias IcWeb.Router.Helpers, as: RouterHelpers

  RouterHelpers.device_url(Endpoint, :index, tag_id: 123)
end
