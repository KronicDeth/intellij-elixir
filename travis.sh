#!/bin/bash -x

#
# IntelliJ IDEA Community Edition
#

ant -logger org.apache.tools.ant.listener.AnsiColorLogger -f intellij-elixir.xml get.idea install.erlang install.elixir

# Ensure erlang binaries are on PATH for Elixir and Elixir is on path for tests
export PATH=$PWD/cache/usr/local/bin:$PWD/cache/bin:$PATH

# Run the tests
ant -logger org.apache.tools.ant.listener.AnsiColorLogger -f intellij-elixir.xml -Djdk.bin=${JAVA_HOME}/bin test.modules

# Was our build successful?
stat=$?

if [ "${TRAVIS}" != true ]; then
    ant -f intellij-elixir.xml -q clean
fi

# Return the build status
exit ${stat}