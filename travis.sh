#!/bin/bash -x

mkdir -p dependencies
pushd dependencies

#
# IntelliJ IDEA Community Edition
#

idea_version="14.0.2"
idea_tar_ball="ideaIC-${idea_version}.tar.gz"
idea_path="idea-IC"

wget --timestamping http://download-cf.jetbrains.com/idea/${idea_tar_ball}

if [ ${idea_tar_ball} -nt ${idea_path} ]; then
    tar zxf ${idea_tar_ball}
    rm -rf ${idea_tar_ball}

    # Move the versioned IDEA folder to a known location
    versioned_idea_path=$(find . -name 'idea-IC*' | head -n 1)
    rm ${idea_path}
    ln -s ${versioned_idea_path} ${idea_path}
fi

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
ant -f intellij-elixir.xml -Derlang.lib=${erlang_lib} -Didea.home=dependencies/${idea_path} -Djdk.bin=${JAVA_HOME}/bin test.modules

# Was our build successful?
stat=$?

if [ "${TRAVIS}" != true ]; then
    ant -f intellij-elixir.xml -q clean
fi

# Return the build status
exit ${stat}