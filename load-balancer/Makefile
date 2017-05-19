JAVAC=javac
JAVA=java

AWS_SDK=$(HOME)/aws-java-sdk-1.11.131

CLASSPATH = $(AWS_SDK)/lib/aws-java-sdk-1.11.131.jar:.

JEXTDIRS=$(AWS_SDK)/third-party/lib/
.SUFFIXES: .java .class
.PHONY: clean run load-balancer

all: load-balancer

load-balancer: LoadBalancer.class
#$(JAVAC) $(JFLAGS) -Djava.ext.dirs=$(JEXTDIRS) -cp "$(CLASSPATH)" $^

run: load-balancer
	$(JAVA) -Djava.ext.dirs=$(JEXTDIRS) -cp "$(CLASSPATH)" LoadBalancer

%.class: %.java
	$(JAVAC) -Djava.ext.dirs=$(JEXTDIRS) $(JFLAGS) -cp "$(CLASSPATH)" $*.java

clean:
	$(RM) *.class
