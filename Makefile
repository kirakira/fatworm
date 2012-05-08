JC = javac -d bin/ -cp lib/antlr-3.4-complete.jar


.PHONY: all parser

all:
	find src -name "*.java"  > sources.txt
	$(JC) @sources.txt

parser:
	cd src/fatworm/parser; make parser
