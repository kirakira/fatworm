grammar Fatworm;
options {
    output=AST;
    ASTLabelType=CommonTree;
    TokenLabelType=CommonToken;
    backtrack=true; 
    memoize=true;
    k=1;
}

tokens {
    ConstValue;
    Char;
    VarChar;
    Decimal;
    CreateDatabase; 
    UseDatabase; 
    DropDatabase;
    CreateTable;
    DropTable;
    CreateDefList;
    PrimaryKey;
    ColumnDefinition;
    ColumnDescription;
    DefaultValue;
    InsertStmt;
    TableName;
    ValueList;
    IdList;
    DeleteStmt;
    Condition;
    AssignList;
    Assign;
    CreateIndex;
    UpdateStmt;
    Query;
    SelectColumn;
    From;
    WhereCondition;
    GroupBy;
    HavingCondition;
    OrderBy;
    OrderKeyList;
    OrderKey;
    SelectExprList;
    SelectExpr;
    SimpleExpr;
    RenameValue;
    AllColumn;
    ColumnName;
    SimpleColumn;
    SimpleValue;
    FieldColumn;
    TableRefList;
    TableRef;
    SimpleRef;
    RenameRef;
    QueryRef;
    OrList;
    AndList;
    Compare;
    Exit;
    Not;
    Exist;
    CompareAny;
    CompareAll;
    In;
    LE;
    GE;
    NE;
    LT;
    GT;
    EQ;
    ASC;
    DESC;
    Func;
    ColNameList;
}

@header {
package fatworm.parser;
import java.util.HashMap;
import org.antlr.runtime.tree.*;
import org.antlr.runtime.Token;
}

@lexer::header{package fatworm.parser;}

@members {
}



prog
    : stmt+
    ;
    
 stmt
     : database_stmt 
     | table_stmt
     | query
     | insert_stmt
     | delete_stmt
     | update_stmt
     ;

database_stmt
    :   CREATE DATABASE id=IDENTIFIER-> ^(CreateDatabase $id)
    |   USE id=IDENTIFIER -> ^(UseDatabase $id)
    |   DROP DATABASE id=IDENTIFIER -> ^(DropDatabase $id)
    ;

table_stmt
    : CREATE TABLE IDENTIFIER '(' create_definition_list ')' -> ^(CreateTable IDENTIFIER create_definition_list)
    | DROP TABLE id_list -> ^(DropTable id_list)
    ;

create_definition_list
    : create_definition  (',' create_definition )* -> ^(CreateDefList create_definition+)
    ;


create_definition
    : column_definition
    | PRIMARY KEY '(' IDENTIFIER ')' -> ^(PrimaryKey IDENTIFIER)
    ;

column_definition
    : IDENTIFIER data_type (column_description)* -> ^(ColumnDefinition IDENTIFIER data_type column_description*)
    ;

column_description
    : NULL -> ^(ColumnDescription NULL) 
    | NOT NULL -> ^(ColumnDescription NOT NULL)
    | DEFAULT const_value -> ^(ColumnDescription DefaultValue const_value)
    | AUTO_INCREMENT -> ^(ColumnDescription AUTO_INCREMENT)
    ;

data_type
    : INT -> INT
    | FLOAT -> FLOAT
    | CHAR '(' INTEGER_LITERAL ')' -> ^(Char INTEGER_LITERAL)
    | VARCHAR '(' INTEGER_LITERAL ')' -> ^(VarChar INTEGER_LITERAL)
    | DATETIME -> DATETIME
    | TIMESTAMP -> TIMESTAMP
    | BOOLEAN -> BOOLEAN
    | DECIMAL '(' INTEGER_LITERAL ')' -> ^(Decimal INTEGER_LITERAL)
    | decimal_with_point
    ;

decimal_with_point
    : DECIMAL '(' i=INTEGER_LITERAL ',' d=INTEGER_LITERAL ')' -> ^(Decimal $i $d)
    ;

insert_stmt
    : INSERT INTO IDENTIFIER VALUES'(' value_list')' -> ^(InsertStmt IDENTIFIER value_list)
    | INSERT INTO IDENTIFIER '(' col_name_list ')' VALUES '(' value_list ')' -> ^(InsertStmt IDENTIFIER col_name_list value_list)
    | INSERT INTO IDENTIFIER '(' query ')' -> ^(InsertStmt IDENTIFIER query) 
    ;

col_name_list
	: col_name (',' col_name)* -> ^(ColNameList col_name+)
	;
	
value_list
    : value  (',' value )* -> ^(ValueList value+)
    ;

id_list
    : IDENTIFIER (',' IDENTIFIER)* -> ^(IdList IDENTIFIER+)
    ;

delete_stmt
    : DELETE FROM IDENTIFIER  (WHERE bool_expr)? -> ^(DeleteStmt IDENTIFIER ^(Condition bool_expr))
    ;

update_stmt
    :UPDATE IDENTIFIER SET assign_list (WHERE bool_expr)? -> ^(UpdateStmt IDENTIFIER ^(Condition bool_expr) assign_list)
    ;

assign_list
    :assign (',' assign)* -> ^(AssignList assign+)
    ;

assign
    : IDENTIFIER '=' const_value -> ^(Assign IDENTIFIER const_value)
    ;

index_stmt
    : CREATE UNIQUE? INDEX index=IDENTIFIER ON table=IDENTIFIER(column=IDENTIFIER) -> ^(CreateIndex $index $table $column UNIQUE?)
    ; 

query
    :SELECT DISTINCT? select_expr_list (select_suffix)*
        -> ^(Query ^(SelectColumn select_expr_list)  select_suffix*   DISTINCT?)
    ; 

select_suffix
    :(FROM table_ref_product) -> ^(From table_ref_product)
    |(GROUP BY col_name) -> ^(GroupBy col_name)
    |(WHERE bool_expr) -> ^(WhereCondition bool_expr)
    |(HAVING bool_expr) -> ^(HavingCondition bool_expr)
    |(ORDER BY order_key_list)-> ^(OrderBy order_key_list)
    ;

order_key_list
    : order_key (',' order_key)* -> ^(OrderKeyList order_key+)
    ;


order_key
    : col_name ASC -> ^(OrderKey col_name ASC)
    | col_name DESC -> ^(OrderKey col_name DESC)
    | col_name -> ^(OrderKey col_name ASC)
    ;

select_expr_list
    : select_expr (',' select_expr)* -> ^(SelectExprList select_expr+)
    ;

select_expr
    :value -> ^(SelectExpr ^(SimpleValue value))
    |value AS IDENTIFIER -> ^(SelectExpr ^(RenameValue value IDENTIFIER))
    |'*' -> ^(SelectExpr ^(AllColumn))
    ;

func
    : AVG
    | COUNT
    | MIN
    | MAX
    | SUM
    ;

col_name
    : field_column_name -> field_column_name
    |IDENTIFIER -> ^(ColumnName ^(SimpleColumn IDENTIFIER))
    ;

field_column_name
    : table=IDENTIFIER '.' column=IDENTIFIER -> ^(ColumnName ^(FieldColumn  $table $column))
    ;

table_ref_product
    : table_ref (',' table_ref)* -> ^(TableRefList table_ref+)
    ;


table_ref
    : table_rename
    | IDENTIFIER -> ^(TableRef ^(SimpleRef IDENTIFIER))
    | '(' query ')' AS IDENTIFIER -> ^(TableRef ^(QueryRef query IDENTIFIER))
    ;

table_rename
    : table=IDENTIFIER AS alias=IDENTIFIER -> ^(TableRef ^(RenameRef $table $alias))
    ;

bool_expr
    : and_bool_expr (OR and_bool_expr)* -> ^(OrList and_bool_expr+)
    ;


and_bool_expr
    : atom_bool_expr (AND atom_bool_expr)* -> ^(AndList atom_bool_expr+)
    ;

atom_bool_expr
    : EXISTS '(' query ')'-> ^(Exist query)
    | NOT EXISTS '(' query ')' -> ^(Not ^(Exist query))
    | value cop ANY '(' query ')' -> ^(CompareAny value query cop)
    | value cop ALL '(' query ')' -> ^(CompareAll value query cop)
    | value IN '(' query ')' -> ^(In value query)
    | '(' bool_expr ')' -> bool_expr
    ;

compare
    : v1=value cop v2=value -> ^(Compare $v1 $v2 cop)
    ;
    
cop
    : '<=' -> LE
    | '>=' -> GE
    | '<>' -> NE
    | '<' -> LT
    | '>' -> GT
    | '=' -> EQ
    ;

        
value
    : mult_value (('+'|'-')^ mult_value)*
    ;
        
mult_value
    : atom_value (('*'|'/'|'%')^ atom_value)*
    ;

atom_value
    : '(' value ')' -> value
    | col_name -> col_name
    | const_value -> ^(ConstValue const_value)
    | '(' query ')' -> query
    | func '(' col_name ')' -> ^(Func ^(func) col_name)
    ;

const_value
    :INTEGER_LITERAL
    |STRING_LITERAL
    |FLOATING_POINT_LITERAL
    |TIMESTAMP_LITERAL
    |TRUE
    |FALSE
    |NULL
    |DEFAULT
    ;


SELECT : 
        S E L E C T;
CREATE : 
        C R E A T E;
DATABASE :
        D A T A B A S E;
USE :
        U S E;
DROP :
        D R O P;
TABLE :
        T A B L E;
PRIMARY :
        P R I M A R Y;
KEY :
        K E Y;
NOT :
        N O T;
NULL :
        N U L L;
DEFAULT :
        D E F A U L T;
AUTO_INCREMENT :
        A U T O'_'I N C R E M E N T;
INT :
        I N T;
FLOAT :
        F L O A T;
CHAR :
        C H A R;
DATETIME :
        D A T E T I M E;
BOOLEAN :
        B O O L E A N;
DECIMAL :
        D E C I M A L;
TIMESTAMP :
        T I M E S T A M P;
VARCHAR :
        V A R C H A R;
INSERT :
        I N S E R T;
INTO :
        I N T O;
VALUES :
        V A L U E S;
DELETE :
        D E L E T E;
UPDATE :
        U P D A T E;
FROM :
        F R O M;
WHERE :
        W H E R E;
SET :
        S E T;
UNIQUE :
        U N I Q U E;
INDEX :
        I N D E X;
ON :
        O N;
DISTINCT :
        D I S T I N C T;
GROUP :
        G R O U P;
BY :
        B Y;
HAVING :
        H A V I N G;
ORDER :
        O R D E R;
ASC :
        A S C;
DESC :
        D E S C;
AS :
        A S;
AVG :
        A V G;
COUNT :
        C O U N T ;
MIN :
        M I N;
MAX :
        M A X;
SUM :
        S U M;
AND :
        A N D;
OR :
        O R;
EXISTS :
        E X I S T S;
ANY :
        A N Y;
IN :
        I N;
ALL :
        A L L;
TRUE :
        T R U E;
FALSE :
        F A L S E;

fragment A
    : ('A'|'a');
fragment B
    : ('B'|'b');
fragment C
    : ('C'|'c');
fragment D
    : ('D'|'d');
fragment E
    : ('E'|'e');
fragment F
    : ('F'|'f');
fragment G
    : ('G'|'g');
fragment H
    : ('H'|'h');
fragment I
    : ('I'|'i');
fragment J
    : ('J'|'j');
fragment K
    : ('K'|'k');
fragment L
    : ('L'|'l');
fragment M
    : ('M'|'m');
fragment N
    : ('N'|'n');
fragment O
    : ('O'|'o');
fragment P
    : ('P'|'p');
fragment Q
    : ('Q'|'q');
fragment R
    : ('R'|'r');
fragment S
    : ('S'|'s');
fragment T
    : ('T'|'t');
fragment U
    : ('U'|'u');
fragment V
    : ('V'|'v');
fragment W
    : ('W'|'w');
fragment X
    : ('X'|'x');
fragment Y
    : ('Y'|'y');
fragment Z
    : ('Z'|'z');

       
TIMESTAMP_LITERAL
    :   '\'' DIGIT DIGIT DIGIT DIGIT'-'DIGIT DIGIT'-'DIGIT DIGIT '\'';

fragment
DIGIT
    :('0'..'9');

INTEGER_LITERAL : ('0' | '1'..'9' ('0'..'9')*);

STRING_LITERAL
    :   '"' (~('"'))* '"'
    | '\'' (~('\''))* '\'' ;

IDENTIFIER
	:	LETTER (LETTER|'0'..'9')*
	;
	
fragment
LETTER
	:	'$'
	|	'A'..'Z'
	|	'a'..'z'
	|	'_'
	;

FLOATING_POINT_LITERAL
    :   ('0'..'9')+ '.' ('0'..'9')* Exponent? 
    |   '.' ('0'..'9')+ Exponent? 
    |   ('0'..'9')+ Exponent 
	;

fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;


WS  :   (' '|'\t'|'\r'|'\n')+ {skip();} ;
