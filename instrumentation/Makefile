JAVAC=javac
JAVA=java

CLASSPATH = $(HOME)/BIT/:$(HOME)/BIT/samples/:.

.SUFFIXES: .java .class
.PHONY: clean run

all: instrumented-raytracer

instrumented-raytracer: raytracer instrumentation
	mkdir -p instrumented
	$(JAVA) -cp "$(CLASSPATH)" Instrument ../raytracer/src instrumented/

# WARNING: bit needs java 1.7
instrumentation: Instrument.java
	$(JAVAC) $(JFLAGS) -cp "$(CLASSPATH)" $^
	

raytracer:
	(cd ../raytracer/ && $(MAKE) all)


%.class: $(BUILDDIR) %.java
	$(JAVAC) $(JFLAGS) -d $(BUILDDIR) -cp "$(CLASSPATH)" -sourcepath "$(SOURCEPATH)" $(SOURCEPATH)/$*.java

clean:
	$(RM) *.class
	$(RM) -r instrumented/
	(cd ../raytracer/ && $(MAKE) clean)
