package com.example.checkersgame;

public class CheckersMove
{
	private CheckersPosition start, end;

	public void setStart(CheckersPosition start)
	{
		this.start = start;
	}

	public void setEnd(CheckersPosition end)
	{
		this.end = end;
	}

	@Override
	public String toString()
	{
		return "Start : row = " + start.get_row() + " col = " + start.get_col() + "\nEnd : row = "
				+ end.get_row() + " col = " + end.get_col();
	}

	public void initVars()
	{
		start = new CheckersPosition();
		end = new CheckersPosition();
	}

	public CheckersMove()
	{
		initVars();
		start.setRow(-1);
		start.setCol(-1);
		end.setRow(-1);
		end.setCol(-1);
	}

	public CheckersMove(String s1, String s2)
	{
		initVars();
		start.position_parse(s1);
		end.position_parse(s2);
	}

	public CheckersPosition get_start()
	{
		return start;
	}

	public CheckersPosition get_end()
	{
		return end;
	}
}
