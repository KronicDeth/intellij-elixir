defmodule VariableUsages do
  use ExUnit.Case

  for va<caret>riable <- [1, 2] do
    test "handles #{variable}" do
      assert variable == 1
    end
  end
end
