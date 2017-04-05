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


all: $(BUILDDIR) server.jar

files:
	@echo sources: $(SERVER_SOURCES)
	@echo classes: $(SERVER_CLASSES)

$(BUILDDIR):
	mkdir -p $@
	@echo sources: $(SERVER_SOURCES)
	@echo classes: $(SERVER_CLASSES)

run: server.jar
	java -jar server.jar


server.jar: $(BUILDDIR) $(SERVER_CLASSES)
	jar -cvfe $@ pt.tecnico.cnv.server.WebServer -C $(BUILDDIR) .


%.class: $(BUILDDIR) %.java
	$(JAVAC) $(JFLAGS) -d $(BUILDDIR) -cp "$(CLASSPATH)" -sourcepath "$(SOURCEPATH)" $(SOURCEPATH)/$*.java

clean:
	$(RM) -r $(BUILDDIR)
	$(RM) server.jar
