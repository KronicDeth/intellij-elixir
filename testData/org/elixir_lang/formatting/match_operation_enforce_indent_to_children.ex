assert {:error, %Document{errors: errors}} = TestAuthors.list(
         %{
           filters: %{
             "first_invalid_filter" => "true",
             "id" => "1,2",
             "second_invalid_filter" => "false"
           }
         }
       )
