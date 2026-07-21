grammar Jupitore;

// rules
// notes. so we have macros+ means more than one macros may be in. and EOF means end of file. so it reads the full thingprogram
    program
    : (NEWLINE | WS)* macro ( (NEWLINE | WS)* macro )* (NEWLINE | WS)* EOF
    ;

// this should be how it looks
   macro
    : TITLE STRING (NEWLINE | WS)+
      (statement | NEWLINE)*
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
    :HOME (ARROW coordList)? stmtTerm // ? means optional. Home or Home->x=0
    | MOVE DIRECTION (ARROW coordList)? stmtTerm  // can be terminated by ; or a newline
    | HEAT TARGET EQUALS expr stmtTerm  // heat extruder: 200 like m140 s60 ; bed
    | LEVEL stmtTerm // level
    | CALL STRING stmtTerm // m.call "Macro Name". so you can call a macros in a macros
                   // | REPEAT NUMBER NEWLINE (statement)* END (NEWLINE | EOF)  // <--- new trying out repeat blocks 3/12/2026
                  //| IF condition NEWLINE statement* ENDIF NEWLINE // for conditional statements - FIXED: NEWLINE instead of SEMICOLON
    | SET_NOZZLE EQUALS expr stmtTerm
    | SET_FILAMENT EQUALS expr stmtTerm
    | SET_LAYER_HEIGHT EQUALS expr stmtTerm
    | SET_EXTRUSION_MULTIPLIER EQUALS expr stmtTerm
    | layer_statement   // new
    | ENABLE_AUTO_EXTRUDE EQUALS expr stmtTerm
    | repeat_statement    // testing
    | if_statement        // testing
    | brepeat_statement  // testing
    | PAUSE stmtTerm
    | RESPOND MSG STRING stmtTerm // to print a message
    | RESUME stmtTerm
    | SET_HEATER TARGET EQUALS expr stmtTerm // set to target temp. m600 more advanced
    | WAITFORTEMP TARGET stmtTerm // wait for target temp m109
    | COOLDOWN (TARGET)? stmtTerm // added here
    | MOVEEX coordList stmtTerm // moves or extrude with the given coords G1
    | ABSOLUTE stmtTerm   // outputs G90 which is an absolute positioning
    | RELATIVE stmtTerm   // outputs G91 which is a relative positioning
    | TIMEOUT_SET expr stmtTerm                        // Set idle timeout
    | RELATIVEEXTRUSION stmtTerm // this is command M83
    | LOAD_BED_MESH STRING stmtTerm            // BED_MESH_PROFILE LOAD=default
    | SET_PRESSURE_ADVANCE expr stmtTerm     //exm:  SET_PRESSURE_ADVANCE ADVANCE=0.04
    | RESET_EXTRUDER stmtTerm                  // G92 E0
    | DWELL expr ( 'S' | 's' | 'MS' | 'ms' )? stmtTerm// new
    | BED_MESH_CALIBRATE stmtTerm    // BED_MESH_CALIBRATE command
    | PROBE_CALIBRATE stmtTerm    // PROBE_CALIBRATE
       // Added - FIXED: lowercase 'cooldown' deleted cooldown.
    | SET_SPEED EQUALS expr stmtTerm // 6/17/26  changed to expr to test for user defined variables.
    | SET_FAN EQUALS expr stmtTerm     // Added        // Added - FIXED: removed SPEED token
    | PRINTFILE STRING stmtTerm             // Added - prints a G-code file via SD card
    | assignment   // new rule for variable assignment 6/17/2026
    | global_assignment  // var x = expr - persists across macros, unlike a plain assignment
    | invalid_assignment  // x/y/z/e = expr - reserved axis letters used as a plain assignment target
    | insert_gcode_statement  // NEW: separate rule for InsertGCode
    ;

// a statement ends either with a newline (as before) or a semicolon -
// the semicolon lets another statement start right after on the same line
stmtTerm
    : SEMICOLON
    | NEWLINE
    ;

// new rule for InsertGCode - supports both orders
insert_gcode_statement
    : INSERT_GCODE STRING (AS_REF)? stmtTerm          // InsertGCode "file.gcode" reference
    | INSERT_GCODE AS_REF STRING stmtTerm            // InsertGCode reference "file.gcode"
    ;

// new rule 6/17/26
 assignment
    : ID EQUALS expr stmtTerm
    ;

// var x = expr - a global_assignment declares/updates a variable in the
// file-global scope, visible from every macro in the compilation unit,
// as opposed to a plain assignment which is local to the current macro
global_assignment
    : VAR ID EQUALS expr NEWLINE
    ;

// x/y/z/e tokenize as dedicated axis tokens rather than a generic ID, so a
// plain "x = 5" can never match the `assignment` rule above - without this
// rule, the parser's error recovery silently drops the whole statement
// before the visitor ever runs. This exists purely to catch that case and
// raise a clear error instead of a silent no-op. See issue #51.
invalid_assignment
    : (X | Y | Z | E) EQUALS expr stmtTerm
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
    : (statement | NEWLINE)+
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
// RECURSIONNN. i am bad at recursion but this works 
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
    | '-' expr                        # unaryMinus   // 7/21/2026 added unary minus to allow for negative numbers in expressions.   
    | <assoc=right> expr '^' expr     # power    
    | expr op=('*'|'/') expr          # mulDiv    //  Multiplication/Division
    | expr op=('+'|'-') expr          # addSub    // Addition/Subtraction
    | NUMBER                          # number    
    | 'i'                             # iterator  //The loop variable
    | PI                              # pi        // The mathematical constant π
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
VAR        : 'var'; // declares a variable in file-global scope, persisting across macros
INSERT_GCODE : 'InsertGCode' | 'InsertGcode' | 'insertGcode'; 

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
AS_REF      : 'reference';
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
// NUMBER : '-'?[0-9]+ ('.' [0-9]+)?; deleting '-' from the front of the number token allows for unary minus.
NUMBER : [0-9]+ ('.' [0-9]+)?;
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