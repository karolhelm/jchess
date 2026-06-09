package jchess.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;

public class GameManager {
    private final Board board;
    private final CastlingRules castlingRules;
    private PieceColor currentTurn;
    private Square enPassantTarget;
    private record GameState(
            boolean whiteCastleKingside, boolean whiteCastleQueenside,
            boolean blackCastleKingside, boolean blackCastleQueenside,
            Square enPassantTarget, int halfMoveClock, int fullMoveNumber,
            GameStatus status, Move lastMove
    ) {}
    private final Deque<GameState> stateHistory = new ArrayDeque<>();
    public enum GameStatus {
        ACTIVE, WHITE_WINS, BLACK_WINS, STALEMATE, DRAW, ENDED
    }

    private GameStatus status = GameStatus.ACTIVE;

    public GameManager(){
        this.board = new Board();
        this.castlingRules = new CastlingRules(board, this::isKingInCheck);
        FenParser.loadFen(this, FenParser.STARTING_FEN);
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
    private List<Piece> capturedBlackPieces = new ArrayList<>();
    private int whiteMaterial = 39;
    private int blackMaterial = 39;
    private int halfMoveClock = 0;
    private int fullMoveNumber = 1;
    public int getHalfMoveClock() { return halfMoveClock; }
    public int getFullMoveNumber() { return fullMoveNumber; }
    public void setHalfMoveClock(int value) { halfMoveClock = value; }
    public void setFullMoveNumber(int value) { fullMoveNumber = value; }
    public List<Piece> getCapturedBlackPieces(){
        return capturedBlackPieces;
    }
    public List<Piece> getCapturedWhitePieces(){
        return capturedWhitePieces;
    }
    public void setWhiteCastleKingside(boolean v) { castlingRules.setWhiteCastleKingside(v); }
    public void setWhiteCastleQueenside(boolean v) { castlingRules.setWhiteCastleQueenside(v); }
    public void setBlackCastleKingside(boolean v) { castlingRules.setBlackCastleKingside(v); }
    public void setBlackCastleQueenside(boolean v) { castlingRules.setBlackCastleQueenside(v); }
    public boolean isWhiteCastleKingside() { return castlingRules.isWhiteCastleKingside(); }
    public boolean isWhiteCastleQueenside() { return castlingRules.isWhiteCastleQueenside(); }
    public boolean isBlackCastleKingside() { return castlingRules.isBlackCastleKingside(); }
    public boolean isBlackCastleQueenside() { return castlingRules.isBlackCastleQueenside(); }
    public Square getEnPassantTarget() { return enPassantTarget; }
    public void setEnPassantTarget(Square s) { enPassantTarget = s; }

    public void initCastlingFromBackRank() {
        castlingRules.initFromBackRank();
    }

    public void resetGraveyardFromBoard() {
        capturedWhitePieces.clear();
        capturedBlackPieces.clear();
        whiteMaterial = countMaterialOnBoard(PieceColor.WHITE);
        blackMaterial = countMaterialOnBoard(PieceColor.BLACK);
    }

    private int countMaterialOnBoard(PieceColor color) {
        int total = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Square(row, col));
                if (piece != null && piece.getColor() == color) {
                    total += piece.getValue();
                }
            }
        }
        return total;
    }

    public void playMove(Move move) {
        if (status != GameStatus.ACTIVE){
            return;
        }
        stateHistory.push(new GameState(
                castlingRules.isWhiteCastleKingside(), castlingRules.isWhiteCastleQueenside(),
                castlingRules.isBlackCastleKingside(), castlingRules.isBlackCastleQueenside(),
                enPassantTarget, halfMoveClock, fullMoveNumber,
                status, board.getLastMove()
        ));
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
        if (move.getPieceMoved().getType() == PieceType.PAWN && Math.abs(move.getStart().getRow() - move.getEnd().getRow()) == 2) {
            int dir = (move.getPieceMoved().getColor() == PieceColor.WHITE) ? 1 : -1;
            enPassantTarget = new Square(move.getEnd().getRow() + dir, move.getEnd().getCol());
        } else {
            enPassantTarget = null;
        }

        board.movePiece(move);
        board.setLastMove(move);
        castlingRules.syncRights();
        boolean pawnMove = move.getPieceMoved().getType() == PieceType.PAWN;
        boolean capture = move.isCapture() || move.isEnPassant();
        if (pawnMove || capture) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }
        if (move.getPieceMoved().getColor() == PieceColor.BLACK) {
            fullMoveNumber++;
        }
        switchTurn();
        evaluateEndConditions();
    }

    public void evaluateEndConditions() {
        updateGameStatus();
        if (status == GameStatus.ACTIVE && isInsufficientMaterial()) {
            status = GameStatus.DRAW;
        }
    }
    public void undoMove(Move move) {
        if (stateHistory.isEmpty()) {
            return;
        }
        switchTurn();
        board.undoMovePiece(move);

        GameState prevState = stateHistory.pop();
        castlingRules.setWhiteCastleKingside(prevState.whiteCastleKingside());
        castlingRules.setWhiteCastleQueenside(prevState.whiteCastleQueenside());
        castlingRules.setBlackCastleKingside(prevState.blackCastleKingside());
        castlingRules.setBlackCastleQueenside(prevState.blackCastleQueenside());
        this.enPassantTarget = prevState.enPassantTarget();
        this.halfMoveClock = prevState.halfMoveClock();
        this.fullMoveNumber = prevState.fullMoveNumber();
        this.status = prevState.status();
        board.setLastMove(prevState.lastMove());

        Piece captured = move.getPieceCaptured();
        if (captured != null) {
            if (captured.getColor() == PieceColor.WHITE) {
                capturedWhitePieces.remove(capturedWhitePieces.size() - 1);
                whiteMaterial += captured.getValue();
            } else {
                capturedBlackPieces.remove(capturedBlackPieces.size() - 1);
                blackMaterial += captured.getValue();
            }
        }
        if (move.getPromotionPiece() != null) {
            int bonus = move.getPromotionPiece().getValue() - move.getPieceMoved().getValue();
            if (move.getPieceMoved().getColor() == PieceColor.WHITE) {
                whiteMaterial -= bonus;
            } else {
                blackMaterial -= bonus;
            }
        }
    }

    public void syncCastlingRights() {
        castlingRules.syncRights();
    }

    private void switchTurn(){
        currentTurn = currentTurn.opposite();
    }

    public List<Move> getLegalMoves(Square square) {
        Piece piece = board.getPiece(square);
        List<Move> legalMoves = new ArrayList<>();

        if (piece == null || piece.getColor() != currentTurn) {
            return legalMoves;
        }

        List<Move> pseudoLegalMoves = MoveGenerator.getPossibleMoves(board, square);
        if (piece.getType() == PieceType.KING) {
            pseudoLegalMoves.addAll(castlingRules.getMoves(piece.getColor()));
        } else if (piece.getType() == PieceType.PAWN) {
            Move epMove = getEnPassantMove(square, piece.getColor());
            if (epMove != null) pseudoLegalMoves.add(epMove);
        }
        for (Move move : pseudoLegalMoves) {
            if (isMoveSafe(move)) {
                legalMoves.add(move);
            }
        }
        return castlingRules.removeKingStepsCoveredByCastling(legalMoves);
    }

    private boolean isMoveSafe(Move move) {
        if (move.isCastling()) {
            return castlingRules.isMoveSafe(move);
        }
        Square start = move.getStart();
        Square end = move.getEnd();
        Piece movingPiece = move.getPieceMoved();
        Piece capturedPiece = move.getPieceCaptured();
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

    private Move getEnPassantMove(Square start, PieceColor color) {
        if (enPassantTarget == null) return null;
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

    private boolean isInsufficientMaterial() {
        int material = 0;
        int pawns = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Square(row, col));
                if (piece == null || piece.getType() == PieceType.KING) {
                    continue;
                }
                if (piece.getType() == PieceType.PAWN) {
                    pawns++;
                }
                material += piece.getValue();
            }
        }
        return pawns == 0 && material <= 3;
    }

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
