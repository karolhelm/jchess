package jchess.model;

public class FenParser {
    public static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"; //starting position

    public static void loadFen(GameManager manager, String fen) {
        String[] parts = fen.split(" ");//splits FEN into parts
        String boardPart = parts[0];
        Board board = manager.getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board.setPiece(new Square(row, col), null);
            }
        }
        int row = 0;
        int col = 0;
        for (char c : boardPart.toCharArray()) {
            if (c == '/') {
                row++;  // the slash is a end of row
                col = 0;
            } else if (Character.isDigit(c)) {
                col += Character.getNumericValue(c); //digit means a number of empty squares ahead up to 8(full row) no need to
            } else {                                    //do modulo because it's limited by the slash
                Piece piece = Piece.fromFenSymbol(c);   //deciphering the Piece
                if (piece != null) {
                    board.setPiece(new Square(row, col), piece);
                    col++;
                }
            }
        }
        if (parts.length > 1) {
            if (parts[1].equals("w"))
                manager.setCurrentTurn(PieceColor.WHITE);
            else
                manager.setCurrentTurn(PieceColor.BLACK);

        }

        if (parts.length > 2) {
            String castling = parts[2];
            manager.setWhiteCastleKingside(castling.contains("K"));
            manager.setWhiteCastleQueenside(castling.contains("Q")); //checks if there is still possibility of given castling
            manager.setBlackCastleKingside(castling.contains("k"));
            manager.setBlackCastleQueenside(castling.contains("q"));
        }
        else {
            manager.setWhiteCastleKingside(false);
            manager.setWhiteCastleQueenside(false);
            manager.setBlackCastleKingside(false);
            manager.setBlackCastleQueenside(false);
        }

        // (En Passant target square)
        if(parts.length > 3 && !parts[3].equals("-")) {
            int epCol = parts[3].charAt(0) - 'a'; //column number
            int epRow = 8 - Character.getNumericValue(parts[3].charAt(1));
            manager.setEnPassantTarget(new Square(epRow, epCol));
        } else
            manager.setEnPassantTarget(null);
        if (parts.length > 4) {
            manager.setHalfMoveClock(Integer.parseInt(parts[4]));
        } else {
            manager.setHalfMoveClock(0);
        }

        if (parts.length > 5) {
            manager.setFullMoveNumber(Integer.parseInt(parts[5]));
        } else {
            manager.setFullMoveNumber(1);
        }

        manager.resetGraveyardFromBoard();
        manager.setStatus(GameManager.GameStatus.ACTIVE);
    }
    public static String toFen(GameManager manager) {
        StringBuilder fen = new StringBuilder();
        Board board = manager.getBoard();  //getting pieces positions
        for (int row = 0; row < 8; row++) {
            int empty = 0;
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Square(row, col));
                if (piece == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append(piece.getFenSymbol());
                }
            }
            if (empty > 0) {
                fen.append(empty);
            }
            if (row < 7) {
                fen.append('/');
            }
        }

        fen.append(' ');
        fen.append(manager.getCurrentTurn() == PieceColor.WHITE ? 'w' : 'b');

        fen.append(' ');
        StringBuilder castling = new StringBuilder();
        if (manager.isWhiteCastleKingside()) castling.append('K');  //turn and castlings
        if (manager.isWhiteCastleQueenside()) castling.append('Q');
        if (manager.isBlackCastleKingside()) castling.append('k');
        if (manager.isBlackCastleQueenside()) castling.append('q');
        fen.append(castling.isEmpty() ? "-" : castling);

        fen.append(' ');
        Square ep = manager.getEnPassantTarget();  //en passant square and full moves and half moves
        fen.append(ep == null ? "-" : toSquare(ep));

        fen.append(' ').append(manager.getHalfMoveClock());
        fen.append(' ').append(manager.getFullMoveNumber());
        return fen.toString();
    }
    private static String toSquare(Square sq) {
        return "" + (char) ('a' + sq.getCol()) + (8 - sq.getRow());
    }


}

