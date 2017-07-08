%{
  one => 1,
  two: 2
}

one = %{
  two => 2,
  three: 3
}

one %{
  two => 2,
  three: 3
}

one two, %{
  three => 3,
  four: 4
}

one %{
      two => 2
    },
    %{
      three: 3
    }

def stab do
  %{
    one => 1,
    two: 2
  }

  one = %{
    two => 2,
    three: 3
  }

  one %{
    two => 2,
    three: 3
  }

  one two, %{
    three => 3,
    four: 4
  }

  one %{
        two => 2
      },
      %{
        three: 3
      }
end
