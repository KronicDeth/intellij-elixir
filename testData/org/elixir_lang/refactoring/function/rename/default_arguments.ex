defmodule RenameMod do
  def gr<caret>eet(name, greeting \\ "hi"), do: "#{greeting}, #{name}"

  def call_it do
    greet("a") <> greet("a", "yo")
  end
end
