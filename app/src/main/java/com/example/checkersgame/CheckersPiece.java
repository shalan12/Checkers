package com.example.checkersgame;

import android.graphics.Rect;
import android.util.Log;

public class CheckersPiece
{
	private boolean crowned, captured, dark;
	private CheckersPosition pos;
	private Rect dstRect;
	private float velX, velY;
    private final int timesteps = 3;
    private int curr_time_step;

	public enum PieceType {
		LIGHT_PIECE, DARK_PIECE
	};

	public CheckersPiece()
	{
		pos = new CheckersPosition();
		dstRect = new Rect();
		piece_init();
        velX = velY = 0;
        curr_time_step = timesteps;
	}

	public CheckersPiece(CheckersPiece cp)
	{
		pos = new CheckersPosition();
		dstRect = new Rect();
		this.setPos(cp.get_position().get_row(), cp.get_position().get_col());
		if (cp.is_captured()) this.set_captured();
		else this.captured = false;
		this.setCrowned(cp.is_crowned());
		if (cp.is_dark()) this.setDark(true);
		else this.setDark(false);
        curr_time_step = 0;
	}

	public void setPos(int row, int col)
	{
        Rect bounds = Commons.getBounds();
        if (bounds != null) {
            float sX = (col - this.pos.get_col()) * (bounds.right - bounds.left) / CheckersBoard.NUM_COLS ;
            float sY = (row - this.pos.get_row()) * (bounds.bottom - bounds.top) / CheckersBoard.NUM_ROWS ;
            this.velX = sX/timesteps;
            this.velY = sY/timesteps;

            curr_time_step = 0;
        }
        this.pos.setCol(col);
        this.pos.setRow(row);
	}

	public Rect getdstRect()
	{
		Rect bounds = Commons.getBounds();
        if (curr_time_step < timesteps) {
            curr_time_step += 1;
            dstRect.left += velX;
            dstRect.right += velX;
            dstRect.top += velY;
            dstRect.bottom += velY;
        } else {
            dstRect.left = (int) ((bounds.right - bounds.left) / CheckersBoard.NUM_COLS * this.pos.get_col());
            dstRect.right = (int) ((int) (dstRect.left + (bounds.right - bounds.left) / ((float)CheckersBoard.NUM_COLS) ));
            dstRect.top = (int) ((bounds.bottom - bounds.top) / CheckersBoard.NUM_ROWS * (this.pos.get_row()) + bounds.top);
            dstRect.bottom = (int) ((int) (dstRect.top + (bounds.bottom - bounds.top) / ((float)CheckersBoard.NUM_ROWS) ));
        }
		return dstRect;
	}

	public void piece_init()
	{
		this.crowned = false;
		this.captured = false;
		this.dark = false;
		this.pos.setRow(0);
		this.pos.setRow(0);
	}

	public void setDark(boolean isDark)
	{
		dark = isDark;
	}

	boolean is_none_piece()
	{
		if (pos.get_row() == -1 || pos.get_col() == -1) return true;
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

	boolean is_dark()
	{
		return this.dark;
	}

	CheckersPosition get_position()
	{
		return this.pos;
	}

	void set_captured()
	{
		setPos(-1, -1);
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
			if (is_dark())
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
