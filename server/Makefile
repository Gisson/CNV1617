JAVAC=javac
JFLAGS=

.SUFFIXES: .java .class
.PHONY: clean run server raytracer instrumented-rt files

CLASSPATH = $(HOME)/BIT/:$(HOME)/BIT/samples/:src/:../raytracer/src:../lib

# this is where javac searches for java files
SOURCEPATH = src

BUILDDIR=target/

# this is where make looks for files in prerequisites and targets
VPATH=$(BUILDDIR):$(SOURCEPATH)


SERVER_SOURCES=$(wildcard $(SOURCEPATH)/pt/tecnico/cnv/server/*.java)
SERVER_CLASSES=$(SERVER_SOURCES:$(SOURCEPATH)/%.java=%.class)


# FIXME: using ant because travisCI does not like the Makefile despite it working both on my machine and on AWS

all: server instrumented-rt

server: raytracer lib
	ant compile
#all: $(BUILDDIR) server.jar

raytracer:
	(cd ../raytracer/ && $(MAKE) all)

instrumented-rt:
	(cd ../instrumentation/ && $(MAKE) all)

files:
	@echo sources: $(SERVER_SOURCES)
	@echo classes: $(SERVER_CLASSES)

lib:
	(cd ../lib && $(MAKE) )

$(BUILDDIR):
	mkdir -p $@
	@echo sources: $(SERVER_SOURCES)
	@echo classes: $(SERVER_CLASSES)

#run: server.jar
#	java -cp "$(CLASSPATH)" -jar server.jar
run: server instrumented-rt
	_JAVA_OPTIONS="-XX:-UseSplitVerifier" ant run
#_JAVA_OPTIONS="-XX:+PrintFlagsFinal -XX:MaxHeapSize=512m -XX:-UseSplitVerifier" ant run
#_JAVA_OPTIONS="-XX:MaxHeapSize=512m -XX:-UseSplitVerifier" ant run
# instrumented classes need _JAVA_OPTIONS="-XX:-UseSplitVerifier" to run

#server.jar: raytracer $(BUILDDIR) $(SERVER_CLASSES)
#	jar -cvfe $@ pt.tecnico.cnv.server.WebServer -C $(BUILDDIR) .


%.class: $(BUILDDIR) %.java
	$(JAVAC) $(JFLAGS) -d $(BUILDDIR) -cp "$(CLASSPATH)" -sourcepath "$(SOURCEPATH)" $(SOURCEPATH)/$*.java

clean:
	$(RM) -r $(BUILDDIR)
	$(RM) server.jar
	ant clean
	(cd ../raytracer/ && $(MAKE) clean)
	(cd ../instrumentation/ && $(MAKE) clean)
	(cd ../lib && $(MAKE) clean)
