defmodule SelfCompletion do
  def an_already_defined_function, do: :ok

  def the_function_currently_being_defined<caret>

  def an_already_defined_function, do: :ok
end
