# Configure your database
config :retort,
       Retort.Repo,
       adapter: Ecto.Adapters.Postgres,
       database: "retort_test",
       hostname: "localhost",
       password: "postgres",
       pool: Ecto.Adapters.SQL.Sandbox,
       username: "postgres"
