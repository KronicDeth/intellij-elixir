# Source code recreated from a .beam file by IntelliJ Elixir
defmodule EExTestWeb.PageController do

  # Functions

  def __info__(p0) do
    # body not decompiled
  end

  @doc false
  def action(conn_before, opts) do
    try() do
      conn_after = super(conn_before, opts)
      conn_after
    catch
      :error, reason ->
        Phoenix.Controller.Pipeline.__catch__(conn_before, reason, EExTestWeb.PageController, conn_before.private().phoenix_action(), __STACKTRACE__)
    end
  end

  @doc false
  def call(conn, action) when is_atom(action) do
    (
      conn = Map.update!(conn, :private, fn x1 -> :maps.put(:phoenix_action, action, :maps.put(:phoenix_controller, EExTestWeb.PageController, x1)) end)
      phoenix_controller_pipeline(conn, action)
    )
  end

  def index(conn, _params) do
    (
      boolean = true
      string = "José Valim"
      charlist = [74, 111, 115, 233, 32, 86, 97, 108, 105, 109]
      tuple = {1, :atom, string, charlist}
      integer = 9001
      float = 0.5
      list = [1, 2]
      improper_list = [1 | 2]
      atom_key_map = %{atom: 2}
      string_key_map = %{"string" => 3}
      user = %EExTest.Accounts.User{__meta__: %Ecto.Schema.Metadata{context: nil, prefix: nil, schema: EExTest.Accounts.User, source: "users", state: :built}, age: nil, id: nil, inserted_at: nil, updated_at: nil, name: "Alice"}
      bitstring = <<20::integer(), 50::integer()-size(9)>>
      binary = <<200::integer(), 50::integer()>>
      with({:ok, user} <- EExTest.Accounts.create_user(%{name: string, age: integer})) do
        IO.inspect(user)
      end
      Phoenix.Controller.render(conn, "index.html")
    )
  end

  @doc false
  def init(opts) do
    opts
  end

  def module_info() do
    # body not decompiled
  end

  def module_info(p0) do
    # body not decompiled
  end

  # Private Functions

  defp unquote(:"-call/2-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"action (overridable 2)")(%Plug.Conn{private: %{phoenix_action: action}} = conn, _options) do
    apply(EExTestWeb.PageController, action, [conn, conn.params()])
  end

  defp phoenix_controller_pipeline(conn, action), do: ...
end
