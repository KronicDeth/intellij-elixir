#!/bin/bash

./fetchIdea.sh

# Run the tests
ant -f intellij-elixir.xml -Didea.home=./idea-IC -Djdk.bin=${JAVA_HOME}/bin test.modules

# Was our build successful?
stat=$?

if [ "${TRAVIS}" != true ]; then
    ant -f intellij-elixir.xml -q clean
    rm -rf idea-IC
fi

# Return the build status
exit ${stat}