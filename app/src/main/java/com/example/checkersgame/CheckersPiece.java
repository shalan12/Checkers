package com.example.checkersgame;

public class CheckersPiece
{
    public enum PieceType {
        LIGHT_PIECE, DARK_PIECE, NON_PIECE
    };

    private boolean crowned, captured;
    private PieceType pieceType;
	private CheckersPosition pos;

	public CheckersPiece(PieceType pieceType)
	{
		pos = new CheckersPosition();
		piece_init();
        this.pieceType = pieceType;
	}

	public CheckersPiece(CheckersPiece cp)
	{
		pos = new CheckersPosition();
		this.setPos(cp.get_position().get_row(), cp.get_position().get_col());
		if (cp.is_captured()) this.set_captured();
		else this.captured = false;
		this.setCrowned(cp.is_crowned());
		this.pieceType = cp.pieceType;
	}

    private void piece_init()
    {
        this.crowned = false;
        this.captured = false;
        this.pos.setRow(0);
        this.pos.setRow(0);
    }


    public void setPos(int row, int col)
	{
        this.pos.setCol(col);
        this.pos.setRow(row);
	}

	boolean is_none_piece()
	{
		if (pos.get_row() == -1 || pos.get_col() == -1 || pieceType == PieceType.NON_PIECE) return true;
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

    PieceType getPieceType() {return this.pieceType;}

	CheckersPosition get_position()
	{
		return this.pos;
	}

	void set_captured()
	{
		setPos(-1, -1);
        this.pieceType = PieceType.NON_PIECE;
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
			if (pieceType == PieceType.DARK_PIECE)
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
