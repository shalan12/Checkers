package com.example.checkersgame;

import java.util.ArrayList;

import com.example.checkersgame.CheckersPiece.PieceType;

public class GameTree
{
	ArrayList<GameTree> subTrees;
	int score;
	CheckersBoard cb;
	static final PieceType ComputerType = PieceType.DARK_PIECE;

	GameTree(CheckersBoard cb, int turn, int turnNo, int maxTurns)
	{
		this.cb = cb;
		subTrees = new ArrayList<GameTree>();
		ArrayList<CheckersPiece> pieces = null;
		int nextTurn = -1;
		int rowInc = 0;
		boolean toMaximize;
		// //system.out.println("turnno = " + turnNo);
		// //system.out.println("max turnno = " + maxTurns);

		if (turnNo < maxTurns)
		{
			// //system.out.println("turnno < maxTurns");
			CheckersBoard boardNext;

			boardNext = new CheckersBoard(cb);

			if (turn == CheckersBoard.TURN_LIGHT)
			{
				pieces = boardNext.getPieces(PieceType.LIGHT_PIECE);
				nextTurn = CheckersBoard.TURN_DARK;
				rowInc = 1; // lightPieces move downwards
				score = Integer.MAX_VALUE;
				toMaximize = false;
			}
			else
			{
				pieces = boardNext.getPieces(PieceType.DARK_PIECE);
				nextTurn = CheckersBoard.TURN_LIGHT;
				rowInc = -1; // darkPieces move upwards
				score = Integer.MIN_VALUE;
				toMaximize = true;
			}
			CheckersSimpleMove csm;
			CheckersPosition pos = new CheckersPosition();
			int i = 0;
			csm = new CheckersSimpleMove();

			for (CheckersPiece piece : pieces)
			{
				System.out.println("i  = " + i++);
				pos.setRow(piece.get_position().get_row());
				pos.setCol(piece.get_position().get_col());
				csm.setStart(pos);
				csm.setEnd(new CheckersPosition(pos.get_row() + rowInc, pos.get_col() + 1));
				doMove(csm, nextTurn, turnNo, maxTurns, toMaximize);
				csm.setEnd(new CheckersPosition(pos.get_row() + rowInc, pos.get_col() - 1));
				doMove(csm, nextTurn, turnNo, maxTurns, toMaximize);
				csm.setEnd(new CheckersPosition(pos.get_row() + rowInc * 2, pos.get_col() + 2));
				doMove(csm, nextTurn, turnNo, maxTurns, toMaximize);
				csm.setEnd(new CheckersPosition(pos.get_row() + rowInc * 2, pos.get_col() - 2));
				doMove(csm, nextTurn, turnNo, maxTurns, toMaximize);
				if (piece.is_crowned())
				{
					csm.setEnd(new CheckersPosition(pos.get_row() + -1 * rowInc, pos.get_col() + 1));
					doMove(csm, nextTurn, turnNo, maxTurns, toMaximize);
					csm.setEnd(new CheckersPosition(pos.get_row() + -1 * rowInc, pos.get_col() - 1));
					doMove(csm, nextTurn, turnNo, maxTurns, toMaximize);
					csm.setEnd(new CheckersPosition(pos.get_row() + -1 * rowInc * 2,
							pos.get_col() + 2));
					doMove(csm, nextTurn, turnNo, maxTurns, toMaximize);
					csm.setEnd(new CheckersPosition(pos.get_row() + -1 * rowInc * 2,
							pos.get_col() - 2));
				}
			}
		}
		else
		{
			score = EvaluateScore();

		}
		if (subTrees.size() == 0) score = EvaluateScore();

	}

	private void doMove(CheckersSimpleMove cSM, int turn, int turnNo, int maxTurns,
			boolean toMaximize)
	{
		CheckersBoard boardNext = new CheckersBoard(cb);
		if (boardNext.isMoveValid(cSM) == 0)
		{
			boardNext.simple_move(cSM);
			subTrees.add(new GameTree(boardNext, turn, turnNo + 1, maxTurns));
			if (toMaximize) score = Math.max(score, subTrees.get(subTrees.size() - 1).score);
			else score = Math.min(score, subTrees.get(subTrees.size() - 1).score);

		}
	}

	private int EvaluateScore()
	{
		int score2 = 0;
		// //system.out.println("score2 = " + score2);
		score2 += cb.getPieceCount(PieceType.DARK_PIECE);
		score2 -= cb.getPieceCount(PieceType.LIGHT_PIECE);
		score2 += 2 * cb.getNumCrowned(PieceType.DARK_PIECE);
		score2 -= 2 * cb.getNumCrowned(PieceType.LIGHT_PIECE);
		// system.out.println("score2 = " + score2);

		return score2;
	}

	public CheckersSimpleMove playMove()
	{
		CheckersBoard nextBoard = null;
		for (int i = 0; i < subTrees.size(); i++)
		{
			if (this.score == subTrees.get(i).score)
			{
				nextBoard = subTrees.get(i).cb;
				break;
			}
		}
		CheckersSimpleMove csm = new CheckersSimpleMove();
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				if (!nextBoard.board_get_piece_at(i, j).is_none_piece()
						&& this.cb.board_get_piece_at(i, j).is_none_piece())
				{
					csm.setEnd(new CheckersPosition(i, j));
				}
				else if (nextBoard.board_get_piece_at(i, j).is_none_piece()
						&& !this.cb.board_get_piece_at(i, j).is_none_piece())
				{
					csm.setStart(new CheckersPosition(i, j));
				}
			}
		}
		return csm;
	}
}
