defmodule Calcinator.Resources.Ecto.RepoTest do
  # 111
  describe "list/1 with minimum page size with default page size with maximum page size" do
    setup [:minimum_page_size, :default_page_size, :maximum_page_size]

    test "without page query option returns page with default page size",
         %{
           page_size: %{
             default: default
           }
         } do
      assert_three_pages %{
        page_size: default,
        query_options: %{}
      }
    end
  end
end
