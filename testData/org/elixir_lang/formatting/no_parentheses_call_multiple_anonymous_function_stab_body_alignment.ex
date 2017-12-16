one fn two ->
  two
end

one fn
  ^two ->
    :three
  ^four ->
    :five
end

one two, fn three ->
  three
end

one fn two -> two end, fn three ->
  three
end

one fn
      ^two ->
        :three
      ^four ->
        :five
    end,
    fn
      ^six ->
        :seven
      ^eight ->
        :nine
    end
