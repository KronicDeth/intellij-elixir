defmodule Interpreter.Resources do
  @moduledoc """
  A module that exposes Ecto schema structs
  """

  # Types

  @typedoc """
  ID that uniquely identifies the `struct`
  """
  @type id :: term

  @type params :: map

  @typedoc """
    * `:associations` - associations to load in the `struct`
  """
  @type query_options :: %{optional(:associations) => atom | [atom]}

  @type sandbox_access_token :: %{required(:owner) => term}

  # Callbacks

  @doc """
  Allows access to sandbox for testing
  """
  @callback allow_sandbox_access(sandbox_access_token) :: :ok | {:already, :owner | :allowed} | :not_found

  @doc """
  Changeset for creating a struct from the `params`
  """
  @callback changeset(params) :: Ecto.Changeset.

  @doc <error descr="Strings aren't allowed in types">"""
  Deletes a single `struct`

  # Returns

    * `{:ok, struct}` - the delete succeeded and the returned struct is the state before delete
    * `{:error, Ecto.Changeset.t}` - errors while deleting the `struct`.  `Ecto.Changeset.t` `errors` contains errors.
  """</error>
  @callback delete(struct) :: {:ok, struct} | {:error, Ecto.Changeset.t}

  @doc """
  Gets a single `struct`

  # Returns

    * `nil` - if the `id` was not found
    * `struct` - if the `id` was found

  """
  @callback get(id, query_options) :: nil | struct

  @doc """
  Inserts `changeset` into a single new `struct`

  # Returns
    * `{:ok, struct}` - `changeset` was inserted into `struct`
    * `{:error, Ecto.Changeset.t}` - insert failed.  `Ecto.Changeset.t` `errors` contain errors.
  """
  @callback insert(Ecto.Changeset.t, query_options) :: {:ok, struct} | {:error, Ecto.Changeset.t}

  @doc """
  Inserts `params` into a single new `struct`

  # Returns

    * `{:ok, struct}` - params were inserted into `struct`
    * `{:error, Ecto.Changeset.t}` - insert failed.  `Ecto.Changeset.t` `errors` contain errors.

  """
  @callback insert(params, query_options) :: {:ok, struct} | {:error, Ecto.Changeset.t}

  @doc """
  Gets a list of `struct`s.
  """
  @callback list(query_options) :: [struct]

  @doc """
  # Returns

    * `true` - if `allow_sandbox_access/1` should be called before any of the query methods are called
    * `false` - otherwise
  """
  @callback sandboxed?() :: boolean

  @doc """
  Updates `struct`

  # Returns

    * `{:ok, struct}` - the update succeeded and the returned `struct` contains the updates
    * `{error, Ecto.Changeset.t}` - errors while updating `struct` with `params`.  `Ecto.Changeset.t` `errors` contains
      errors.
  """
  @callback update(struct, params, query_options) :: {:ok, struct} | {:error, Ecto.Changeset.t}
end
