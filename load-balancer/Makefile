JAVAC=javac
JAVA=java

AWS_SDK=$(HOME)/aws-java-sdk-1.11.131

CLASSPATH = $(AWS_SDK)/lib/aws-java-sdk-1.11.131.jar:../lib/:.:../instrumentation/:$(AWS_SDK)/third-party/lib/

JEXTDIRS=$(AWS_SDK)/third-party/lib/
.SUFFIXES: .java .class
.PHONY: clean run load-balancer all lib instrumentation

SOURCES=$(wildcard *.java)
CLASSES=$(SOURCES:.java=.class)

all: load-balancer

load-balancer: $(CLASSES) lib instrumentation
#$(JAVAC) $(JFLAGS) -Djava.ext.dirs=$(JEXTDIRS) -cp "$(CLASSPATH)" $^

lib:
	(cd ../lib && $(MAKE) )
instrumentation:
	(cd ../instrumentation && $(MAKE) )

run: load-balancer
	$(JAVA) -Djava.ext.dirs=$(JEXTDIRS) -cp "$(CLASSPATH)" LoadBalancer

%.class: %.java
	$(JAVAC) -Djava.ext.dirs=$(JEXTDIRS) $(JFLAGS) -cp "$(CLASSPATH)" $*.java

clean:
	$(RM) *.class
	(cd ../lib && $(MAKE) clean)
