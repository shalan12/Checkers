package com.example.checkersgame;

import java.util.ArrayList;
import java.util.InputMismatchException;

import com.example.checkersgame.CheckersPiece.PieceType;

public class GameTree
{
	ArrayList<GameTree> subTrees;
	int score;
	CheckersBoard cb;
	static final PieceType ComputerType = PieceType.DARK_PIECE;

	GameTree(CheckersBoard cb, PieceType turn, int turnNo, int maxTurns)
    {
		this.cb = cb;
		subTrees = new ArrayList<GameTree>();
		ArrayList<CheckersPiece> pieces = null;
		PieceType nextTurn = null;
		int rowInc = 0;
		boolean toMaximize;

		if (turnNo < maxTurns) {
            CheckersBoard boardNext = new CheckersBoard(cb); // make a copy of the board

            if (turn == PieceType.LIGHT_PIECE) {
                pieces = boardNext.getPieces(PieceType.LIGHT_PIECE);
                nextTurn = PieceType.DARK_PIECE;
                rowInc = 1; // lightPieces move downwards
                score = Integer.MAX_VALUE;
                toMaximize = false;
            } else {
                pieces = boardNext.getPieces(PieceType.DARK_PIECE);
                nextTurn = PieceType.LIGHT_PIECE;
                rowInc = -1; // darkPieces move upwards
                score = Integer.MIN_VALUE;
                toMaximize = true;
            }

            CheckersSimpleMove csm;
            CheckersPosition pos = new CheckersPosition();
            int i = 0;
            csm = new CheckersSimpleMove();

            for (CheckersPiece piece : pieces) {
                pos.setRow(piece.get_position().get_row());
                pos.setCol(piece.get_position().get_col());
                csm.setStart(pos);
                ArrayList<Integer> muls = new ArrayList<Integer>();
                muls.add(1);
                if (piece.is_crowned()) muls.add(-1); // if piece is crowned try moves in the opposite direction too
                for (int mul : muls) {
                    csm.setEnd(new CheckersPosition(pos.get_row() + mul * rowInc, pos.get_col() + 1)); // to move diagnoally one step
                    doMove(csm, nextTurn, turnNo, maxTurns, toMaximize);
                    csm.setEnd(new CheckersPosition(pos.get_row() + mul * rowInc, pos.get_col() - 1)); // to move one step in the other diagnol
                    doMove(csm, nextTurn, turnNo, maxTurns, toMaximize);
                    csm.setEnd(new CheckersPosition(pos.get_row() + mul * rowInc * 2, pos.get_col() + 2)); // to move two steps in diagnol
                    doMove(csm, nextTurn, turnNo, maxTurns, toMaximize);
                    csm.setEnd(new CheckersPosition(pos.get_row() + mul * rowInc * 2, pos.get_col() - 2)); // to move two steps in the other diagnol
                    doMove(csm, nextTurn, turnNo, maxTurns, toMaximize);
                }
            }
        }

		if (subTrees.size() == 0) score = EvaluateScore(); // if leaf node evaluate score

	}

	private void doMove(CheckersSimpleMove cSM, PieceType turn, int turnNo, int maxTurns,
			boolean toMaximize)
	{
		CheckersBoard boardNext = new CheckersBoard(cb); // make a copy of the old board
		if (boardNext.isMoveValid(cSM) == CheckersBoard.MOVE_VALID) // if the move can be performed
		{
			boardNext.simple_move(cSM); // perform move
			subTrees.add(new GameTree(boardNext, turn, turnNo + 1, maxTurns)); // recursively play the game for this point on
			if (toMaximize) score = Math.max(score, subTrees.get(subTrees.size() - 1).score);
			else score = Math.min(score, subTrees.get(subTrees.size() - 1).score);

		}
	}

	//  a state score based on the number of pieces of each color
	private int EvaluateScore()
	{
		int score2 = 0;
		score2 += cb.getPieceCount(PieceType.DARK_PIECE);
		score2 -= cb.getPieceCount(PieceType.LIGHT_PIECE);
		score2 += 2 * cb.getNumCrowned(PieceType.DARK_PIECE);
		score2 -= 2 * cb.getNumCrowned(PieceType.LIGHT_PIECE);

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
