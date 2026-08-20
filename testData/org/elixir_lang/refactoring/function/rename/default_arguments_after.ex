defmodule RenameMod do
  def salute(name, greeting \\ "hi"), do: "#{greeting}, #{name}"

  def call_it do
    salute("a") <> salute("a", "yo")
  end
end
