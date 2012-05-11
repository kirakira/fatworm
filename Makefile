JC = javac -d bin/ -cp bin:lib/antlr-3.4-complete.jar


.PHONY: init parser base dataentity schema storage all

init:
	mkdir -p bin

parser: init
	cd src/fatworm/parser; make parser

base: init
	$(JC) src/fatworm/util/ByteLib.java

dataentity: base
	find src/fatworm/dataentity -name "*.java" > sources.txt
	$(JC) @sources.txt

schema: dataentity
	$(JC) src/fatworm/record/Schema.java

storage: schema
	$(JC) src/fatworm/record/RecordFile.java
	$(JC) src/fatworm/storagemanager/StorageManagerInterface.java
	find src/fatworm/storage -name "*.java" > sources.txt
	$(JC) @sources.txt

all: storage
	find src -name "*.java"  > sources.txt
	$(JC) @sources.txt

clean:
	rm -rf bin/
