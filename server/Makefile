JAVAC=javac
JFLAGS=

.SUFFIXES: .java .class
.PHONY: clean run

#CLASSPATH = src/

# this is where javac searches for java files
SOURCEPATH = src

BUILDDIR=target/

# this is where make looks for files in prerequisites and targets
VPATH=$(BUILDDIR):$(SOURCEPATH)


SERVER_SOURCES=$(wildcard $(SOURCEPATH)/pt/tecnico/cnv/server/*.java)
SERVER_CLASSES=$(SERVER_SOURCES:$(SOURCEPATH)/%.java=%.class)


# FIXME: using ant because travisCI does not like the Makefile despite it working both on my machine and on AWS

all:
	ant compile jar
#all: $(BUILDDIR) server.jar

files:
	@echo sources: $(SERVER_SOURCES)
	@echo classes: $(SERVER_CLASSES)

$(BUILDDIR):
	mkdir -p $@
	@echo sources: $(SERVER_SOURCES)
	@echo classes: $(SERVER_CLASSES)

#run: server.jar
#	java -jar server.jar
run:
	ant run


server.jar: $(BUILDDIR) $(SERVER_CLASSES)
	jar -cvfe $@ pt.tecnico.cnv.server.WebServer -C $(BUILDDIR) .


%.class: $(BUILDDIR) %.java
	$(JAVAC) $(JFLAGS) -d $(BUILDDIR) -cp "$(CLASSPATH)" -sourcepath "$(SOURCEPATH)" $(SOURCEPATH)/$*.java

clean:
	$(RM) -r $(BUILDDIR)
	$(RM) server.jar
	ant clean
