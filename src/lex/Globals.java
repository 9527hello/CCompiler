package lex;

import java.io.File;

class YyLval {
	public char ascii;
}

public class Globals {
	public static String yyText;
	public static int yyLeng;
	public static int yyLineno;
	public static YyLval yyLval = new YyLval();
	public static File yyCodeOut;
}
