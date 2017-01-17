package com.example.checkersgame;

import java.util.ArrayList;

import com.example.checkersgame.CheckersPiece.PieceType;

public class GameTree
{
	ArrayList<GameTree> subTrees;
    private int score;
	private CheckersBoard cb;
	static final PieceType ComputerType = PieceType.DARK_PIECE;

	GameTree(CheckersBoard cb, int turnNo, int maxTurns)
    {
		this.cb = cb;
		subTrees = new ArrayList<GameTree>();
		ArrayList<CheckersPiece> pieces;
		int rowInc;
		boolean toMaximize;

		if (turnNo < maxTurns) {
            CheckersBoard boardNext = new CheckersBoard(cb); // make a copy of the board

            if (cb.get_turn()== PieceType.LIGHT_PIECE) {
                pieces = boardNext.getPieces(PieceType.LIGHT_PIECE);
                rowInc = 1; // lightPieces move downwards
                score = Integer.MAX_VALUE;
                toMaximize = false;
            } else {
                pieces = boardNext.getPieces(PieceType.DARK_PIECE);
                rowInc = -1; // darkPieces move upwards
                score = Integer.MIN_VALUE;
                toMaximize = true;
            }

            CheckersMove cm = new CheckersMove();
            CheckersPosition pos = new CheckersPosition();

            for (CheckersPiece piece : pieces) {
                pos.setRow(piece.get_position().get_row());
                pos.setCol(piece.get_position().get_col());
                cm.setStart(pos);
                ArrayList<Integer> muls = new ArrayList<Integer>();
                muls.add(1);
                if (piece.is_crowned()) muls.add(-1); // if piece is crowned try moves in the opposite direction too
                for (int mul : muls) {
                    cm.setEnd(new CheckersPosition(pos.get_row() + mul * rowInc, pos.get_col() + 1)); // to move diagonally one step
                    doMove(cm, turnNo, maxTurns, toMaximize);
                    cm.setEnd(new CheckersPosition(pos.get_row() + mul * rowInc, pos.get_col() - 1)); // to move one step in the other diagonal
                    doMove(cm, turnNo, maxTurns, toMaximize);
                    cm.setEnd(new CheckersPosition(pos.get_row() + mul * rowInc * 2, pos.get_col() + 2)); // to move two steps in diagonal
                    doMove(cm, turnNo, maxTurns, toMaximize);
                    cm.setEnd(new CheckersPosition(pos.get_row() + mul * rowInc * 2, pos.get_col() - 2)); // to move two steps in the other diagonal
                    doMove(cm, turnNo, maxTurns, toMaximize);
                }
            }
        }

		if (subTrees.size() == 0) score = EvaluateScore(); // if leaf node evaluate score

	}

	private void doMove(CheckersMove cm, int turnNo, int maxTurns,
                        boolean toMaximize)
	{
		CheckersBoard boardNext = new CheckersBoard(cb); // make a copy of the old board
		if (boardNext.isMoveValid(cm) == CheckersBoard.MOVE.MOVE_VALID) // if the move can be performed
		{
			boardNext.move(cm); // perform move
			subTrees.add(new GameTree(boardNext, turnNo + 1, maxTurns)); // recursively play the game for this point on
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

	public CheckersMove playMove()
	{
		CheckersBoard nextBoard = null;
        // find the board corresponding to the best move
        for (int i = 0; i < subTrees.size(); i++) {
			if (this.score == subTrees.get(i).score) {
				nextBoard = subTrees.get(i).cb;
				break;
			}
		}

		CheckersMove cm = new CheckersMove();
        // find the move performed to get to that board
        for (int i = 0; i < CheckersBoard.NUM_ROWS; i++) {
			for (int j = 0; j < CheckersBoard.NUM_COLS; j++) {
                if (nextBoard.getPieceAt(i, j).is_none_piece() && !this.cb.getPieceAt(i, j).is_none_piece()) {
                    cm.setStart(new CheckersPosition(i, j));
                } else if (this.cb.getPieceAt(i, j).is_none_piece() && !nextBoard.getPieceAt(i, j).is_none_piece()) {
					cm.setEnd(new CheckersPosition(i, j));
                }
			}
		}

		return cm;
	}
}
