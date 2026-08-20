defmodule ParameterInfo.DefaultArgsDeclaration do
  def greet(name, greeting \\ "Hello"), do: "#{greeting}, #{name}"
end
