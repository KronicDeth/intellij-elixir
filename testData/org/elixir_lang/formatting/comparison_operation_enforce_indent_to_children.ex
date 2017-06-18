assert links == %{
         "related" => "/api/v1/test-authors/#{author_id}/posts",
         "self" => "/api/v1/test-authors/#{author_id}/relationships/posts"
       }
