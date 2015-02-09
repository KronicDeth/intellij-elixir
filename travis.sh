#!/bin/bash

./fetchIdea.sh

# Run the tests
ant -f intellij-elixir.xml -Didea.home=./idea-IC -Djdk.home.idea_ic-135.123=./idea-IC test.modules

# Was our build successful?
stat=$?

if [ "${TRAVIS}" != true ]; then
    ant -f intellij-elixir.xml -q clean
    rm -rf idea-IC
fi

# Return the build status
exit ${stat}