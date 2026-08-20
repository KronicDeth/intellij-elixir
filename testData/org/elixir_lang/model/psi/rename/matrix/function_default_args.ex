defmodule DefaultArgs do
  def renamee(name, greeting \\ "hello"), do: {greeting, name}

  def one do
    renamee("a")
  end

  def two do
    renamee("a", "hi")
  end
end
