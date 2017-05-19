JAVAC=javac
JAVA=java

CLASSPATH = .

JEXTDIRS= .
.SUFFIXES: .java .class
.PHONY: clean all lib

SOURCES=$(wildcard mylib/*.java)
CLASSES=$(SOURCES:.java=.class)

all: lib 
lib: $(CLASSES)
#$(JAVAC) $(JFLAGS) -Djava.ext.dirs=$(JEXTDIRS) -cp "$(CLASSPATH)" $^

%.class: %.java
	$(JAVAC) -Djava.ext.dirs=$(JEXTDIRS) $(JFLAGS) -cp "$(CLASSPATH)" $*.java

clean:
	$(RM) $(CLASSES)
