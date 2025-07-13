grammar Lox;

@header {
package lox_compiladores;
}

// Regras do Parser (Syntax Grammar do livro)
program
    : declaration* EOF
    ;

declaration
    : classDecl
    | funDecl
    | varDecl
    | statement
    ;

classDecl
    : 'class' IDENTIFIER ('<' IDENTIFIER)? '{' function* '}'
    ;

funDecl
    : 'fun' function
    ;

varDecl
    : 'var' IDENTIFIER ('=' expression)? ';'
    ;

statement
    : exprStmt
    | forStmt
    | ifStmt
    | printStmt
    | returnStmt
    | whileStmt
    | block
    ;

exprStmt
    : expression ';'
    ;

forStmt
    : 'for' '(' ( varDecl | exprStmt | ';' ) expression? ';' expression? ')' statement
    ;

ifStmt
    : 'if' '(' expression ')' statement ('else' statement)?
    ;

printStmt
    : 'print' expression ';'
    ;

returnStmt
    : 'return' expression? ';'
    ;

whileStmt
    : 'while' '(' expression ')' statement
    ;

block
    : '{' declaration* '}'
    ;

expression
    : assignment
    ;

assignment
    : ( call '.' )? IDENTIFIER '=' assignment
    | logic_or
    ;

logic_or
    : logic_and ( 'or' logic_and )*
    ;

logic_and
    : equality ( 'and' equality )*
    ;

equality
    : comparison ( ( '!=' | '==' ) comparison )*
    ;

comparison
    : term ( ( '>' | '>=' | '<' | '<=' ) term )*
    ;

term
    : factor ( ( '-' | '+' ) factor )*
    ;

factor
    : unary ( ( '/' | '*' ) unary )*
    ;

unary
    : ( '!' | '-' ) unary
    | call
    ;

call
    : primary ( '(' arguments? ')' | '.' IDENTIFIER )*
    ;

primary
    : 'true'
    | 'false'
    | 'nil'
    | 'this'
    | NUMBER
    | STRING
    | IDENTIFIER
    | '(' expression ')'
    | 'super' '.' IDENTIFIER
    ;

function
    : IDENTIFIER '(' parameters? ')' block
    ;

parameters
    : IDENTIFIER ( ',' IDENTIFIER )*
    ;

arguments
    : expression ( ',' expression )*
    ;

// Regras do Lexer (Lexical Grammar do livro)
IDENTIFIER : [a-zA-Z_] [a-zA-Z0-9_]* ;
NUMBER : [0-9]+ ( '.' [0-9]+ )? ;
STRING : '"' .*? '"' ;

// Palavras-chave
CLASS : 'class' ;
FUN : 'fun' ;
VAR : 'var' ;
FOR : 'for' ;
IF : 'if' ;
ELSE : 'else' ;
PRINT : 'print' ;
RETURN : 'return' ;
WHILE : 'while' ;
TRUE : 'true' ;
FALSE : 'false' ;
NIL : 'nil' ;
THIS : 'this' ;
SUPER : 'super' ;
OR : 'or' ;
AND : 'and' ;

// Operadores e símbolos
PLUS : '+' ;
MINUS : '-' ;
STAR : '*' ;
SLASH : '/' ;
BANG : '!' ;
BANG_EQUAL : '!=' ;
EQUAL : '=' ;
EQUAL_EQUAL : '==' ;
GREATER : '>' ;
GREATER_EQUAL : '>=' ;
LESS : '<' ;
LESS_EQUAL : '<=' ;
DOT : '.' ;
COMMA : ',' ;
SEMICOLON : ';' ;
LEFT_PAREN : '(' ;
RIGHT_PAREN : ')' ;
LEFT_BRACE : '{' ;
RIGHT_BRACE : '}' ;

// Ignorar
WS : [ \t\r\n]+ -> skip ;
COMMENT : '//' ~[\r\n]* -> skip ;