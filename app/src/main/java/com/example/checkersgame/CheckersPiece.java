package com.example.checkersgame;

import android.graphics.Rect;

public class CheckersPiece
{
	private boolean crowned, captured, dark;
	private CheckersPosition pos;
	private Rect dstRect;

	public enum PieceType {
		LIGHT_PIECE, DARK_PIECE
	};

	public CheckersPiece()
	{
		pos = new CheckersPosition();
		dstRect = new Rect();
		piece_init();
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
	}

	public void setPos(int row, int col)
	{
		this.pos.setRow(row);
		this.pos.setCol(col);
	}

	public Rect getdstRect()
	{
		Rect bounds = Commons.getBounds();
		dstRect.left = (bounds.right - bounds.left) / 8 * this.pos.get_col();
		dstRect.right = (int) (dstRect.left + (bounds.right - bounds.left) / 8f);
		dstRect.top = (bounds.bottom - bounds.top) / 8 * (this.pos.get_row()) + bounds.top;
		dstRect.bottom = (int) (dstRect.top + (bounds.bottom - bounds.top) / 8f);
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
