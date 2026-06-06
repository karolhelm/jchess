package jchess.model;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final Board board;
    private PieceColor currentTurn;
    private boolean whiteCastleKingside;
    private boolean whiteCastleQueenside;
    private boolean blackCastleKingside;
    private boolean blackCastleQueenside;
    private Square enPassantTarget;
    public enum GameStatus {
        ACTIVE, WHITE_WINS, BLACK_WINS, STALEMATE
    }

    private GameStatus status = GameStatus.ACTIVE;

    public GameManager(){
        this.board = new Board();
        FenParser.loadFen(this, FenParser.STARTING_FEN);
        this.currentTurn = PieceColor.WHITE;
    }

    public Board getBoard() {
        return board;
    }

    public PieceColor getCurrentTurn() {
        return currentTurn;
    }
    public void setCurrentTurn(PieceColor color){
        this.currentTurn = color;
    }
    public GameStatus getStatus() {
        return status;
    }
    public void setStatus(GameStatus newStatus) {
        this.status = newStatus;
    }
    private List<Piece> capturedWhitePieces = new ArrayList<>();
    private List<Piece> capturedBlackPieces = new ArrayList<>();  //graveyard variables
    private int whiteMaterial = 39;
    private int blackMaterial = 39;
    public List<Piece> getCapturedBlackPieces(){
        return capturedBlackPieces;
    }
    public List<Piece> getCapturedWhitePieces(){
        return capturedWhitePieces;
    }
    public void setWhiteCastleKingside(boolean v) { whiteCastleKingside = v; }
    public void setWhiteCastleQueenside(boolean v) { whiteCastleQueenside = v; }
    public void setBlackCastleKingside(boolean v) { blackCastleKingside = v; }
    public void setBlackCastleQueenside(boolean v) { blackCastleQueenside = v; }
    public void setEnPassantTarget(Square s) { enPassantTarget = s; }

    public void playMove(Move move) {
        if (status != GameStatus.ACTIVE){
            return;
        }
        Piece captured = move.getPieceCaptured();
        if(captured!=null) {
            if(captured.getColor() == PieceColor.WHITE){
                capturedWhitePieces.add(captured);
                whiteMaterial -= captured.getValue();
            }else{
                capturedBlackPieces.add(captured);
                blackMaterial -= captured.getValue();
            }
        }

        if (move.getPromotionPiece() != null){
            int bonus = move.getPromotionPiece().getValue() - move.getPieceMoved().getValue();
            if(move.getPieceMoved().getColor() == PieceColor.WHITE)
                whiteMaterial += bonus;
             else
                blackMaterial += bonus;
        }
        updateCastlingRights(move);
        if (move.getPieceMoved().getType() == PieceType.PAWN && Math.abs(move.getStart().getRow() - move.getEnd().getRow()) == 2) {
            int dir = (move.getPieceMoved().getColor() == PieceColor.WHITE) ? 1 : -1; //setting square for possible en passant move
            enPassantTarget = new Square(move.getEnd().getRow() + dir, move.getEnd().getCol());
        } else {
            enPassantTarget = null;
        }

        board.movePiece(move);
        board.setLastMove(move);
        switchTurn();
        updateGameStatus();
    }
    private void updateCastlingRights(Move move){
        Piece piece = move.getPieceMoved();
        if (piece.getType() == PieceType.KING){
            if (piece.getColor() == PieceColor.WHITE){
                whiteCastleKingside = false;
                whiteCastleQueenside = false;
            }
            else{
                blackCastleKingside = false;
                blackCastleQueenside = false;
            }
        }else if(piece.getType() == PieceType.ROOK){
            if (move.getStart().equals(new Square(7, 7))) whiteCastleKingside = false;
            if (move.getStart().equals(new Square(7, 0))) whiteCastleQueenside = false;
            if (move.getStart().equals(new Square(0, 7))) blackCastleKingside = false;
            if (move.getStart().equals(new Square(0, 0))) blackCastleQueenside = false;
        }
    }
    private void switchTurn(){
        currentTurn = currentTurn.opposite();//useful method from enum
    }

    public List<Move> getLegalMoves(Square square) {
        Piece piece = board.getPiece(square);
        List<Move> legalMoves = new ArrayList<>();

        if (piece == null || piece.getColor() != currentTurn) {
            return legalMoves;
        }

        List<Move> pseudoLegalMoves = MoveGenerator.getPossibleMoves(board, square);
        if (piece.getType() == PieceType.KING) {
            pseudoLegalMoves.addAll(getCastlingMoves(piece.getColor()));
        } else if (piece.getType() == PieceType.PAWN) {
            Move epMove = getEnPassantMove(square, piece.getColor()); //collecting moves into one list
            if (epMove != null) pseudoLegalMoves.add(epMove);
        }
        for (Move move : pseudoLegalMoves) {
            if (isMoveSafe(move)) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    private boolean isMoveSafe(Move move) {
        Square start = move.getStart();
        Square end = move.getEnd();
        Piece movingPiece = move.getPieceMoved();
        Piece capturedPiece = move.getPieceCaptured();
        // check logic when castling
        if (move.isCastling()) {
            if (isKingInCheck(movingPiece.getColor())) {
                return false;
            }
            int row = start.getRow();
            int crossedCol;
            if (end.getCol() == 6) {
                crossedCol = 5;
            } else {
                crossedCol = 3;
            }
            Square crossedSquare = new Square(row, crossedCol);
            board.setPiece(crossedSquare, movingPiece);
            board.setPiece(start, null);
            boolean isCrossedSafe = !isKingInCheck(movingPiece.getColor());
            board.setPiece(start, movingPiece);
            board.setPiece(crossedSquare, null);
            if (!isCrossedSafe) {
                return false;
            }

        }
        Square enPassantCaptureSquare = null;
        if (move.isEnPassant()) {
            enPassantCaptureSquare = new Square(start.getRow(), end.getCol());
            board.setPiece(enPassantCaptureSquare, null);
        }


        board.setPiece(end, movingPiece);
        board.setPiece(start, null);

        boolean isSafe = !isKingInCheck(movingPiece.getColor());

        board.setPiece(start, movingPiece);

        if (move.isEnPassant()) {
            board.setPiece(end, null);
            board.setPiece(enPassantCaptureSquare, capturedPiece);
        } else {
            board.setPiece(end, capturedPiece);
        }

        return isSafe;
    }

    private List<Move> getCastlingMoves(PieceColor color) {
        List<Move> moves = new ArrayList<>();
        int row = (color == PieceColor.WHITE) ? 7 : 0;
        Square kingSq = new Square(row, 4);

        boolean canKingside = (color == PieceColor.WHITE) ? whiteCastleKingside : blackCastleKingside;
        boolean canQueenside = (color == PieceColor.WHITE) ? whiteCastleQueenside : blackCastleQueenside;
        Piece king = board.getPiece(kingSq); //generating castling move

        if (canKingside && board.isEmpty(new Square(row, 5)) && board.isEmpty(new Square(row, 6))) {
            moves.add(new Move(kingSq, new Square(row, 6), king, null, null, false, true));
        }
        if (canQueenside && board.isEmpty(new Square(row, 1)) && board.isEmpty(new Square(row, 2)) && board.isEmpty(new Square(row, 3))) {
            moves.add(new Move(kingSq, new Square(row, 2), king, null, null, false, true));
        }
        return moves;
    }

    private Move getEnPassantMove(Square start, PieceColor color) {
        if (enPassantTarget == null) return null; //generating en passant move
        int dir = (color == PieceColor.WHITE) ? -1 : 1;
        if (start.getRow() + dir == enPassantTarget.getRow() && Math.abs(start.getCol() - enPassantTarget.getCol()) == 1) {
            Piece capturedPawn = board.getPiece(new Square(start.getRow(), enPassantTarget.getCol()));
            return new Move(start, enPassantTarget, board.getPiece(start), capturedPawn, null, true, false);
        }
        return null;
    }


    private boolean isKingInCheck(PieceColor color) {
        Square kingSquare = null;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = new Square(row, col);
                Piece piece = board.getPiece(square);
                if(piece != null && piece.getType() == PieceType.KING && piece.getColor() == color){
                    kingSquare = square;
                    break;
                }
            }
            if (kingSquare != null) {
                break;
            }
        }

        if (kingSquare == null) {
            return false;
        }

        PieceColor enemyColor=color.opposite();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = new Square(row, col);
                Piece piece = board.getPiece(square);
                if (piece != null && piece.getColor() == enemyColor) {
                    List<Move> enemyMoves = MoveGenerator.getPossibleMoves(board, square);
                    for (Move move : enemyMoves) {
                        if(move.getEnd().equals(kingSquare))
                            return true;
                    }
                }
            }

        }
        return false;
    }
    public boolean isInCheck(PieceColor color) {
        return isKingInCheck(color);
    }

    // checkmate and stalemate
    private void updateGameStatus() {
        boolean hasAnySafeMove = false;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = new Square(row, col);
                Piece piece = board.getPiece(square);
                if (piece != null && piece.getColor() == currentTurn) {
                    List<Move> possibleMoves = getLegalMoves(square);

                        if (!possibleMoves.isEmpty()) {
                            hasAnySafeMove = true;
                            break;
                        }
                }
                if (hasAnySafeMove) break;
            }
            if (hasAnySafeMove) break;
        }
        if (!(hasAnySafeMove)) {
            if (isKingInCheck(currentTurn)) {
                if (currentTurn == PieceColor.WHITE) {
                    status = GameStatus.BLACK_WINS;
                } else {
                    status = GameStatus.WHITE_WINS;
                }
            } else {
                status = GameStatus.STALEMATE;

            }
        }
    }
    public int getMaterialAdvantage(PieceColor color) {
        if (color == PieceColor.WHITE)
            return whiteMaterial - blackMaterial;
        else
            return blackMaterial - whiteMaterial;

    }
}
