package jchess.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class FenParser {
    public static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public static String resolveStartingFen(GameMode mode, String customFenOrNull) {
        if (customFenOrNull != null && !customFenOrNull.isBlank()) {
            return customFenOrNull.trim();
        }
        if (mode == GameMode.CHESS_960) {
            return generateChess960Fen();
        }
        return STARTING_FEN;
    }

    public static String generateChess960Fen() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Set<Integer> available = new HashSet<>();
        for (int col = 1; col <= 8; col++) {
            available.add(col);
        }
        char[] backRank = new char[8];

        int kingCol = random.nextInt(2, 8);
        available.remove(kingCol);
        backRank[kingCol - 1] = 'k';

        int leftRookCol = random.nextInt(1, kingCol);
        available.remove(leftRookCol);
        backRank[leftRookCol - 1] = 'r';

        int rightRookCol = random.nextInt(kingCol + 1, 9);
        available.remove(rightRookCol);
        backRank[rightRookCol - 1] = 'r';

        List<Integer> lightCols = new ArrayList<>();
        List<Integer> darkCols = new ArrayList<>();
        for (int col : available) {
            if (col % 2 == 1) {
                lightCols.add(col);
            } else {
                darkCols.add(col);
            }
        }

        int lightBishopCol = lightCols.get(random.nextInt(lightCols.size()));
        available.remove(lightBishopCol);
        backRank[lightBishopCol - 1] = 'b';

        int darkBishopCol = darkCols.get(random.nextInt(darkCols.size()));
        available.remove(darkBishopCol);
        backRank[darkBishopCol - 1] = 'b';

        List<Integer> remaining = new ArrayList<>(available);
        int queenCol = remaining.get(random.nextInt(remaining.size()));
        available.remove(queenCol);
        backRank[queenCol - 1] = 'q';

        for (int knightCol : available) {
            backRank[knightCol - 1] = 'n';
        }

        String blackBackRank = new String(backRank);
        String whiteBackRank = blackBackRank.toUpperCase();
        return blackBackRank + "/pppppppp/8/8/8/8/PPPPPPPP/" + whiteBackRank + " w KQkq - 0 1";
    }

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
        manager.initCastlingFromBackRank();
        manager.syncCastlingRights();
        manager.setStatus(GameManager.GameStatus.ACTIVE);
        manager.evaluateEndConditions();
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

