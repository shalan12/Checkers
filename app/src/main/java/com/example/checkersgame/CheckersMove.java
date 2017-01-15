package com.example.checkersgame;

import java.util.ArrayList;

public class CheckersMove
{
	ArrayList<CheckersSimpleMove> moves;

	CheckersMove(String s)
	{
		String[] temp = s.split("\\,");
		moves = new ArrayList<CheckersSimpleMove>();
		for (int i = 0; i < temp.length - 1; i++)
		{
			moves.add(new CheckersSimpleMove(temp[i], temp[i + 1]));
		}
	}

	ArrayList<CheckersSimpleMove> getMoves()
	{
		return moves;
	}
}
