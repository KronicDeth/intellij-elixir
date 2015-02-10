#!/bin/bash

./fetchIdea.sh

# Erlang version switcher
curl -O https://raw.githubusercontent.com/spawngrid/kerl/master/kerl
chmod a+x kerl
./kerl build 17.4 17.4
./kerl install 17.4 $PWD/erlang/17.4
source $PWD/erlang/17.4/activate

# Elixir version switcher
\curl -sSL https://raw.githubusercontent.com/taylor/kiex/master/install | bash -s
~/.kiex/bin/kiex install 1.0.2
source $HOME/.kiex/elixirs/elixir-1.0.2.env
ERLANG_LIB=`elixir -e "IO.write :code.lib_dir"`

# Run the tests
ant -f intellij-elixir.xml -Derlang.lib=${ERLANG_LIB} -Didea.home=./idea-IC -Djdk.bin=${JAVA_HOME}/bin test.modules

# Was our build successful?
stat=$?

if [ "${TRAVIS}" != true ]; then
    ant -f intellij-elixir.xml -q clean
    rm -rf idea-IC
fi

# Return the build status
exit ${stat}