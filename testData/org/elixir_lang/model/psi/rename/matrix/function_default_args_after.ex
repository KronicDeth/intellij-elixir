defmodule DefaultArgs do
  def fresh(name, greeting \\ "hello"), do: {greeting, name}

  def one do
    fresh("a")
  end

  def two do
    fresh("a", "hi")
  end
end
