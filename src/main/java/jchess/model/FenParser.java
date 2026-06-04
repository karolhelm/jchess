package jchess.model;

public class FenParser {
    public static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"; //starting position

    public static void loadFen(GameManager manager, String fen){
        String[] parts = fen.split(" ");//splits FEN into parts
        String boardPart=parts[0];
        Board board = manager.getBoard();

        int row = 0;
        int col = 0;
        for (char c : boardPart.toCharArray()){
            if(c == '/'){
                row++;  // the slash is a end of row
                col = 0;
            }else if(Character.isDigit(c)){
                col += Character.getNumericValue(c); //digit means a number of empty squares ahead up to 8(full row) no need to
            }else{                                    //do modulo because it's limited by the slash
                Piece piece = Piece.fromFenSymbol(c);   //deciphering the Piece
                if(piece != null){
                    board.setPiece(new Square(row, col), piece);
                    col++;
                }
            }
        }
        if (parts.length > 1){
            if (parts[1].equals("w"))
                manager.setCurrentTurn(PieceColor.WHITE);
            else
                manager.setCurrentTurn(PieceColor.BLACK);

        }

        if (parts.length > 2){
            String castling = parts[2];
            manager.setWhiteCastleKingside(castling.contains("K"));
            manager.setWhiteCastleQueenside(castling.contains("Q")); //checks if there is still possibility of given castling
            manager.setBlackCastleKingside(castling.contains("k"));
            manager.setBlackCastleQueenside(castling.contains("q"));
        }

        // (En Passant target square)
        if (parts.length > 3 && !parts[3].equals("-")){
            int epCol = parts[3].charAt(0) - 'a'; //column number
            int epRow = 8 - Character.getNumericValue(parts[3].charAt(1));
            manager.setEnPassantTarget(new Square(epRow, epCol));
        }else
            manager.setEnPassantTarget(null);
    }
}
