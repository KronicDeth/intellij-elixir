#!/bin/bash -x

#
# IntelliJ IDEA Community Edition
#

ant -logger org.apache.tools.ant.listener.AnsiColorLogger -f intellij-elixir.xml get.idea

mkdir -p dependencies
pushd dependencies

#
# kerl - Erlang version switcher
#

erlang_version="17.4"

if [ ! -f "${PWD}/erlang/${erlang_version}/activate" ]; then
  curl --remote-name https://raw.githubusercontent.com/spawngrid/kerl/master/kerl
  chmod a+x kerl
  ./kerl build ${erlang_version} ${erlang_version}
  ./kerl install ${erlang_version} $PWD/erlang/${erlang_version}
fi

source "${PWD}/erlang/${erlang_version}/activate"

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
ant -logger org.apache.tools.ant.listener.AnsiColorLogger -f intellij-elixir.xml -Derlang.lib=${erlang_lib} -Didea.home=cache/idea -Djdk.bin=${JAVA_HOME}/bin test.modules

# Was our build successful?
stat=$?

if [ "${TRAVIS}" != true ]; then
    ant -f intellij-elixir.xml -q clean
fi

# Return the build status
exit ${stat}