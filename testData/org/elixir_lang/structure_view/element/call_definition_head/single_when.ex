def create(state = %__MODULE__{ecto_schema_module: ecto_schema_module, view: view}, params) when is_map(params) do
  with :ok <- can(state, :create, ecto_schema_module),
       {:ok, document} <- document(params, :create),
       insertable_params = insertable_params(state, document),
       {:ok, changeset} <- changeset(state, insertable_params),
       :ok <- can(state, :create, changeset),
       {:ok, created} <- create_changeset(state, changeset) do
    authorized = authorized(state, created)
    {:ok, view.show(authorized, params)}
  end
end
