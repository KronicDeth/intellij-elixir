defmodule Usage do
  # First element is an integer - not a module alias or atom, so no MFA reference
  tuple = {42, :er<caret>ror, :ok}
  tuple
end
