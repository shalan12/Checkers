package com.example.checkersgame;

import java.util.ArrayList;

import android.graphics.Rect;
import android.util.Log;

import com.example.checkersgame.CheckersPiece.PieceType;

public class CheckersBoard
{
	private ArrayList<CheckersPiece> lightPieces;
	private ArrayList<CheckersPiece> darkPieces;
	private CheckersPiece nonePiece;
	private Rect dstRect;
	final static int WINNER_LIGHT = 1;
	final static int DRAW = 0;
	final static int WINNER_DARK = 2;
	final static int NUM_ROWS = 8;
	final static int NUM_COLS = 8;
    final static int NUM_PIECES = 12;
    public final static int MOVE_VALID = 1;
    public final static int MOVE_INVALID = -1;
	private boolean turnIsKill;
	private PieceType turn;
	private int winner;


	public void initVars()
	{
		this.lightPieces = new ArrayList<CheckersPiece>();
		this.darkPieces = new ArrayList<CheckersPiece>();
		nonePiece = new CheckersPiece(PieceType.NON_PIECE);
		nonePiece.setPos(-1, -1);
		dstRect = Commons.getBounds();
		turn = PieceType.LIGHT_PIECE;
		turnIsKill = false;
		winner = DRAW;
	}

	public void setTurn(PieceType pt)
	{
		turn = pt;
	}

	public Rect getDstRect()
	{
		return dstRect;
	}

	public void setDstRect(Rect r)
	{
		dstRect = r;
	}

	public void setDstRect(int left, int top, int right, int bottom)
	{
		dstRect = new Rect(left, top, right, bottom);
	}

	public CheckersBoard()
	{
		this.initVars();
		this.board_init();
	}

	public PieceType get_turn()
	{
		return turn;
	}

	public void set_winner(int winner)
	{
		this.winner = winner;
	}

	public CheckersBoard(CheckersBoard cb)
	{
		initVars();
		CheckersPiece cp;
		for (CheckersPiece piece : cb.darkPieces)
		{
			cp = new CheckersPiece(piece);
			this.darkPieces.add(cp);
		}
		for (CheckersPiece piece : cb.lightPieces)
		{
			cp = new CheckersPiece(piece);
			this.lightPieces.add(cp);
		}
		this.turn = cb.turn;
		this.turnIsKill = cb.turnIsKill;
	}

	CheckersPiece board_get_piece_at(int row, int col)
	{

		for (CheckersPiece x : this.darkPieces)
		{
			if (x.get_position().get_row() == row && x.get_position().get_col() == col) return x;
		}
		for (CheckersPiece x : this.lightPieces)
		{
			if (x.get_position().get_row() == row && x.get_position().get_col() == col) return x;
		}
		return this.nonePiece;
	}

	void board_init()
	{
		int r = 0;
		int c = 1;
		CheckersPiece temp;
        // add light pieces
		for (int i = 0; i < NUM_PIECES; i++)
		{
			temp = new CheckersPiece(PieceType.LIGHT_PIECE);
			temp.setPos(r, c);
			this.lightPieces.add(temp);
			c += 2;
			if (c >= NUM_COLS)
			{
				c = (c + 1) % 2;
				r++;
			}
		}

		r = NUM_ROWS/2 + 1;
		c = 0;
        // add dark pieces
        for (int i = 0; i < NUM_PIECES; i++)
		{
			temp = new CheckersPiece(PieceType.DARK_PIECE);
			temp.setPos(r, c);
			this.darkPieces.add(temp);
			c += 2;
			if (c >= NUM_COLS)
			{
				c = (c + 1) % 2;
				r++;
			}
		}
	}

	void simple_move(CheckersSimpleMove cSM)
	{
		CheckersPiece cp = board_get_piece_at(cSM.get_start().get_row(), cSM.get_start().get_col());
		if (isMoveValid(cSM) == MOVE_VALID)
		{
			cp.setPos(cSM.get_end().get_row(), cSM.get_end().get_col());
			CheckersPosition cStart = cSM.get_start();
			CheckersPosition cEnd = cSM.get_end();

			int rowDiff = cStart.get_row() - cEnd.get_row();
			int colDiff = cStart.get_col() - cEnd.get_col();

			if (rowDiff == 2 || rowDiff == -2) // if killing a piece
			{
				CheckersPiece cap = board_get_piece_at(cStart.get_row() - rowDiff / 2, cStart.get_col() - colDiff / 2); // get piece being killed
				if (!cap.is_none_piece()) // if is a valid piece
				{
					cap.set_captured();
					turnIsKill = true; // get another turn to kill, if can kill
				}
			}

			if ((turnIsKill && !canKill(cp)) || !turnIsKill) // otherwise turn changes
			{
				turnIsKill = false;
				if (turn == PieceType.DARK_PIECE) turn = PieceType.LIGHT_PIECE;
				else turn = PieceType.DARK_PIECE;
			}

			if (cp.getPieceType() == PieceType.DARK_PIECE && cEnd.get_row() == 0) cp.setCrowned(true);
			else if (cp.getPieceType() == PieceType.LIGHT_PIECE && cEnd.get_row() == NUM_ROWS - 1) cp.setCrowned(true);
		}
	}

	void move(CheckersMove cM)
	{
		for (CheckersSimpleMove x : cM.getMoves())
		{
			simple_move(x);
		}
	}

	public ArrayList<CheckersPiece> getPieces(PieceType pT)
	{
		CheckersPiece cp;
		ArrayList<CheckersPiece> toReturn = new ArrayList<CheckersPiece>();
		for (int i = 0; i < NUM_ROWS; i++)
		{
			for (int j = 0; j < NUM_COLS; j++)
			{
				cp = board_get_piece_at(i, j);
				if (!cp.is_none_piece() && pT == cp.getPieceType())
				{
				    toReturn.add(cp);
				}
			}
		}
		return toReturn;
	}

	public String toString()
	{
		String toReturn = "";
		for (int row = 0; row < NUM_ROWS; row++)
		{
			toReturn += Integer.toString(NUM_ROWS - row) + " ";
			for (int col = 0; col < NUM_COLS; col++)
			{
				toReturn += (board_get_piece_at(row, col));
			}
			toReturn += '\n';
		}
		toReturn += "  ABCDEFGH";
		return toReturn;
	}

	public int getPieceCount(PieceType pT) // to be used in ai
	{
		int count = 0;
		ArrayList<CheckersPiece> pieces;
		if (pT == PieceType.DARK_PIECE) pieces = this.darkPieces;
		else pieces = this.lightPieces;
		for (CheckersPiece cp : pieces)
		{
			if (!cp.is_none_piece() && !cp.is_captured()) count++;
		}
		return count;
	}

	public int winner()
	{
		boolean atleastOneLightPiece = false;
		boolean atleastOneDarkPiece = false;
		if (getPieceCount(PieceType.LIGHT_PIECE) > 0) atleastOneLightPiece = true;
		if (getPieceCount(PieceType.DARK_PIECE) > 0) atleastOneDarkPiece = true;
		if (atleastOneDarkPiece && !atleastOneLightPiece || winner == WINNER_DARK) return WINNER_DARK;
		else if (atleastOneLightPiece && !atleastOneDarkPiece || winner == WINNER_LIGHT) return WINNER_LIGHT;
		else return DRAW;
	}

	public int getNumCrowned(PieceType pT)
	{
		int count = 0;
		ArrayList<CheckersPiece> pieces;
		if (pT == PieceType.DARK_PIECE) pieces = this.darkPieces;
		else pieces = this.lightPieces;

        for (CheckersPiece cp : pieces)
		{
			if (!cp.is_none_piece() && !cp.is_captured()) count++;
		}

		return count;
	}

	public boolean canKill(CheckersPiece cp)
	{
		CheckersSimpleMove simpleMove = new CheckersSimpleMove();
		simpleMove.setStart(cp.get_position());
		CheckersPosition cpos = new CheckersPosition(cp.get_position().get_row() + 2, cp.get_position().get_col() + 2);
		simpleMove.setEnd(cpos);
		if (isMoveValid(simpleMove) == MOVE_VALID) return true;
		cpos = new CheckersPosition(cp.get_position().get_row() + 2, cp.get_position().get_col() - 2);
		simpleMove.setEnd(cpos);
		if (isMoveValid(simpleMove) == MOVE_VALID) return true;
		cpos = new CheckersPosition(cp.get_position().get_row() - 2, cp.get_position().get_col() + 2);
		simpleMove.setEnd(cpos);
		if (isMoveValid(simpleMove) == MOVE_VALID) return true;
		cpos = new CheckersPosition(cp.get_position().get_row() - 2, cp.get_position().get_col() - 2);
		simpleMove.setEnd(cpos);
		if (isMoveValid(simpleMove) == MOVE_VALID) return true;
		return false;

	}

	public int isMoveValid(CheckersSimpleMove simpleMove) //buggy
	{
        //ArrayList<CheckersPiece> pieces;
        CheckersPiece startPiece = board_get_piece_at(simpleMove.get_start().get_row(), simpleMove.get_start().get_col());
        CheckersPiece endPiece = board_get_piece_at(simpleMove.get_end().get_row(), simpleMove.get_end().get_col());
        //if (startPiece. != turn || endPiece)
        return MOVE_VALID;

	}
}
