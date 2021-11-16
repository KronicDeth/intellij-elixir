defmodule Behaviour do
  @callback callback_as_function
  @macrocallback callback_as_macro

  def helper do
    call<caret>
  end
end
