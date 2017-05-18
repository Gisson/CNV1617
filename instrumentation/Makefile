JAVAC=javac
JAVA=java

AWS_SDK=$(HOME)/aws-java-sdk-1.11.131

CLASSPATH = $(HOME)/BIT/:$(HOME)/BIT/samples/:.:../server/build/classes:$(AWS_SDK)/lib/aws-java-sdk-1.11.131.jar

JEXTDIRS=$(AWS_SDK)/third-party/lib/
.SUFFIXES: .java .class
.PHONY: clean run

all: instrumented-raytracer

instrumented-raytracer: raytracer instrumentation
	mkdir -p instrumented
	$(JAVA) -Djava.ext.dirs=$(JEXTDIRS) -cp "$(CLASSPATH)" Instrument ../raytracer/src instrumented/

# WARNING: bit needs java 1.7
instrumentation: Instrument.java
	$(JAVAC) $(JFLAGS) -Djava.ext.dirs=$(JEXTDIRS) -cp "$(CLASSPATH)" $^
	

raytracer:
	(cd ../raytracer/ && $(MAKE) all)


%.class: $(BUILDDIR) %.java
	$(JAVAC) -Djava.ext.dirs=$(JEXTDIRS) $(JFLAGS) -d $(BUILDDIR) -cp "$(CLASSPATH)" -sourcepath "$(SOURCEPATH)" $(SOURCEPATH)/$*.java

clean:
	$(RM) *.class
	$(RM) -r instrumented/
	(cd ../raytracer/ && $(MAKE) clean)
