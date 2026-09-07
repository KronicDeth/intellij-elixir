defmodule ComprehensionTestNameSites do
  use ExUnit.Case

  for fresh <- [1, 2] do
    test "handles #{fresh}" do
      assert fresh == 1
    end
  end
end
