defmodule PhoneNumberPoolingTest do
  use ExUnit.Case

  describe "available_or_purchased_number/3" do
    for test_case <- [
          %{partition_strategy: "area_code", expected_phone_number: "+15550000001"},
          %{partition_strategy: "toll_free", expected_phone_number: "+18005550002"}
        ] do
      @partition_strategy test_case.partition_strategy
      @expected_phone_number test_case.expected_phone_number

      test "sets webhooks on a " <> @partition_strategy <> "-type phone_number_pools record" do
        assert available_or_purchased_number(random_pool_name<caret>()) == @expected_phone_number
      end

      test "informs caller that no numbers are available for " <> @partition_strategy do
        assert available_or_purchased_number(random_pool_name()) == @expected_phone_number
      end
    end
  end

  defp random_pool_name do
    "pool"
  end

  defp available_or_purchased_number(pool_name) do
    pool_name
  end
end
