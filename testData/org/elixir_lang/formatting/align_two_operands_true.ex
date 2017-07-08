%Error{
  detail: "`#{pointer}` contains quoted integer (`#{integer}`), " <>
          "but also excess text (`#{inspect remainder_of_binary}`)"
}
