root=../../..
parser: Fatworm.g
	java -cp $(root)/lib/antlr-3.4-complete.jar org.antlr.Tool Fatworm.g
	javac -cp $(root)/lib/antlr-3.4-complete.jar *.java -d $(root)/bin/

run:
	java -cp $(root)/lib/antlr-3.4-complete.jar:$(root)/bin/ fatworm.parser.ParserTest
clean:
	rm -r $(root)/bin/*
