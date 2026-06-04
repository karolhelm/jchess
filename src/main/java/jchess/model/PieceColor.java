package jchess.model;

public enum PieceColor{
    WHITE, BLACK;

    public PieceColor opposite(){
        if(this==WHITE)
            return BLACK;
        else
            return WHITE;
    }
}
