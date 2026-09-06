defmodule BareStringBindingSites do
  def run do
    "#{fresh = 1}"
    fresh
  end
end
