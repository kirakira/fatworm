JC = javac -d bin/ -cp bin:lib/antlr-3.4-complete.jar


.PHONY: init parser base dataentity schema storage all

all: parser
	find src -name "*.java"  > sources.txt
	$(JC) @sources.txt

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
	$(JC) src/fatworm/record/RecordFile.java src/fatworm/record/Iterator.java
	$(JC) src/fatworm/storagemanager/StorageManagerInterface.java
	find src/fatworm/storage -name "*.java" > sources.txt
	$(JC) @sources.txt

sort: storage
	$(JC) src/fatworm/absyn/ColName.java src/fatworm/absyn/OrderByColumn.java src/fatworm/absyn/SimpleCol.java src/fatworm/absyn/FieldCol.java
	$(JC) src/fatworm/query/Scan.java src/fatworm/query/TupleComparator.java src/fatworm/query/OrderContainer.java src/fatworm/query/AdvancedOrderContainer.java
	$(JC) src/fatworm/tester/TableScan.java src/fatworm/tester/SortTester.java

clean:
	rm -rf bin/
