JAVAC=javac
JAVA=java

JFLAGS=-Xlint:deprecation

AWS_SDK=$(HOME)/aws-java-sdk-1.11.132

CLASSPATH = $(HOME)/BIT/:$(HOME)/BIT/samples/:.:../server/build/classes:$(AWS_SDK)/lib/aws-java-sdk-1.11.132.jar

JEXTDIRS=$(AWS_SDK)/third-party/lib/
.SUFFIXES: .java .class
.PHONY: clean run

all: instrumented-raytracer

instrumented-raytracer: raytracer instrumentation
	mkdir -p instrumented
	$(JAVA) -Djava.ext.dirs=$(JEXTDIRS) -cp "$(CLASSPATH)" Instrument ../raytracer/src instrumented/

# WARNING: bit needs java 1.7
instrumentation: Instrument.java MSS.class
	$(JAVAC) $(JFLAGS) -Djava.ext.dirs=$(JEXTDIRS) -cp "$(CLASSPATH)" $<
	
MSS.class: MSS.java

MSS: MSS.class

raytracer:
	(cd ../raytracer/ && $(MAKE) all)


%.class: %.java
	$(JAVAC) -Djava.ext.dirs=$(JEXTDIRS) $(JFLAGS) -cp "$(CLASSPATH)" $*.java

clean:
	$(RM) *.class
	$(RM) -r instrumented/
	(cd ../raytracer/ && $(MAKE) clean)
