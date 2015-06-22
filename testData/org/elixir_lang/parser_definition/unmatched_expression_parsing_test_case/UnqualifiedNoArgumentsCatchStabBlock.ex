identifier do catch expression end
identifier do catch; expression; end
identifier do
  catch expression
end
identifier do
  catch
  expression
end

identifier do catch -> end
identifier do catch -> ; end
identifier do
  catch ->
end
identifier do
  catch
  ->
end

identifier do catch -> expression end
identifier do afer -> expression; end
identifier do
  catch -> expression
end
identifier do
  catch
    ->
      expression
end

identifier do catch () -> expression end
identifier do catch () -> expression; end
identifier do
  catch () -> expression
end
identifier do
  catch
    ()
      ->
        expression
end

identifier do catch expression -> end
identifier do catch expression ->; end
identifier do
  catch
    expression
      ->
end

identifier do catch (key: value) -> end
identifier do catch (key: value) ->; end
identifier do
  catch
    (key: value)
      ->
end

identifier do catch (one, two) -> end
identifier do catch (one, two) ->; end
identifier do
  catch
    (one, two)
      ->
end

identifier do catch (one, key: value) -> end
identifier do catch (one, key: value) ->; end
identifier do
  catch
    (one, key: value)
      ->
end

identifier do catch (one, two: 2) when three -> end
identifier do catch (one, two: 2) when three ->; end
identifier do
  catch
    (one, two: 2) when three
      ->
end

identifier do catch (one, two: 2) when three -> four end
identifier do catch (one, two: 2) when three -> four; end
identifier do
  catch (one, two: 2) when three
    ->
      four
end

identifier do catch key: value -> end
identifier do catch key: value ->; end
identifier do
  catch key: value
    ->
end

identifier do catch one, two -> end
identifier do catch one, two ->; end
identifier do
  catch
    one, two
      ->
end

identifier do catch one, key: value -> end
identifier do catch one, key: value ->; end
identifier do
  catch
    one, key: value
      ->
end

identifier do catch one, two: 2 when three -> end
identifier do catch one, two: 2 when three ->; end
identifier do
  catch one, two: 2 when three
    ->
end

identifier do catch one, two: 2 when three -> four end
identifier do catch one, two: 2 when three -> four; end
identifier do
  catch one, two: 2 when three
    ->
      four
end

identifier do
  catch
    (one_a, two_a: 2) when three_a
      ->
        four_a
  catch
    (one_b, two_b: -2) when three_b
      ->
        four_b
end
