defmodule ComprehensionTestNameSites do
  use ExUnit.Case

  for renamee <- [1, 2] do
    test "handles #{renamee}" do
      assert renamee == 1
    end
  end
end
