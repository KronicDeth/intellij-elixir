   def handle_response({:ok, %{status_code: 200, body: body}}) do
       Logger.info "Successful response"
       Logger.debug fn -> inspect(body) end
       Logger.
       { :ok, :jsx.decode(bo<caret>dy) }
   end
