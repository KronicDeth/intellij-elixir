defmodule One do
  import Two, except: [three: 4], only: [five: 6]
end
