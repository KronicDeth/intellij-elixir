defmodule Phoenix.Router.Helpers do
  def defhelper_catch_all({helper, routes_and_exprs}) do
    [quote @anno do
       defp raise_<caret>route_error(unquote(helper), suffix, arity, action) do
         Phoenix.Router.Helpers.raise_route_error(__MODULE__, "#{unquote(helper)}_#{suffix}",
           arity, action, unquote(routes))
       end
     end | catch_alls]
  end
end
