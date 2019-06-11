grammar FixRules;
/*
Author: Michael C. Starkie
*/

/*
 The MIT License (MIT)

 Copyright (c) 2016 Global Forge LLC

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */



parseRules      :   fixrules;

fixrules        :   action (';' action)* ';'? ;

action          :   rvalue | conditional | function;
rvalue          :   assign | exchange | unary | userdef | '(' conditional ')' ;

conditional     :   iff then (els)? ;

userdef         :   '{' className=qualifiedName ('#' methodName=Identifier)? '}' ;

userTerm        :   '{' className=qualifiedName '}' ;

qualifiedName   :   Identifier ('.' Identifier)* ;

Identifier      :   Letter (Letter|JavaIDDigit)* ;

assign          :   tag EQ (CAST)? expr;

exchange        :   tag FLP tag;

tag             :   ref* tagref;        // (&382 [1]->)* &375

ref             :   tagref idxref;      // &382 [1]->

tagref          :   '&' tn=tagnum;      // &375

idxref          :   idx REF;            // [1]->

idx             :   '[' i=expr ']';     // [1]

tagnum          :   MINUS? INT;         // 375

unary           :   DEL tg=tag                          #DeleteTag
                |   DEL '&[' (tagnum ',')* tagnum ']'   #DeleteTagSet
                |   ADD '&[' (tagnum ',')* tagnum ']'   #KeepTagSet
                ;

expr            :   expr op=(MUL|DIV) expr      # MulDiv
                |   expr op=(ADD|MINUS) expr    # AddSub
                |   expr CT expr                # Cat
                |   terminal                    # Term
                |   template                    # AutoGen
                |   '(' expr ')'                # Parens
                ;

function		:	split	;

					/* split(&55, /, &55, &65) */
split			:	'split(' sourceTag=tag ',' regex=VAL ',' (tag ',')* tag ')' ;

terminal        :   tag             # myTag
                |   userTerm        # myUserTerm
                |   VAL             # Val
                |   MINUS? INT      # Int
                |   MINUS? FLOAT    # Float
                ;

template        :   DATETIME        # DateTime
                |   DATE            # Date
                ;

iff             :   iff op=(AND|OR) iff     # AndOr
                |   is_equal                # IsEqual
                |   not_equal               # NotEqual
                |   is_greater              # IsGreater
                |   is_less                 # IsLess
                |   is_lessEq               # IsLessEq
                |   is_greatEq              # IsGreaterEq
                |   not                     # NotTrue
                |   is                      # IsTrue
                |   '(' iff ')'             # BoolParens
                ;

is_equal        :   tg=terminal IEQ op=terminal ;
not_equal       :   tg=terminal NEQ op=terminal ;
is_greater      :   tg=terminal GT  op=terminal ;
is_less         :   tg=terminal LT  op=terminal ;
is_lessEq       :   tg=terminal LE  op=terminal ;
is_greatEq      :   tg=terminal GE  op=terminal ;
not             :   NOT tg=terminal ;
is              :   IS tg=terminal ;

then            :   ('?' rvalue) | ('?' '[' fixrules ']');
els             :   (':' rvalue) | (':' '[' fixrules ']');

CAST			: '(int)' ;
FLP				: '<->';
DEL             : '~'  ;
CT              : '|'  ;
IEQ             : '==' ;
NEQ             : '!=' ;
GT              : '>'  ;
LT              : '<'  ;
LE              : '<=' ;
GE              : '>=' ;
EQ              : '='  ;
NOT             : '!'  ;
IS              : '^'  ;
MUL             : '*'  ;
DIV             : '/'  ;
ADD             : '+'  ;
MINUS           : '-'  ;
AND             : '&&' ;
OR              : '||' ;
REF             : '->' ;
DATETIME        : '<DATETIME>' ;
DATE            : '<DATE>' ;
//VAL             : ('"' ('\\"'|~'"')+  '"') ;
//VAL         	: '"' (~('"' | '\\' | '\r' | '\n' | '\u0001') | '\\' ('"' | '\\'))* '"' ;
//                 Any char other than quote, CR, LF or SOH
//                 but quote ok if escaped with backslash.
VAL         	: '"' (~('"' | '\r' | '\n' | '\u0001') | '\\' ('"'))* '"' ; 
WS              : [\t\r\n ]+ -> skip ;
INT             : DIGIT+ ;
FLOAT           : MINUS? INT+ '.' DIGIT+
                | '.' DIGIT+
                ;
DIGIT           :  [0-9] ;
fragment
Letter
    :  '\u0024' |              // $
       '\u0041'..'\u005a' |    // A-Z
       '\u005f' |              // _
       '\u0061'..'\u007a'      // a-z
    ;

fragment
JavaIDDigit
    :  '\u0030'..'\u0039'      // 0-9
    ;
