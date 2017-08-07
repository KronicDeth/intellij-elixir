defmodule LoggerLogstashBackend.TCPTest do
  def setup_backend(context = %{backend: true}) do
    # don't include in function head so that backend true will always match even if forgot to populate :port
    %{line: line, port: port} = context

    backend = {LoggerLogstashBackend, String.to_atom("#{inspect __MODULE__}:#{line}")}

    Logger.add_backend backend
    Logger.configure_backend backend, [
      host: "127.0.0.1",
      port: port,
      level: :info,
      type: "some_app",
      metadata: [
        some_metadata: "go here"
      ],
      protocol: :tcp
    ]

    on_exit fn ->
      Logger.remove_backend backend
    end

    con<caret>text
  end
end
