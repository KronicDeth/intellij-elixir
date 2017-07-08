defmodule Foo do
def foo do
receive do
{:ok, value} -> value
{:error, reason} -> reason
end
end
end
