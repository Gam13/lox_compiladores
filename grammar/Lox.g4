grammar Lox;

@header {
package lox_compiladores;
}

// Regras do Parser (Syntax Grammar)
program        : declaration* EOF ;

declaration    : varDecl
               | statement
               ;

varDecl        : 'var' IDENTIFIER ('=' expression)? ';' ;

statement      : exprStmt
               | printStmt
               ;

exprStmt       : expression ';' ;
printStmt      : 'print' expression ';' ;

expression     : assignment ;
assignment     : IDENTIFIER '=' assignment | equality ;
equality       : comparison ( ('!=' | '==') comparison )* ;
comparison     : term ( ('>' | '>=' | '<' | '<=' ) term )* ;
term           : factor ( ('-' | '+') factor )* ;
factor         : unary ( ('/' | '*') unary )* ;
unary          : ('!' | '-') unary | primary ;
primary        : NUMBER
               | STRING
               | 'true'
               | 'false'
               | 'nil'
               | IDENTIFIER
               | '(' expression ')'
               ;

// Regras do Lexer (Lexical Grammar)
IDENTIFIER : [a-zA-Z_] [a-zA-Z0-9_]* ;
NUMBER     : [0-9]+ ('.' [0-9]+)? ;
STRING     : '"' .*? '"' ;

// Palavras-chave
VAR    : 'var' ;
PRINT  : 'print' ;
TRUE   : 'true' ;
FALSE  : 'false' ;
NIL    : 'nil' ;

// Operadores
EQUAL  : '=' ;
EQUAL_EQUAL : '==' ;
BANG_EQUAL : '!=' ;
GREATER : '>' ;
GREATER_EQUAL : '>=' ;
LESS : '<' ;
LESS_EQUAL : '<=' ;
PLUS : '+' ;
MINUS : '-' ;
STAR : '*' ;
SLASH : '/' ;
BANG : '!' ;

// Delimitadores
SEMICOLON : ';' ;
LEFT_PAREN : '(' ;
RIGHT_PAREN : ')' ;

// Ignorar
WS      : [ \t\r\n]+ -> skip ;
COMMENT : '//' ~[\r\n]* -> skip ;