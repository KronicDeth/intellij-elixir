%{
  state |
  request_by_correlation_id: Map.put(
    state.request_by_correlation_id,
    correlation_id,
    %Retort.Client.Request{
      from: from,
      method: String.to_existing_atom(method)
    }
  )
}
