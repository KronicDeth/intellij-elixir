# dotOperation(atOperation(identifier), identifier)
@identifier.function

# dotOperation(atOperation(identifier), alias)
@identifier.Alias

# dotOperation(identifier, identifier)
identifier = Alias
identifier.function

# dotOperation(alias, alias)
First.Second

# dotOperation(alias, identifier)
Alias.identifier

# dotOperation(dotOperation(alias, alias), identifier)
AliasOne.AliasTwo.identifier

# dotOperation(atom, identifier)
:"Elixir.String".to_integer

# dotOperation(atom, alias)
:"Elixir.String".Char
