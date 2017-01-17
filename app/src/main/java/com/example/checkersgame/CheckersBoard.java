package com.example.checkersgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Rect;
import android.util.Log;

import com.example.checkersgame.CheckersPiece.PieceType;

public class CheckersBoard
{
	private ArrayList<CheckersPiece> lightPieces;
	private ArrayList<CheckersPiece> darkPieces;
	private CheckersPiece nonePiece;
	private Rect dstRect;
	final static int NUM_ROWS = 8;
	final static int NUM_COLS = 8;
    final static int NUM_PIECES = 12;
    public enum MOVE {
        MOVE_VALID, WRONG_NUM_STEPS, MOVING_WRONG_PIECE, MOVE_OUT_OF_BOUNDS, WRONG_DIRECTION, NOT_KILLING_MOVE
    };
    private CheckersPiece jumpPiece; // if just jumped, this is the piece that made the jump
	private PieceType turn;
	private PieceType winner;


	public void initVars()
	{
		this.lightPieces = new ArrayList<CheckersPiece>();
		this.darkPieces = new ArrayList<CheckersPiece>();
		nonePiece = new CheckersPiece(PieceType.NON_PIECE);
		nonePiece.setPos(-1, -1);
        jumpPiece = nonePiece;
        dstRect = Commons.getBounds();
        turn = PieceType.LIGHT_PIECE;
		winner = PieceType.NON_PIECE;
	}

	public void setTurn(PieceType pt)
	{
		turn = pt;
	}

	public Rect getDstRect()
	{
		return dstRect;
	}

	public void setDstRect(int left, int top, int right, int bottom)
	{
		dstRect = new Rect(left, top, right, bottom);
	}

	public CheckersBoard()
	{
		this.initVars();
		this.boardInit();
	}

	public PieceType get_turn()
	{
		return turn;
	}

	public void set_winner(PieceType winner)
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
            if (piece == cb.jumpPiece) jumpPiece = cp;
		}
		for (CheckersPiece piece : cb.lightPieces)
		{
			cp = new CheckersPiece(piece);
			this.lightPieces.add(cp);
            if (piece == cb.jumpPiece) jumpPiece = cp;
        }
		this.turn = cb.turn;
	}

	CheckersPiece getPieceAt(int row, int col)
	{

		for (CheckersPiece x : this.darkPieces) {
			if (x.get_position().get_row() == row && x.get_position().get_col() == col) return x;
		}
		for (CheckersPiece x : this.lightPieces) {
			if (x.get_position().get_row() == row && x.get_position().get_col() == col) return x;
		}
		return this.nonePiece;
	}

	void boardInit()
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

		r = NUM_ROWS / 2 + 1;
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

	void move(CheckersMove cm)
	{
		CheckersPiece cp = getPieceAt(cm.get_start().get_row(), cm.get_start().get_col());
		if (isMoveValid(cm) == MOVE.MOVE_VALID)
		{
            jumpPiece = nonePiece;
			cp.setPos(cm.get_end().get_row(), cm.get_end().get_col()); // update piece position

            CheckersPosition cStart = cm.get_start();
			CheckersPosition cEnd = cm.get_end();
			int rowDiff = cEnd.get_row() - cStart.get_row();
			int colDiff = cEnd.get_col() - cStart.get_col();

			if (rowDiff == 2 || rowDiff == -2) // if killing a piece
			{
				CheckersPiece cap = getPieceAt(cStart.get_row() + rowDiff / 2, cStart.get_col() + colDiff / 2); // get piece being killed
				cap.set_captured();
                if (canKill(cp)) {
                    Log.d("move", "jumpPiece set -- " + cp.getPieceType().toString());
                    jumpPiece = cp; // get another turn to kill, if can kill
                }
			}

			if (jumpPiece == nonePiece)
            {
                Log.d("move", "no jumpPiece -- switching turns");
				if (turn == PieceType.DARK_PIECE) turn = PieceType.LIGHT_PIECE;
				else turn = PieceType.DARK_PIECE;
			}

			if (cp.getPieceType() == PieceType.DARK_PIECE && cEnd.get_row() == 0) cp.setCrowned(true);
			else if (cp.getPieceType() == PieceType.LIGHT_PIECE && cEnd.get_row() == NUM_ROWS - 1) cp.setCrowned(true);
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
				cp = getPieceAt(i, j);
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
				toReturn += (getPieceAt(row, col));
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

	public PieceType getWinner()
	{
		boolean atleastOneLightPiece = false;
		boolean atleastOneDarkPiece = false;
		if (getPieceCount(PieceType.LIGHT_PIECE) > 0) atleastOneLightPiece = true;
		if (getPieceCount(PieceType.DARK_PIECE) > 0) atleastOneDarkPiece = true;
		if (atleastOneDarkPiece && !atleastOneLightPiece || winner == PieceType.DARK_PIECE) return PieceType.DARK_PIECE;
		else if (atleastOneLightPiece && !atleastOneDarkPiece || winner == PieceType.LIGHT_PIECE) return PieceType.LIGHT_PIECE;
		else return PieceType.NON_PIECE;
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
        CheckersPosition cpos;
        CheckersMove move = new CheckersMove();
		move.setStart(cp.get_position());
        if ((cp.getPieceType() == PieceType.LIGHT_PIECE || cp.is_crowned()) && cp.get_position().get_row() + 2 < NUM_ROWS) {
            if(cp.get_position().get_col() + 2 < NUM_COLS) {
                cpos = new CheckersPosition(cp.get_position().get_row() + 2, cp.get_position().get_col() + 2);
                move.setEnd(cpos);
                if (isKillingMove(move)) return true;
            }
            if (cp.get_position().get_col() - 2 >= 0) {
                cpos = new CheckersPosition(cp.get_position().get_row() + 2, cp.get_position().get_col() - 2);
                move.setEnd(cpos);
                if (isKillingMove(move)) return true;
            }
        }
        if ((cp.getPieceType() == PieceType.DARK_PIECE || cp.is_crowned()) && cp.get_position().get_row() - 2 >= 0) {
            if (cp.get_position().get_col() + 2 < NUM_COLS) {
                cpos = new CheckersPosition(cp.get_position().get_row() - 2, cp.get_position().get_col() + 2);
                move.setEnd(cpos);
                if (isKillingMove(move)) return true;
            }
            if (cp.get_position().get_col() - 2 >= 0) {
                cpos = new CheckersPosition(cp.get_position().get_row() - 2, cp.get_position().get_col() - 2);
                move.setEnd(cpos);
                if (isKillingMove(move)) return true;
            }
        }

		return false;
	}

    public boolean isKillingMove(CheckersMove move) {
        Log.d("isKillingMove", "Testing move [[" + move.get_start().get_row() + "," + move.get_start().get_col() + "], " +
                "[" + move.get_end().get_row() + ", " + move.get_end().get_col() +"] ]");
        CheckersPiece endPiece = getPieceAt(move.get_end().get_row(), move.get_end().get_col());
        int dCol = move.get_end().get_col() - move.get_start().get_col();
        int dRow = move.get_end().get_row() - move.get_start().get_row();
        if (Math.abs(dCol) != 2 || Math.abs(dRow) != 2) return false;
        int midR = move.get_start().get_row() + dRow / 2;
        int midC = move.get_start().get_col() + dCol / 2;
        CheckersPiece midPiece = getPieceAt(midR, midC); // get the piece that's one step away
        Log.d("isKillingMove", "isKillingMove == " +
                Boolean.toString(midPiece.getPieceType() != turn && midPiece.getPieceType() != PieceType.NON_PIECE && endPiece.getPieceType() == PieceType.NON_PIECE));
        return (midPiece.getPieceType() != turn && midPiece.getPieceType() != PieceType.NON_PIECE && endPiece.getPieceType() == PieceType.NON_PIECE);
    }

	public MOVE isMoveValid(CheckersMove move) // buggy
	{
        CheckersPiece startPiece = getPieceAt(move.get_start().get_row(), move.get_start().get_col());
        CheckersPiece endPiece = getPieceAt(move.get_end().get_row(), move.get_end().get_col());
        CheckersPosition[] poss = {move.get_start(), move.get_end()};
        Log.d("isMoveValid", turn.toString());
        //check if positions within bounds
        for (CheckersPosition pos : poss) {
            if (pos.get_row() < 0 || pos.get_row() >= NUM_ROWS ||
                pos.get_col() < 0 || pos.get_col() >= NUM_COLS) return  MOVE.MOVE_OUT_OF_BOUNDS;
        }

        if (startPiece.getPieceType() != turn || endPiece.getPieceType() != PieceType.NON_PIECE) return MOVE.MOVING_WRONG_PIECE; // moving wrong piece or moving on top of a piece
        else if (jumpPiece.getPieceType() != PieceType.NON_PIECE && startPiece != jumpPiece) return  MOVE.MOVING_WRONG_PIECE; // moving wrong piece
        else {
            int dCol = move.get_end().get_col() - move.get_start().get_col();
            int dRow = move.get_end().get_row() - move.get_start().get_row();
            if (!startPiece.is_crowned() &&
               (
                  (startPiece.getPieceType() == PieceType.DARK_PIECE && dRow > 0) ||
                  (startPiece.getPieceType() == PieceType.LIGHT_PIECE && dRow < 0))
               ) return  MOVE.WRONG_DIRECTION; // moving in wrong direction

            boolean playerCankill = false;
            List<CheckersPiece> pieces;
            if (jumpPiece.getPieceType() != PieceType.NON_PIECE) pieces = Arrays.asList(jumpPiece);
            else pieces = getPieces(startPiece.getPieceType());
            for (CheckersPiece piece : pieces) {
                playerCankill = playerCankill || canKill(piece);
            }
            Log.d("isMoveValid", "canKill == " + Boolean.toString(playerCankill));
            // we know pieces are moving in the right direction at this point. just need to check if the number of steps is right
            if (playerCankill) {
                if (!isKillingMove(move)) return MOVE.NOT_KILLING_MOVE; // isKillingMove also checks if the number of steps taken was 2
            } else {
                if (Math.abs(dRow) != 1 || Math.abs(dCol) != 1) return MOVE.WRONG_NUM_STEPS;
            }
        }

        return MOVE.MOVE_VALID;
	}
}
