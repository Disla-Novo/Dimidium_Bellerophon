grammar Jupitore;

// rules
// notes. so we have macros+ means more than one macros may be in. and EOF means end of file. so it reads the full thingprogram
    program
    : (NEWLINE | WS)* macro ( (NEWLINE | WS)* macro )* (NEWLINE | WS)* EOF
    ;

// this should be how it looks
   macro
    : TITLE STRING (NEWLINE | WS)+
      statement*
     MEND (NEWLINE | WS)*
  ;
//macro
   // : TITLE STRING (NEWLINE | WS)+
    //    ((statement (NEWLINE | WS)*) | NEWLINE)*
    //  MEND (NEWLINE | WS)*
  //  ;

// zero or more statements: statement*
// MEND macro ends with M.end

// defining the statements or actions below
// | this means its an option
statement
    :HOME (ARROW coordList SEMICOLON?)? NEWLINE // ? means optional. Home or Home->x=0
    | MOVE DIRECTION (ARROW coordList (SEMICOLON)?)? NEWLINE  // semicolon optional
    | HEAT TARGET EQUALS expr NEWLINE  // heat extruder: 200 like m140 s60 ; bed
    | LEVEL NEWLINE // level
    | CALL STRING NEWLINE // m.call "Macro Name". so you can call a macros in a macros
                   // | REPEAT NUMBER NEWLINE (statement)* END (NEWLINE | EOF)  // <--- new trying out repeat blocks 3/12/2026
                  //| IF condition NEWLINE statement* ENDIF NEWLINE // for conditional statements - FIXED: NEWLINE instead of SEMICOLON
    | SET_NOZZLE EQUALS expr NEWLINE
    | SET_FILAMENT EQUALS expr NEWLINE
    | SET_LAYER_HEIGHT EQUALS expr NEWLINE
    | SET_EXTRUSION_MULTIPLIER EQUALS expr NEWLINE
    | layer_statement   // new
    | ENABLE_AUTO_EXTRUDE EQUALS expr NEWLINE
    | repeat_statement    // testing
    | if_statement        // testing
    | brepeat_statement  // testing
    | PAUSE NEWLINE
    | RESPOND MSG STRING NEWLINE // to print a message
    | RESUME NEWLINE
    | SET_HEATER TARGET EQUALS expr NEWLINE // set to target temp. m600 more advanced
    | WAITFORTEMP TARGET NEWLINE // wait for target temp m109
    | COOLDOWN (TARGET)? NEWLINE // added here
    | MOVEEX coordList SEMICOLON? NEWLINE // moves or extrude with the given coords G1 - FIXED: optional semicolon
    | ABSOLUTE NEWLINE   // outputs G90 which is an absolute positioning
    | RELATIVE NEWLINE   // outputs G91 which is a relative positioning
    | TIMEOUT_SET expr NEWLINE                        // Set idle timeout
    | RELATIVEEXTRUSION NEWLINE // this is command M83
    | LOAD_BED_MESH STRING NEWLINE            // BED_MESH_PROFILE LOAD=default
    | SET_PRESSURE_ADVANCE expr NEWLINE     //exm:  SET_PRESSURE_ADVANCE ADVANCE=0.04
    | RESET_EXTRUDER NEWLINE                  // G92 E0
    | DWELL expr ( 'S' | 's' | 'MS' | 'ms' )? NEWLINE// new 
    | BED_MESH_CALIBRATE NEWLINE    // BED_MESH_CALIBRATE command
    | PROBE_CALIBRATE NEWLINE    // PROBE_CALIBRATE
       // Added - FIXED: lowercase 'cooldown' deleted cooldown. 
    | SET_SPEED EQUALS expr NEWLINE // 6/17/26  changed to expr to test for user defined variables. 
    | SET_FAN EQUALS expr NEWLINE     // Added        // Added - FIXED: removed SPEED token
    | PRINTFILE STRING NEWLINE             // Added - prints a G-code file via SD card
    | assignment   // new rule for variable assignment 6/17/2026
    ;


// new rule 6/17/26
 assignment
    : ID EQUALS expr NEWLINE
    ;

  repeat_statement
    : REPEAT NUMBER NEWLINE statement_block END (NEWLINE | EOF)
    ;
 brepeat_statement
    : BREPEAT NUMBER NEWLINE statement_block END (NEWLINE | EOF)
    ;        

    layer_statement
    : LAYER NUMBER NEWLINE statement_block END (NEWLINE | EOF)
    ;
statement_block
    : statement+
    ;

if_statement
    : IF condition NEWLINE statement_block ENDIF (NEWLINE | EOF)
    ;

// coordinate lists
coordList
    : coord (WS? coord)* //optional spaces between coordinates. 
    ;

// example of above is Home->x=0 y=10 z=5;
// changing this whole thing up 4/21/2026
//coord
   // : (X | Y | Z | E) (EQUALS | PLUSEQ | MINUSEQ | MULTEQ | DIVEQ) expr  // Added E for extruder
  //  ;

coord
    : X (EQUALS | PLUSEQ | MINUSEQ | MULTEQ | DIVEQ) expr
    | Y (EQUALS | PLUSEQ | MINUSEQ | MULTEQ | DIVEQ) expr
    | Z (EQUALS | PLUSEQ | MINUSEQ | MULTEQ | DIVEQ) expr
    | E (EQUALS | PLUSEQ | MINUSEQ | MULTEQ | DIVEQ)? expr?
    ;


// so putting a mark so i can reunderstand what i did
// we have our addsub #muldiv and what not
// this tells ANTLR (pls) to generate specific methods to it can be handled and altered
// RECURSIONNN. i am bad at recursion but this works so don't touch
// expr is defined by using expr itself. so now its nesting until it hits a terminal


// find answers. '^' expr and then # power
// it may be POW : '^' or something similar?
//expr
  //  : <assoc=right> expr '+' expr   # addSub
   // | <assoc=right> expr '-' expr   # addSub
   // | <assoc=right> expr '*' expr   # mulDiv
   // | <assoc=right> expr '/' expr   # mulDiv
   // | func '(' expr ')'              # funcCall
   // | '(' expr ')'                   # parens
   // | NUMBER                         # number
  //  | 'i'                            # iterator
   // ;

   // our new one is 

   expr
    : '(' expr ')'                    # parens    
    | func '(' expr ')'               # funcCall  
    | <assoc=right> expr '^' expr     # power    
    | expr op=('*'|'/') expr          # mulDiv    //  Multiplication/Division
    | expr op=('+'|'-') expr          # addSub    // Addition/Subtraction
    | NUMBER                          # number    
    | 'i'                             # iterator  //The loop variable
    | ID                              # variable  // Variable reference 6/17/2026
    ;

// these are our functions. we may add new ones but itll be computed in compute.java

// add ABS for absolute and sign for signum
// sign is signum which if num is positive returns 1. neg returns -1
func
    : SIN
    | COS
    | TAN
    | SQRT
    | ABS
    | SIGN
    ;
// 4/8/2026 we added abs and signum
 // added other operations on top here. it used to just be EQUALS 3/12/2026. adding expressions 3/14/26
// conditions below


condition
    : TARGET COMPARE NUMBER
    ; // ex: extruder > 100. the target is bed or extruder in this case. compare is >< etc.

// the tokens!!!

// Keywords first - O specific rules before general ones
TITLE      : 'M.title';
MEND       : 'M.end';
HOME       : 'Home';
MOVE       : 'Move';
DIRECTION  : 'left' | 'right' | 'center' | 'up' | 'down';
HEAT       : 'Heat';
TARGET     : 'bed' | 'extruder' | 'chamber';
LEVEL      : 'Level';
CALL       : 'M.call';
IF         : 'if';
ENDIF      : 'endif';
REPEAT     : 'repeat';
BREPEAT    : 'Brepeat';
END        : 'end';

SIN        : 'sin';
COS        : 'cos';
TAN        : 'tan';
SQRT       : 'sqrt';
PI         : 'pi';
// added new 4/8/2026
ABS        : 'abs';
SIGN       : 'sign';

PAUSE      : 'Pause' | 'PAUSE' | 'pause';
RESPOND    : 'Respond'| 'RESPOND' | 'respond';
RESUME     : 'resume' | 'RESUME' | 'Resume';
SET_HEATER : 'Set_Heater_Temperature';
WAITFORTEMP : 'WaitForTemp';
DWELL       : 'Dwell';
MOVEEX      : 'MoveTo';
MSG         : 'MSG';
ABSOLUTE    : 'Absolute';
ARROW       : '->';
RELATIVE    : 'Relative';
TIMEOUT_SET : 'TIMEOUT_SET';
RELATIVEEXTRUSION : 'RelativeExtrusion';
LOAD_BED_MESH       : 'LoadBedMesh'| 'LOAD_BED_MESH';
SET_PRESSURE_ADVANCE : 'SetPressureAdvance'| 'SET_PRESSURE_ADVANCE';
RESET_EXTRUDER       : 'ResetExtruder';
BED_MESH_CALIBRATE : 'BedMeshCalibrate'| 'BED_MESH_CALIBRATE';
PROBE_CALIBRATE : 'ProbeCalibrate'| 'PROBE_CALIBRATE';
COOLDOWN    : 'cooldown'| 'Cooldown' | 'COOLDOWN';
SET_SPEED   : 'SetSpeed';
SET_FAN     : 'SetFan';
PRINTFILE   : 'PRINTFILE';
// new tokens
SET_NOZZLE       : 'SetNozzle';
SET_FILAMENT     : 'SetFilament';
SET_LAYER_HEIGHT : 'SetLayerHeight';
SET_EXTRUSION_MULTIPLIER : 'SetExtrusionMultiplier';
LAYER            : 'Layer';
ENABLE_AUTO_EXTRUDE : 'EnableAutoExtrude';

// Operators —  BEFORE NUMBER and ID
PLUSEQ     : '+='; 
MINUSEQ    : '-=';  
MULTEQ     : '*=';   
DIVEQ      : '/=';   
EQUALS     : '=';
PLUS       : '+';
MINUS      : '-';
COMPARE    : '>' | '<' | '==' | '!=';

// Numbers and strings
NUMBER : '-'?[0-9]+ ('.' [0-9]+)?;
STRING : '"' (~["\r\n])* '"';

// Delimiters
SEMICOLON  : ';';
NEWLINE    : '\r'? '\n';
WS         : [ \t]+ -> skip;

// Axes (put after operators to avoid conflicts)
X          : 'x';
Y          : 'y';
Z          : 'z';
E          : 'e';

// Comments
COMMENT : '#' ~[\r\n]* -> skip;

// ID -  LAST so keywords match first
ID : [a-zA-Z_][a-zA-Z0-9_]* ;

// Catch-all for anything not recognized
UNRECOGNIZED : . ;