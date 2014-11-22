# dotOperation(atOperation(identifier), identifier)
@identifier.function

# dotOperation(atOperation(identifier), alias)
@identifier.Alias

# dotOperation(identifier, identifier)
identifier = Alias
identifier.function

# dotOperation(alias, alias)
First.Second

# dotOperation(atom, identifier)
:"Elixir.String".to_integer

# dotOperation(atom, alias)
:"Elixir.String".Char
