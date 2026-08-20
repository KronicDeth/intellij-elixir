defmodule MyApp.RepoTest do
  use MyApp.ConnCase, async: true

  def test do
    Repo.insert!(%{})
  end
end
