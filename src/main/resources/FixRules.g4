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
														
action          :   assign | exchange | unary | conditional | userdef;

conditional     :   iff then (els)? ;

userdef         :   '{' className=qualifiedName ('#' methodName=Identifier)? '}' ;

userTerm        :   '{' className=qualifiedName '}' ;

qualifiedName   :   Identifier ('.' Identifier)* ;

Identifier      :   Letter (Letter|JavaIDDigit)* ;
			
assign          :   tag EQ (CAST)? expr;

exchange        :   tag FLP tag;

tag             :   ref* tagref;        // (&382[1]->)*&375  

ref             :   tagref idxref;      // &382[1]->

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
		
then            :   ('?' action) | ('?' '[' fixrules ']');
els             :   (':' action) | (':' '[' fixrules ']');

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
VAL             : ('"' ('""'|~'"')+  '"') 	;
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
       '\u0061'..'\u007a' |    // a-z
       '\u00c0'..'\u00d6' |    // international
       '\u00d8'..'\u00f6' |
       '\u00f8'..'\u00ff' |
       '\u0100'..'\u1fff' |
       '\u3040'..'\u318f' |
       '\u3300'..'\u337f' |
       '\u3400'..'\u3d2d' |
       '\u4e00'..'\u9fff' |
       '\uf900'..'\ufaff'
    ;

fragment
JavaIDDigit
    :  '\u0030'..'\u0039' |    // 0-9  
       '\u0660'..'\u0669' |    // International
       '\u06f0'..'\u06f9' |
       '\u0966'..'\u096f' |
       '\u09e6'..'\u09ef' |
       '\u0a66'..'\u0a6f' |
       '\u0ae6'..'\u0aef' |
       '\u0b66'..'\u0b6f' |
       '\u0be7'..'\u0bef' |
       '\u0c66'..'\u0c6f' |
       '\u0ce6'..'\u0cef' |
       '\u0d66'..'\u0d6f' |
       '\u0e50'..'\u0e59' |
       '\u0ed0'..'\u0ed9' |
       '\u1040'..'\u1049'
   ;
						

