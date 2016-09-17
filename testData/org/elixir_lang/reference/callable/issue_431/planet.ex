defmodule Physics.Planet do

  alias Physics.Utils.{Laws, Convert, Calculate}

  defstruct [
    ev: 0,
    mass: 0,
    radius: 0,
    name: nil,
    type: :rocky
  ]

  def planets do
    alias Physics.Planet
    [
      {:mars,    %Planet{name: "Mars",    type: :rocky,   mass: 6.41e23,  radius: 3.37e6}},
      {:moon,    %Planet{name: "Moon",    type: :rocky,   mass: 7.35e22,  radius: 1.738e6}},
      {:venus,   %Planet{name: "Venus",   type: :rocky,   mass: 4.86e24,  radius: 6.05e6}},
      {:earth,   %Planet{name: "Earth",   type: :rocky,   mass: 5.972e24, radius: 6.37e6}},
      {:saturn,  %Planet{name: "Saturn",  type: :gaseous, mass: 5.68e26,  radius: 6.02e7}},
      {:uranus,  %Planet{name: "Uranus",  type: :gaseous, mass: 8.68e25,  radius: 2.55e7}},
      {:mercury, %Planet{name: "Mercury", type: :rocky,   mass: 3.3e23,   radius: 2.439e6}},
      {:jupiter, %Planet{name: "Jupiter", type: :gaseous, mass: 1.89e27,  radius: 7.14e7}},
      {:neptune, %Planet{name: "Neptune", type: :gaseous, mass: 1.02e26,  radius: 2.47e7}}
    ]
  end

  def select, do: for planet<caret> <- planets, do: planet |> set_ev

  def escape_velocity(planet) when is_map(planet) do
    planet
    |> calculate_escape_velocity
    |> Convert.to_km
    |> Convert.to_nearest_tenth
  end

  defp set_ev({name, planet}) do
    {name, %{ planet | ev: escape_velocity(planet) }}
  end

  defp calculate_escape_velocity(%{mass: mass, radius: radius}) do
    2 * Laws.newtons_gravitational_constant * mass / radius
      |> Calculate.square_root
  end

end
