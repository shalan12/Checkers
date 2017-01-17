package com.example.checkersgame;

import android.graphics.Rect;

import java.util.ArrayDeque;
import java.util.Queue;

public class CheckersPiece
{
    public enum PieceType {
        LIGHT_PIECE, DARK_PIECE, NON_PIECE
    };

    private boolean crowned, captured;
    private PieceType pieceType;
	private CheckersPosition pos;
	private Rect dstRect;
    // for animation
	private Queue<Float> velX, velY; // we'll have a queue of velocities, corresponding to the pending animations (in-case more than one move is performed suddenly)
    private final int timesteps = 8;
    private int curr_time_step;



	public CheckersPiece(PieceType pieceType)
	{
		pos = new CheckersPosition();
		dstRect = new Rect();
		piece_init();
        velX = new ArrayDeque<Float>();
        velY = new ArrayDeque<Float>();
        curr_time_step = timesteps;
        this.pieceType = pieceType;
	}

	public CheckersPiece(CheckersPiece cp)
	{
		pos = new CheckersPosition();
		dstRect = new Rect();
        velX = new ArrayDeque<Float>();
        velY = new ArrayDeque<Float>();
		this.setPos(cp.get_position().get_row(), cp.get_position().get_col());
		if (cp.is_captured()) this.set_captured();
		else this.captured = false;
		this.setCrowned(cp.is_crowned());
		this.pieceType = cp.pieceType;
        curr_time_step = timesteps;
	}

    private void piece_init()
    {
        this.crowned = false;
        this.captured = false;
        this.pos.setRow(0);
        this.pos.setRow(0);
    }


    public void setPos(int row, int col)
	{
        Rect bounds = Commons.getBounds();
        if (bounds != null) {
            float sX = (col - this.pos.get_col()) * (bounds.right - bounds.left) / CheckersBoard.NUM_COLS ;
            float sY = (row - this.pos.get_row()) * (bounds.bottom - bounds.top) / CheckersBoard.NUM_ROWS ;
            this.velX.add(sX/timesteps);
            this.velY.add(sY/timesteps);
            curr_time_step = 0;
        }
        this.pos.setCol(col);
        this.pos.setRow(row);
	}

	public Rect getdstRect()
	{
		Rect bounds = Commons.getBounds();
        // animate piece to it's position
        if (curr_time_step >= timesteps) {
            if (!velX.isEmpty()) velX.remove();
            if (!velY.isEmpty()) velY.remove();
            curr_time_step = 0;
        }
        if (velX.isEmpty()) {
            dstRect.left = (int) ((bounds.right - bounds.left) / CheckersBoard.NUM_COLS * this.pos.get_col());
            dstRect.right = (int) ((int) (dstRect.left + (bounds.right - bounds.left) / ((float)CheckersBoard.NUM_COLS) ));
            dstRect.top = (int) ((bounds.bottom - bounds.top) / CheckersBoard.NUM_ROWS * (this.pos.get_row()) + bounds.top);
            dstRect.bottom = (int) ((int) (dstRect.top + (bounds.bottom - bounds.top) / ((float)CheckersBoard.NUM_ROWS) ));
        } else {
            curr_time_step += 1;
            dstRect.left += velX.peek();
            dstRect.right += velX.peek();
            dstRect.top += velY.peek();
            dstRect.bottom += velY.peek();
        }
		return dstRect;
	}

	boolean is_none_piece()
	{
		if (pos.get_row() == -1 || pos.get_col() == -1 || pieceType == PieceType.NON_PIECE) return true;
		else return false;
	}

	boolean is_captured()
	{
		return this.captured;
	}

	boolean is_crowned()
	{
		return this.crowned;
	}

    PieceType getPieceType() {return this.pieceType;}

	CheckersPosition get_position()
	{
		return this.pos;
	}

	void set_captured()
	{
		setPos(-1, -1);
        this.pieceType = PieceType.NON_PIECE;
		captured = true;
	}

	void setCrowned(boolean val)
	{
		crowned = val;
	}

	@Override
	public String toString()
	{
		String s;
		if (get_position().get_row() == -1 || is_captured())
		{
			s = ".";
		}
		else
		{
			if (pieceType == PieceType.DARK_PIECE)
			{
				s = "b";
				if (is_crowned()) s = "B";
			}
			else
			{
				s = "w";
				if (is_crowned()) s = "W";
			}
		}
		return s;
	}
}
