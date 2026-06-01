package jchess.model;

public enum Piece {
    WHITE_PAWN(PieceType.PAWN, PieceColor.WHITE),
    WHITE_KNIGHT(PieceType.KNIGHT, PieceColor.WHITE),
    WHITE_BISHOP(PieceType.BISHOP, PieceColor.WHITE),
    WHITE_ROOK(PieceType.ROOK, PieceColor.WHITE),
    WHITE_QUEEN(PieceType.QUEEN, PieceColor.WHITE),
    WHITE_KING(PieceType.KING, PieceColor.WHITE),

    BLACK_PAWN(PieceType.PAWN, PieceColor.BLACK),  //enum
    BLACK_KNIGHT(PieceType.KNIGHT, PieceColor.BLACK),
    BLACK_BISHOP(PieceType.BISHOP, PieceColor.BLACK),
    BLACK_ROOK(PieceType.ROOK, PieceColor.BLACK),
    BLACK_QUEEN(PieceType.QUEEN, PieceColor.BLACK),
    BLACK_KING(PieceType.KING, PieceColor.BLACK);

    private final PieceType type;
    private final PieceColor color;

    Piece(PieceType type, PieceColor color){
        this.type = type;
        this.color = color;
    }

    public PieceType getType(){
        return type;
    }
    public PieceColor getColor(){
        return color;
    }           //simple getters
    public int getValue(){
        return type.getValue();
    }


    public char getFenSymbol(){
        if(this.color == PieceColor.WHITE) //converting board  to FEN
            return Character.toUpperCase(type.getSymbol());
        else
            return type.getSymbol();
    }

            //converting FEN to board
    public static Piece fromFenSymbol(char symbol){
        for(Piece piece : values()){
            if (piece.getFenSymbol() == symbol)
                return piece;
        }
        return null;
    }
}
