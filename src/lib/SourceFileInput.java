package lib;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

//用于读入待词法解析的程序文件
public class SourceFileInput extends Utils{
	private final int MAXLEX  = 1024;
	private final int MAXLOOK = 16;
	private final int END     = 3 * MAXLEX + 2 * MAXLOOK;
	
	//缓冲区相关指针
	private int pMark;
	private int sMark;
	private int eMark;
	private int next;
	private int endBuf;
	
	//当前lexeme行号、长度
	private int pLineno;
	private int pLength;
	private int lineno;
	private int mLineno;
	
	//读文件是否遇到结束标志
	private boolean endOfRead;
	
	//输入流
	private BufferedInputStream inputStream;
	
	private int beenCalled = 0;
	
	private int termChar = 0;
	
	//一般有前一个，当前，和正在处理的lexeme，在shift之后当前和新分配的MAXLEX都需要MAXLOOK空间
	public byte[] inputBuf = new byte[3 * MAXLEX + 2 * MAXLOOK];
	
	private int Min(int a, int b) {
		return a < b ? a : b;
	}
	
	private int Danger() {
		return endBuf - MAXLOOK;
	}
	
	private boolean NoMoreChars() {	
		return endOfRead && next >= endBuf;
	}
	
	public int MarkStart() {
		mLineno = lineno;
		return eMark = sMark = next;
	};
	
	public int MarkEnd() {
		mLineno = lineno;
		return eMark = next;
	}
	
	public int MoveStart() {
		if (sMark >= eMark)
			return -1;
		else
			return ++sMark;
	}
	
	public int ToMark() {
		lineno = mLineno;
		return next = eMark;
	}
	
	//一般在MarkStart之前调用
	private int MarkPrev() {
		pLineno = lineno;
		pLength = eMark - sMark;
		return pMark = sMark;
	}
	
	private boolean FillBuf(int startingAt) {		
		int need   = (END - startingAt) / MAXLEX * MAXLEX;
		
		if (0 == need)
			return true;
		
		if (need < 0) {
			System.err.println("Bad request startingAt " + Utils.GetErrPositon());
			return false;
		}
		
		byte[] buf = new byte[need];
		int got;
		try {
			got = inputStream.read(buf);
			System.arraycopy(buf, 0, inputBuf, startingAt, got);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		endBuf = startingAt + got;
		
		if (got < need)
			endOfRead = true;
		return true;
	}
	
	public boolean Flush(boolean force) {
		if (NoMoreChars() || endOfRead)
			return true;
		
		if (next >= Danger() || force) {
			int leftEdge = -1 != pMark ? Min(pMark, sMark) : sMark;
			int shiftAmt = leftEdge;
			if (shiftAmt < MAXLEX) {
				if (!force) {
					System.err.println("Not enough space for new lexeme " + Utils.GetErrPositon());
					return false;
				}				
				//丢弃当前未被处理的lexeme并重置pre
				leftEdge = MarkStart();
				MarkPrev();
				shiftAmt  = leftEdge;
			}
			int copyAmt = endBuf - leftEdge;
			System.arraycopy(inputBuf, leftEdge, inputBuf, 0, copyAmt);
			if (!FillBuf(copyAmt)) {
				System.err.println("Buffer full can't read " + Utils.GetErrPositon());
				return false;
			}
			if (-1 != pMark)
				pMark -= shiftAmt;
			sMark  -= shiftAmt;
			eMark  -= shiftAmt;
			next   -= shiftAmt;
		}
		return true;
	}
	
	public int Look(int n) {
		if (n > endBuf - next) 
			return endOfRead ? '\0' : -1;
		if (--n < -(next))
			return 0;
		return inputBuf[next + n];
	}
	
	public boolean PushBack(int n) {
		while (--n >= 0 && next > sMark) {
			--next;
			if ('\n' == inputBuf[next] || '\0' == inputBuf[next])
				--lineno;
		}
		if (next < eMark) {
			eMark   = next;
			mLineno = lineno;
		}
		return next >= sMark; //Different from book
	}
	
	public void Term() {
		termChar       = inputBuf[next];
		inputBuf[next] = '\0';
	}
	
	public void UnTerm() {
		if (0 != termChar) {
			inputBuf[next] = (byte)termChar;
			termChar       = 0;
		}
	}
	
	public int Input() {
		int retVal = -1;
		
		if (0 != termChar) {
			UnTerm();
			retVal = Advance();
			MarkEnd();
			Term();
		}
		else {
			retVal = Advance();
			MarkEnd();
		}
		return retVal;
	}
	
	public void Unput(int c) {
		if (0 != termChar) {
			UnTerm();
			if (PushBack(1)) 
				inputBuf[next] = (byte)c;
			Term();
		}
		else {
			if (PushBack(1))
				inputBuf[next] = (byte)c;
		}
	}
	
	public int LookAhead(int n) {
		return (1 == n && 0 != termChar) ? termChar : Look(n);
	}
	
	public boolean FlushBuf() {
		if (0 != termChar)
			UnTerm();
		return Flush(true);
	}
	
	public boolean OpenNewFile(String fileName) {
		inputStream    = null;
		File inputFile = new File(fileName);
		if (!inputFile.exists()) {
			System.err.println("Can't not open file:" + fileName + " " + Utils.GetErrPositon());
			return false;
		}
		try {
			inputStream = new BufferedInputStream(new FileInputStream(fileName));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		pMark     = pLineno = -1;
		sMark     = eMark   = endBuf = next = END;
		lineno    = mLineno = 1;
		endOfRead = false;				
		return true;
	}
	
	public int Advance() {
		if (0 == beenCalled) {
			next           = sMark = eMark = END - 1;
			pMark          = -1;
			inputBuf[next] = '\n';
			--lineno;
			--mLineno;
			beenCalled     = 1;
		}
		
		if (NoMoreChars())
			return 0;
		
		if (!endOfRead && !Flush(false))
			return -1;
		
		int cur = (int)inputBuf[next];
		if ('\n' == cur)
			++lineno;
		++next;	
		return cur;
	}
	
	public String CurText() {
		String str = "";
		int index  = sMark;
		int cur;
	    while ('\0' != (cur = inputBuf[index])) {
	    	str += (char)cur;
	    	++index;
	    }
		return str;
	}
	
	public int CurLength() {
		return eMark - sMark;
	}
	
	public int CurLineno() {
		return lineno;
	}
	
	public String PrevText() {
		String str = "";
		for (int i = pMark; i < sMark; ++i)
			str += inputBuf[i];
		return str;
	}
	
	public int PrevLength() {
		return pLength;
	}
	
	public int PrevLineno() {
		return pLineno;
	}
	
	public void Close() {
		try {
			inputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SourceFileInput(String sourceFileName) {
		OpenNewFile(sourceFileName);
	}
}
