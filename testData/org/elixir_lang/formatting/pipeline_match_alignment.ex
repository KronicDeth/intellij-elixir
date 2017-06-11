opts = []
       |> Keyword.merge(params_to_render_opts(params))
       |> Keyword.merge(pagination_to_render_opts(pagination, options))
