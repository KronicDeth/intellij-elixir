ANT=ant
TARGET_XML=intellij-elixir.xml

all: install test build

install:
	${ANT} -logger org.apache.tools.ant.listener.AnsiColorLogger -f ${TARGET_XML} get.idea get.intellij-erlang release.intellij_elixir

build:
	${ANT} -logger org.apache.tools.ant.listener.AnsiColorLogger -f ${TARGET_XML} build.modules

test:
	${ANT} -logger org.apache.tools.ant.listener.AnsiColorLogger -f ${TARGET_XML} test.modules

clean:
	${ANT} -logger org.apache.tools.ant.listener.AnsiColorLogger -f ${TARGET_XML} clean
