receive do
  _ -> :flushed
after 0 ->
  :timeout
end
