"""
One
""".relative_identifier(
  """
  Two
  """,
  key: """
       Three
       """
)
"""
One
""".relative_identifier(
  """
  Two
  """,
  key: """
       Three
       """
)(
  """
  Four
  """,
  key: """
       Five
       """
)
"""
One
""".relative_identifier key: """
                             Two
                             """
"""
One
""".relative_identifier unqualified """
                                    Two
                                    """,
                                    key: """
                                         Three
                                         """
"""
One
""".relative_identifier """
                        Two
                        """,
                        key: """
                             Three
                             """