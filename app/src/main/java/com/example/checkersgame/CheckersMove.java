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
		return "[[" + start.get_row() + ", " + start.get_col() + "][" + end.get_row() + ", " + end.get_col() + "]]";
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

	public CheckersMove(CheckersMove cm)
	{
		initVars();
		start.setRow(cm.get_start().get_row());
		start.setCol(cm.get_start().get_col());
		end.setRow(cm.get_end().get_row());
		end.setCol(cm.get_end().get_col());
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
