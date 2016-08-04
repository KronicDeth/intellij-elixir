relative_identifier(
  One.{}
)(
  Two.{Three}
).{Four, Five}
relative_identifier key: One.{}
relative_identifier unqualified One.{Two},
                                key: Three.{Four, Five}
relative_identifier One.{},
                    key: Two.{Three}
