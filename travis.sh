#!/bin/bash -x

#
# IntelliJ IDEA Community Edition
#

ant -logger org.apache.tools.ant.listener.AnsiColorLogger -f intellij-elixir.xml get.idea install.erlang

mkdir -p dependencies
pushd dependencies

#
# kiex - Elixir version switcher
#

if [ ! -f "$HOME/.kiex/elixirs/elixir-1.0.2.env" ]; then
  \curl -sSL https://raw.githubusercontent.com/taylor/kiex/master/install | bash -s
  ~/.kiex/bin/kiex install 1.0.2
fi

source "$HOME/.kiex/elixirs/elixir-1.0.2.env"
erlang_lib=`elixir -e "IO.write :code.lib_dir"`

popd

# Run the tests
ant -logger org.apache.tools.ant.listener.AnsiColorLogger -f intellij-elixir.xml -Derlang.lib=cache/lib/erlang/lib -Didea.home=cache/idea -Djdk.bin=${JAVA_HOME}/bin test.modules

# Was our build successful?
stat=$?

if [ "${TRAVIS}" != true ]; then
    ant -f intellij-elixir.xml -q clean
fi

# Return the build status
exit ${stat}