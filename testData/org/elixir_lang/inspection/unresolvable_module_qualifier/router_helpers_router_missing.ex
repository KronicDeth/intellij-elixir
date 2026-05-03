defmodule RouterHelpersRouterMissingTest do
  alias Missing.Router.Helpers

  <error descr="Module 'Helpers' is not defined or aliased in this scope">Helpers</error>.device_url(Endpoint, :index, tag_id: 123)
end
