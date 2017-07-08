defmodule Calcinator.Authorization.Can do
  @spec filter_associations_can(
          struct,
          Authorization.association_ascent,
          Authorization.subject,
          Authorization.action,
          t
        ) :: struct
  @spec filter_associations_can(
          [struct],
          Authorization.association_ascent,
          Authorization.subject,
          Authorization.action,
          t
        ) :: [struct]
end
