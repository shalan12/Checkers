package com.example.checkersgame;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

import com.example.checkersgame.CheckersPiece.PieceType;

public class GameTreePlayer
{
    CheckersBoard cb;
    int maxTurns;

	GameTreePlayer(CheckersBoard cb, int maxTurns)
    {
        this.cb = cb;
        this.maxTurns = maxTurns;
	}

    public CheckersMove getBestMove() {
        return getBestMoveScore(this.cb, 0, maxTurns).second;
    }

    private Pair<Integer, CheckersMove> getBestMoveScore(CheckersBoard cb, int turnNo, int maxTurns) {
        int score;
        CheckersMove move = null;

        if (turnNo < maxTurns) {
            ArrayList<CheckersPiece> pieces = cb.getPieces(cb.get_turn());
            CheckersMove cm = new CheckersMove();
            CheckersPosition pos = new CheckersPosition();
            int rowDir;
            boolean toMaximize;
            if (cb.get_turn() == PieceType.DARK_PIECE) {
                score = Integer.MIN_VALUE;
                toMaximize = true;
                rowDir = -1;
            } else {
                score = Integer.MAX_VALUE;
                toMaximize = false;
                rowDir = 1;
            }
            for (CheckersPiece piece : pieces) {
                pos.setRow(piece.get_position().get_row());
                pos.setCol(piece.get_position().get_col());
                cm.setStart(pos);
                ArrayList<Integer> muls = new ArrayList<Integer>();
                int[] colIncs = {1, -1, 2, -2};
                int[] rowIncs = {rowDir, rowDir, rowDir * 2, rowDir * 2};
                muls.add(1);
                if (piece.is_crowned()) muls.add(-1); // if piece is crowned try moves in the opposite direction too
                for (int mul : muls) {
                    for (int i = 0; i < colIncs.length; i++) {
                        cm.setEnd(new CheckersPosition(pos.get_row() + mul * rowIncs[i], pos.get_col() + colIncs[i])); // to move diagonally one step
                        if (cb.isMoveValid(cm) == CheckersBoard.MOVE.MOVE_VALID) {
                            Pair<Integer, CheckersMove> tempMoveScore = getBestMoveScore(doMove(cb, cm), turnNo + 1, maxTurns);
                            if ((tempMoveScore.first > score && toMaximize) || (tempMoveScore.first < score && !toMaximize)) {
                                score = tempMoveScore.first;
                                move = new CheckersMove(cm); // copy move
                            }
                        }
                    }
                }
            }
        } else {
            score = EvaluateScore(cb);
        }

        return new Pair<Integer, CheckersMove>(score, move);
    }

    // returns the board that results by performing move 'cm' on board 'cb'
	private CheckersBoard doMove(CheckersBoard cb, CheckersMove cm)
	{
		CheckersBoard boardNext = new CheckersBoard(cb); // make a copy of the old board
		boardNext.move(cm); // perform move
        return boardNext;
	}

	//  a state score based on the number of pieces of each color
	private int EvaluateScore(CheckersBoard cb)
	{
		int score2 = 0;
		score2 += cb.getPieceCount(PieceType.DARK_PIECE);
		score2 -= cb.getPieceCount(PieceType.LIGHT_PIECE);
		score2 += 2 * cb.getNumCrowned(PieceType.DARK_PIECE);
		score2 -= 2 * cb.getNumCrowned(PieceType.LIGHT_PIECE);

		return score2;
	}
}