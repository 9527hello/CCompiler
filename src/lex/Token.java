package lex;

public class Token {
	/* Token type */
	public final static int ID       = 0; //YyLex½áÊø×´Ì¬
	public final static int STRING   = 1;
	public final static int ICON     = 2;
	public final static int FCON     = 3;
	public final static int LP       = 4;
	public final static int RP       = 5;
	public final static int LC       = 6;
	public final static int RC       = 7;
	public final static int LB       = 8;
	public final static int RB       = 9;
	public final static int STRUCTOP = 10;
	public final static int INCOP    = 11;
	public final static int UNOP     = 12;
	public final static int STAR     = 13;
	public final static int DIVOP    = 14;
	public final static int PLUS     = 15;
	public final static int MINUS    = 16;
	public final static int SHIFTOP  = 17;
	public final static int RELOP    = 18;
	public final static int EQUOP    = 19;
	public final static int ASSIGNOP = 20;
	public final static int EQUAL    = 21;
	public final static int AND      = 22;
	public final static int XOR      = 23;
	public final static int OR       = 24;
	public final static int ANDAND   = 25;
	public final static int OROR     = 26;
	public final static int QUEST    = 27;
	public final static int COLON    = 28;
	public final static int COMMA    = 29;
	public final static int SEMI     = 30;
	public final static int ELLIPSIS = 31;

    /* Key type */
	public final static int CLASS    = 32;
	public final static int BREAK    = 33;
	public final static int CASE     = 34;
	public final static int TYPE     = 35;
	public final static int CONTINUE = 36;
	public final static int DEFAULT  = 37;
	public final static int DO       = 38;
	public final static int ELSE     = 39;
	public final static int ENUM     = 40;
	public final static int FOR      = 41;
	public final static int GOTO     = 42;
	public final static int IF       = 43;
	public final static int RETURN   = 44;
	public final static int SIZEOF   = 45;
	public final static int STRUCT   = 46;
	public final static int SWITCH   = 47;
	public final static int WHILE    = 48;
	public final static int OTHER    = 49;
}
