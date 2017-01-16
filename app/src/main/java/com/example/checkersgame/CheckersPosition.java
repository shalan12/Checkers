package com.example.checkersgame;

public class CheckersPosition
{
	private int row, col;

	public void setRow(int r)
	{
		row = r;
	}

	public void setCol(int c)
	{
		col = c;
	}

	public void position_parse(String s)
	{
		s = s.toUpperCase();
		this.col = s.charAt(0) - 'A';
		this.row = 7 - (s.charAt(1) - '1');
	}

	public CheckersPosition(int r, int c)
	{
		row = r;
		col = c;
	}

	public CheckersPosition()
	{
		row = -1;
		col = -1;
	}

	int get_row()
	{
		return this.row;
	}
	int get_col()
	{
		return this.col;
	}
}
