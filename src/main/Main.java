package main;

import lex.Globals;
import lex.Lexyy;

public class Main {
	public static void main(String[] args) {		
		Lexyy lex = new Lexyy("test.c");
		int curTok;
		while (-1 != (curTok = lex.YyLex())) {			
			System.out.print(curTok);
			System.out.println(Globals.yyText);
		}
	}
}
