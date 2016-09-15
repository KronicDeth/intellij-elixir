defmodule Zzyzzzyzy.MessageController do
  use Zzyzzzyzy.Web, :controller

  alias Zzyzzzyzy.Endpoint

  def create(conn, params) do
    case params do
      %{"auth" => auth} ->
        if auth == Endpoint.config(:auth_key) do
          case params do
            %{"channel" => channel, "event" => event, "data" => data} ->
              Endpoint.broadcast!(channel, event, data)
              conn |> put_status(:created) |> json(%{success: true})
            _no_required_data ->
              conn |> put_status(:precondition_failed) |> json(%{error: "Incorrect input"})
          end
        else
          conn |> hu<caret>
        end
      _no_auth_token ->
        conn |> handle_unauthorized
    end
  end

  defp handle_unauthorized(conn) do
     conn |> put_status(:unauthorized)
  end
end
