defmodule DefaultArgs do
  def gr<caret>eet(name, greeting \\ "Hi") do
    "#{greeting}, #{name}"
  end
end
