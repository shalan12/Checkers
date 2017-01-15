package com.example.checkersgame;

import java.util.ArrayList;

import android.graphics.Rect;

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
	final static int TURN_DARK = 2;
	final static int TURN_LIGHT = 1;
	final static int NUM_ROWS = 8;
	final static int NUM_COLS = 8;
	private boolean turnIsKill;
	private int turn;
	private int winner;

	public void initVars()
	{
		this.lightPieces = new ArrayList<CheckersPiece>();
		this.darkPieces = new ArrayList<CheckersPiece>();
		nonePiece = new CheckersPiece();
		nonePiece.setPos(-1, -1);
		dstRect = Commons.getBounds();
		turn = TURN_LIGHT;
		turnIsKill = false;
		winner = DRAW;
	}

	public void setTurn(int t)
	{
		turn = t;
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

	public int get_turn()
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

	public CheckersBoard(String s1, String s2)
	{
		this.initVars();
		String[] darkPieces = s1.split("\\,");
		String[] lightPieces = s2.split("\\,");
		CheckersPiece temp;
		nonePiece.setPos(-1, -1);
		for (int i = 0; i < darkPieces.length; i++)
		{
			temp = new CheckersPiece();
			temp.get_position().position_parse(darkPieces[i]);
			temp.setDark(true);
			this.darkPieces.add(temp);
		}
		for (int i = 0; i < lightPieces.length; i++)
		{
			temp = new CheckersPiece();
			temp.get_position().position_parse(lightPieces[i]);
			this.lightPieces.add(temp);
		}
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
		for (int i = 0; i < 12; i++)
		{
			temp = new CheckersPiece();
			temp.setPos(r, c);
			this.lightPieces.add(temp);
			c += 2;
			if (c > 7)
			{
				c = (c + 1) % 2;
				r++;
			}
		}
		r = 5;
		c = 0;
		for (int i = 0; i < 12; i++)
		{
			temp = new CheckersPiece();
			temp.setPos(r, c);
			temp.setDark(true);
			this.darkPieces.add(temp);
			c += 2;
			if (c > 7)
			{
				c = (c + 1) % 2;
				r++;
			}
		}
	}

	void simple_move(CheckersSimpleMove cSM)
	{
		CheckersPiece cp = board_get_piece_at(cSM.get_start().get_row(), cSM.get_start().get_col());
		if (isMoveValid(cSM) == 0)
		{
			cp.setPos(cSM.get_end().get_row(), cSM.get_end().get_col());
			CheckersPosition cStart = cSM.get_start();
			CheckersPosition cEnd = cSM.get_end();
			// //system.out.println("MoveValid");
			int rowDiff = cStart.get_row() - cEnd.get_row();
			int colDiff = cStart.get_col() - cEnd.get_col();
			// //system.out.println("isKill = " + turnIsKill + "\n can kill = "
			// +
			// canKill(cp));
			// //system.out.println("\nturn = " + turn);
			if (rowDiff == 2 || rowDiff == -2)
			{
				// //system.out.println("Killing");
				CheckersPiece cap = board_get_piece_at(cStart.get_row() - rowDiff / 2,
						cStart.get_col() - colDiff / 2);
				if (!cap.is_none_piece())
				{
					cap.set_captured();
					turnIsKill = true;
				}
			}
			// //system.out.println("\nturn = " + turn);

			if ((turnIsKill && !canKill(cp)) || !turnIsKill)
			{
				// //system.out.println("Here");
				turnIsKill = false;
				if (turn == TURN_DARK) turn = TURN_LIGHT;
				else turn = TURN_DARK;
			}
			// //system.out.println("\nturn = " + turn);

			if (cp.is_dark() && cEnd.get_row() == 0) cp.setCrowned(true);
			else if (!cp.is_dark() && cEnd.get_row() == 7) cp.setCrowned(true);
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
				if (!cp.is_none_piece())
				{
					if (pT == PieceType.DARK_PIECE && cp.is_dark()) toReturn.add(cp);
					else if (pT == PieceType.LIGHT_PIECE && !cp.is_dark()) toReturn.add(cp);
				}

			}
		}
		return toReturn;
	}

	// CheckersPiece operator[] (CheckersPosition cP) {
	// return board_get_piece_at(cP.get_row(), cP.get_col());
	// }
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
		// //system.out.println(pT.toString() + " count = " + count);
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
		// //system.out.println("numCrowned " + pT.toString() + " count = " +
		// count);
		return count;
	}

	public int validate(CheckersMove moves)
	{
		int res = 0;
		for (CheckersSimpleMove m : moves.getMoves())
		{
			res = isMoveValid(m);
			if (res > 0)
			{
				break;
			}
		}
		return res;
	}

	public boolean canKill(CheckersPiece cp)
	{
		CheckersSimpleMove simpleMove = new CheckersSimpleMove();
		simpleMove.setStart(cp.get_position());
		CheckersPosition cpos = new CheckersPosition(cp.get_position().get_row() + 2, cp
				.get_position().get_col() + 2);
		simpleMove.setEnd(cpos);
		if (isMoveValid(simpleMove) == 0) return true;
		cpos = new CheckersPosition(cp.get_position().get_row() + 2,
				cp.get_position().get_col() - 2);
		simpleMove.setEnd(cpos);
		if (isMoveValid(simpleMove) == 0) return true;
		cpos = new CheckersPosition(cp.get_position().get_row() - 2,
				cp.get_position().get_col() + 2);
		simpleMove.setEnd(cpos);
		if (isMoveValid(simpleMove) == 0) return true;
		cpos = new CheckersPosition(cp.get_position().get_row() - 2,
				cp.get_position().get_col() - 2);
		simpleMove.setEnd(cpos);
		if (isMoveValid(simpleMove) == 0) return true;
		return false;

	}

	public int isMoveValid(CheckersSimpleMove simpleMove)
	{
		// 0 valid
		// 1 game finished
		// 2 wrong turn
		// 3 wrong starting
		// 4 wrong ending
		// 5 wrong step
		// 6 not crowned | wrong step
		// 7 not killed
		int res = 0;
		if (turnIsKill) res = 7;
		if (winner() > 0)
		{
			res = 1;
		}
		else
		{
			CheckersPiece startPiece = board_get_piece_at(simpleMove.get_start().get_row(),
					simpleMove.get_start().get_col());
			CheckersPiece endPiece = board_get_piece_at(simpleMove.get_end().get_row(), simpleMove
					.get_end().get_col());

			if (startPiece.is_none_piece())
			{
				res = 3;
			}
			else if (!endPiece.is_none_piece())
			{
				res = 4;
			}
			else if (simpleMove.get_end().get_row() > 7 || simpleMove.get_end().get_row() < 0
					|| simpleMove.get_end().get_col() > 7 || simpleMove.get_end().get_col() < 0) res = 4;
			else if ((startPiece.is_dark() && turn == TURN_LIGHT)
					|| (!startPiece.is_dark() && turn == TURN_DARK))
			{
				res = 2;
			}
			else
			{
				int dCol = simpleMove.get_end().get_col() - simpleMove.get_start().get_col();
				int dRow = simpleMove.get_end().get_row() - simpleMove.get_start().get_row();

				if (dRow == 0 || dCol == 0 || Math.abs(dRow) > 2 || Math.abs(dCol) > 2
						|| (Math.abs(dRow) == 2 && Math.abs(dCol) != 2)
						|| (Math.abs(dCol) == 2 && Math.abs(dRow) != 2))
				{
					res = 6;
				}
				else if (!startPiece.is_crowned())
				{
					if ((startPiece.is_dark() && dRow > 0) || (!startPiece.is_dark() && dRow < 0))
					{
						res = 6;
					}
				}
				else if (Math.abs(dRow) == 2 || Math.abs(dCol) == 2)
				{
					int midR = simpleMove.get_start().get_row() + dRow / 2;
					int midC = simpleMove.get_start().get_col() + dCol / 2;
					CheckersPiece midPiece = board_get_piece_at(midR, midC);

					if (midPiece.is_none_piece() || midPiece.is_dark() == startPiece.is_dark())
					{
						res = 5;
					}
					else res = 0;
				}

			}
		}
		System.out.println(simpleMove + "\nturn = " + turn + " turniskill = " + turnIsKill
				+ " valid = " + res);
		return res;
	}
}
