# map/struct

one two: %Three{
      four: %Five{
        six: seven
      }
    },
    eight: Nine

one two: %{
      three: %Four{
        five: six
      }
    },
    seven: Eight

# list

one two: [
      three: %Four{
        five: six
      },
      seven: Eight
    ]

one two: [
      three,
      four
    ]

one two: [
      three,
      four: %{
        five: six
      },
      seven: eight
    ]

# tuple

one two: {
      three,
      four
    }

one two: {
      three,
      four: %{
        five: six
      },
      seven: eight
    }
