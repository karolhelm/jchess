package jchess.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class ChessLogicTest {
    private GameManager gameManager;
    private Board board;

    @BeforeEach
    void setUp() {
        gameManager = new GameManager();
        board = gameManager.getBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board.setPiece(new Square(r, c), null);
            }
        }
    }

    @Test
    void testCheckmateDetection() {
        board.setPiece(new Square(7, 0), new King(Piece.Color.BLACK));
        board.setPiece(new Square(7, 1), new Queen(Piece.Color.WHITE));
        board.setPiece(new Square(6, 1), new King(Piece.Color.WHITE));

        gameManager.playMove(new Move(new Square(6, 1), new Square(6, 2), board.getPiece(new Square(6, 1)), null));

        assertEquals(GameManager.GameStatus.WHITE_WINS, gameManager.getStatus());
    }

    @Test
    void testStalemateDetection() {
        board.setPiece(new Square(7, 0), new King(Piece.Color.BLACK));
        board.setPiece(new Square(6, 2), new Queen(Piece.Color.WHITE));
        board.setPiece(new Square(5, 1), new King(Piece.Color.WHITE));

        gameManager.playMove(new Move(new Square(5, 1), new Square(5, 2), board.getPiece(new Square(5, 1)), null));

        assertEquals(GameManager.GameStatus.STALEMATE, gameManager.getStatus());
    }

    @Test
    void testCastlingBlockedByPiece() {
        Square kingStart = new Square(0, 4);
        board.setPiece(kingStart, new King(Piece.Color.WHITE));
        board.setPiece(new Square(0, 7), new Rook(Piece.Color.WHITE));
        board.setPiece(new Square(0, 5), new Bishop(Piece.Color.WHITE));

        List<Move> legalMoves = gameManager.getLegalMoves(kingStart);
        assertFalse(legalMoves.stream().anyMatch(Move::isCastling));
    }

    @Test
    void testCastlingThroughCheck() {
        Square kingStart = new Square(0, 4);
        board.setPiece(kingStart, new King(Piece.Color.WHITE));
        board.setPiece(new Square(0, 7), new Rook(Piece.Color.WHITE));
        board.setPiece(new Square(7, 5), new Rook(Piece.Color.BLACK));

        List<Move> legalMoves = gameManager.getLegalMoves(kingStart);
        assertFalse(legalMoves.stream().anyMatch(Move::isCastling));
    }

    @Test
    void testEnPassantExecution() {
        Square whitePawnSquare = new Square(4, 4);
        Pawn whitePawn = new Pawn(Piece.Color.WHITE);
        board.setPiece(whitePawnSquare, whitePawn);

        Square blackPawnStart = new Square(6, 5);
        Square blackPawnEnd = new Square(4, 5);
        Pawn blackPawn = new Pawn(Piece.Color.BLACK);
        board.setPiece(blackPawnStart, blackPawn);

        Move blackDoubleMove = new Move(blackPawnStart, blackPawnEnd, blackPawn, null);
        board.setLastMove(blackDoubleMove);

        Square epTarget = new Square(5, 5);
        Move epMove = new Move(whitePawnSquare, epTarget, whitePawn, blackPawn);
        epMove.setEnPassant(true);

        board.movePiece(epMove);

        assertNull(board.getPiece(new Square(4, 5)));
        assertEquals(whitePawn, board.getPiece(epTarget));
    }

    @Test
    void testAutomaticPromotionToQueen() {
        Square start = new Square(6, 0);
        Square end = new Square(7, 0);
        Pawn pawn = new Pawn(Piece.Color.WHITE);
        board.setPiece(start, pawn);

        Move move = new Move(start, end, pawn, null);
        board.movePiece(move);

        assertTrue(board.getPiece(end) instanceof Queen);
    }

    @Test
    void testKingCannotMoveIntoCheck() {
        Square kingSquare = new Square(0, 4);
        board.setPiece(kingSquare, new King(Piece.Color.WHITE));
        board.setPiece(new Square(7, 3), new Rook(Piece.Color.BLACK));

        List<Move> legalMoves = gameManager.getLegalMoves(kingSquare);
        assertFalse(legalMoves.stream().anyMatch(m -> m.getEnd().equals(new Square(0, 3))));
    }

    @Test
    void testCastlingImpossibleIfKingMoved() {
        Square kingSquare = new Square(0, 4);
        King king = new King(Piece.Color.WHITE);
        king.setHasMoved(true);
        board.setPiece(kingSquare, king);
        board.setPiece(new Square(0, 7), new Rook(Piece.Color.WHITE));

        List<Move> legalMoves = gameManager.getLegalMoves(kingSquare);
        assertFalse(legalMoves.stream().anyMatch(Move::isCastling));
    }

    @Test
    void testCastlingImpossibleIfRookMoved() {
        Square kingSquare = new Square(0, 4);
        board.setPiece(kingSquare, new King(Piece.Color.WHITE));

        Square rookSquare = new Square(0, 7);
        Rook rook = new Rook(Piece.Color.WHITE);
        rook.setHasMoved(true);
        board.setPiece(rookSquare, rook);

        List<Move> legalMoves = gameManager.getLegalMoves(kingSquare);
        assertFalse(legalMoves.stream().anyMatch(Move::isCastling));
    }
    @Test
    void testInitialBoardSetupHas32Pieces() {
        Board startingBoard = new Board();
        int pieceCount = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (startingBoard.getPiece(new Square(r, c)) != null) {
                    pieceCount++;
                }
            }
        }
        assertEquals(32, pieceCount);
    }
}