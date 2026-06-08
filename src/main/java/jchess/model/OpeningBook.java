package jchess.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OpeningBook {

    private static final Map<String, List<String>> BOOK = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        // White's choices from the starting position
        add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -",
                "e2e4", "d2d4", "c2c4", "g1f3");

        // ===== 1. e4 =====
        add("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3",
                "e7e5", "c7c5", "e7e6", "c7c6");

        // 1.e4 e5
        add("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6",
                "g1f3", "f1c4", "b1c3");

        // 1.e4 e5 2.Nf3
        add("rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq -",
                "b8c6", "g8f6");

        // 1.e4 e5 2.Nf3 Nc6
        add("r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq -",
                "f1b5", "f1c4", "b1c3");

        // 1.e4 e5 2.Nf3 Nc6 3.Bb5 (Spanish)
        add("r1bqkbnr/1ppp1ppp/p1n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq -",
                "b5a4", "b5c6");
        add("r1bqkbnr/pppp1ppp/2n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq -",
                "a7a6", "g8f6");

        // 1.e4 e5 2.Nf3 Nc6 3.Bc4 (Italian)
        add("r1bqkbnr/pppp1ppp/2n5/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R b KQkq -",
                "g8f6", "f8c5");

        // ===== 1.e4 c5 (Sicilian) =====
        add("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6",
                "g1f3", "b1c3");

        // 1.e4 c5 2.Nf3
        add("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq -",
                "d7d6", "b8c6", "g8f6");

        // ===== 1.e4 e6 (French) =====
        add("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -",
                "d2d4", "b1c3");

        // 1.e4 e6 2.d4
        add("rnbqkbnr/pppp1ppp/4p3/8/3PP3/8/PPP2PPP/RNBQKBNR b KQkq d3",
                "d7d5");

        // ===== 1.e4 c6 (Caro-Kann) =====
        add("rnbqkbnr/pp1ppppp/2p5/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -",
                "d2d4", "b1c3");

        // 1.e4 c6 2.d4
        add("rnbqkbnr/pp1ppppp/2p5/8/3PP3/8/PPP2PPP/RNBQKBNR b KQkq d3",
                "d7d5");

        // ===== 1. d4 =====
        add("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3",
                "d7d5", "g8f6", "e7e6");

        // 1.d4 d5
        add("rnbqkbnr/ppp1pppp/8/3p4/3P4/8/PPP1PPPP/RNBQKBNR w KQkq d6",
                "c2c4", "g1f3");

        // 1.d4 d5 2.c4 (Queen's Gambit)
        add("rnbqkbnr/ppp1pppp/8/3p4/2PP4/8/PP2PPPP/RNBQKBNR b KQkq c3",
                "e7e6", "c7c6");

        // 1.d4 Nf6
        add("rnbqkb1r/pppppppp/5n2/8/3P4/8/PPP1PPPP/RNBQKBNR w KQkq -",
                "c2c4", "g1f3");

        // 1.d4 Nf6 2.c4
        add("rnbqkb1r/pppppppp/5n2/8/2PP4/8/PP2PPPP/RNBQKBNR b KQkq c3",
                "e7e6", "g7g6");

        // 1.d4 e6
        add("rnbqkbnr/pppp1ppp/4p3/8/3P4/8/PPP1PPPP/RNBQKBNR w KQkq -",
                "c2c4", "g1f3");

        // ===== 1. c4 (English) =====
        add("rnbqkbnr/pppppppp/8/8/2P5/8/PP1PPPPP/RNBQKBNR b KQkq c3",
                "e7e5", "g8f6", "c7c5");

        // ===== 1. Nf3 =====
        add("rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKBNR b KQkq -",
                "g8f6", "d7d5");

        // ===========================================================
        //  Deeper 1.e4 e5 lines
        // ===========================================================

        // 1.e4 e5 2.Nf3 Nc6 3.Nc3 (Three Knights)
        add("r1bqkbnr/pppp1ppp/2n5/4p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R b KQkq -",
                "g8f6", "f8c5");

        // 1.e4 e5 2.Nf3 Nc6 3.Nc3 Nf6 (Four Knights)
        add("r1bqkb1r/pppp1ppp/2n2n2/4p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R w KQkq -",
                "f1b5", "f1c4", "d2d4");

        // 1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4
        add("r1bqkbnr/1ppp1ppp/p1n5/4p3/B3P3/5N2/PPPP1PPP/RNBQK2R b KQkq -",
                "g8f6");

        // 1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6
        add("r1bqkb1r/1ppp1ppp/p1n2n2/4p3/B3P3/5N2/PPPP1PPP/RNBQK2R w KQkq -",
                "e1g1", "d2d3");

        // 1.e4 e5 2.Nf3 Nc6 3.Bc4 Nf6 (Two Knights)
        add("r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq -",
                "d2d3", "b1c3", "f3g5");

        // 1.e4 e5 2.Nf3 Nc6 3.Bc4 Bc5 (Giuoco Piano)
        add("r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq -",
                "c2c3", "d2d3", "b1c3");

        // 1.e4 e5 2.Nf3 Nf6 (Petrov)
        add("rnbqkb1r/pppp1ppp/5n2/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq -",
                "f3e5", "b1c3", "d2d4");

        // 1.e4 e5 2.Nf3 Nf6 3.Nxe5
        add("rnbqkb1r/pppp1ppp/5n2/4N3/4P3/8/PPPP1PPP/RNBQKB1R b KQkq -",
                "d7d6");

        // 1.e4 e5 2.Nf3 Nf6 3.Nxe5 d6 4.Nf3
        add("rnbqkb1r/ppp2ppp/3p1n2/8/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq -",
                "f6e4");

        // ===========================================================
        //  Scandinavian (1.e4 d5)
        // ===========================================================

        add("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6",
                "e4d5");
        add("rnbqkbnr/ppp1pppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR b KQkq -",
                "d8d5", "g8f6");
        add("rnb1kbnr/ppp1pppp/8/3q4/8/8/PPPP1PPP/RNBQKBNR w KQkq -",
                "b1c3");
        add("rnb1kbnr/ppp1pppp/8/3q4/8/2N5/PPPP1PPP/R1BQKBNR b KQkq -",
                "d5a5");

        // ===========================================================
        //  Pirc / Modern (1.e4 d6)
        // ===========================================================

        add("rnbqkbnr/ppp1pppp/3p4/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -",
                "d2d4");
        add("rnbqkbnr/ppp1pppp/3p4/8/3PP3/8/PPP2PPP/RNBQKBNR b KQkq d3",
                "g8f6");
        add("rnbqkb1r/ppp1pppp/3p1n2/8/3PP3/8/PPP2PPP/RNBQKBNR w KQkq -",
                "b1c3");
        add("rnbqkb1r/ppp1pppp/3p1n2/8/3PP3/2N5/PPP2PPP/R1BQKBNR b KQkq -",
                "g7g6");

        // ===========================================================
        //  Sicilian deeper lines
        // ===========================================================

        // 1.e4 c5 2.Nf3 d6
        add("rnbqkbnr/pp2pppp/3p4/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq -",
                "d2d4");
        // 1.e4 c5 2.Nf3 d6 3.d4
        add("rnbqkbnr/pp2pppp/3p4/2p5/3PP3/5N2/PPP2PPP/RNBQKB1R b KQkq d3",
                "c5d4");
        // 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4
        add("rnbqkbnr/pp2pppp/3p4/8/3NP3/8/PPP2PPP/RNBQKB1R b KQkq -",
                "g8f6");
        // 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 (Open Sicilian)
        add("rnbqkb1r/pp2pppp/3p1n2/8/3NP3/2N5/PPP2PPP/R1BQKB1R b KQkq -",
                "a7a6", "b8c6", "g7g6");

        // 1.e4 c5 2.Nf3 Nc6
        add("r1bqkbnr/pp1ppppp/2n5/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq -",
                "d2d4", "f1b5");
        // 1.e4 c5 2.Nf3 Nc6 3.d4
        add("r1bqkbnr/pp1ppppp/2n5/2p5/3PP3/5N2/PPPP1PPP/RNBQKB1R b KQkq d3",
                "c5d4");
        // 1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4
        add("r1bqkbnr/pp1ppppp/2n5/8/3NP3/8/PPPP1PPP/RNBQKB1R b KQkq -",
                "g8f6", "g7g6");

        // 1.e4 c5 2.Nf3 Nf6 3.e5 (Anti-Sveshnikov-ish)
        add("rnbqkb1r/pp1ppppp/5n2/2p1P3/8/5N2/PPPP1PPP/RNBQKB1R b KQkq -",
                "f6d5");

        // 1.e4 c5 2.c3 (Alapin)
        add("rnbqkbnr/pp1ppppp/8/2p5/4P3/2P5/PP1P1PPP/RNBQKBNR b KQkq -",
                "g8f6", "d7d5");

        // ===========================================================
        //  French deeper
        // ===========================================================

        // 1.e4 e6 2.d4 d5
        add("rnbqkbnr/ppp2ppp/4p3/3p4/3PP3/8/PPP2PPP/RNBQKBNR w KQkq d6",
                "b1c3", "e4e5");
        // 1.e4 e6 2.d4 d5 3.Nc3
        add("rnbqkbnr/ppp2ppp/4p3/3p4/3PP3/2N5/PPP2PPP/R1BQKBNR b KQkq -",
                "g8f6", "f8b4");
        // 1.e4 e6 2.d4 d5 3.e5 (Advance)
        add("rnbqkbnr/ppp2ppp/4p3/3pP3/3P4/8/PPP2PPP/RNBQKBNR b KQkq -",
                "c7c5");

        // ===========================================================
        //  Caro-Kann deeper
        // ===========================================================

        // 1.e4 c6 2.d4 d5
        add("rnbqkbnr/pp2pppp/2p5/3p4/3PP3/8/PPP2PPP/RNBQKBNR w KQkq d6",
                "b1c3", "e4e5", "e4d5");
        // 1.e4 c6 2.d4 d5 3.Nc3 dxe4 4.Nxe4 (Classical)
        add("rnbqkbnr/pp2pppp/2p5/8/3PN3/8/PPP2PPP/R1BQKBNR b KQkq -",
                "c8f5", "b8d7");
        // 1.e4 c6 2.d4 d5 3.e5 (Advance)
        add("rnbqkbnr/pp2pppp/2p5/3pP3/3P4/8/PPP2PPP/RNBQKBNR b KQkq -",
                "c8f5");
        // 1.e4 c6 2.d4 d5 3.exd5 (Exchange)
        add("rnbqkbnr/pp2pppp/2p5/3P4/3P4/8/PPP2PPP/RNBQKBNR b KQkq -",
                "c6d5");

        // ===========================================================
        //  QGD / Slav deeper
        // ===========================================================

        // 1.d4 d5 2.c4 e6 3.Nc3
        add("rnbqkbnr/ppp2ppp/4p3/3p4/2PP4/2N5/PP2PPPP/R1BQKBNR b KQkq -",
                "g8f6");
        // 1.d4 d5 2.c4 e6 3.Nc3 Nf6
        add("rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/2N5/PP2PPPP/R1BQKBNR w KQkq -",
                "c1g5", "g1f3");

        // 1.d4 d5 2.c4 c6 (Slav)
        add("rnbqkbnr/pp2pppp/2p5/3p4/2PP4/8/PP2PPPP/RNBQKBNR w KQkq -",
                "g1f3", "b1c3");
        // 1.d4 d5 2.c4 c6 3.Nf3 Nf6 4.Nc3
        add("rnbqkb1r/pp2pppp/2p2n2/3p4/2PP4/2N2N2/PP2PPPP/R1BQKB1R b KQkq -",
                "d5c4", "e7e6");

        // ===========================================================
        //  Indian defences deeper
        // ===========================================================

        // 1.d4 Nf6 2.c4 e6 3.Nc3
        add("rnbqkb1r/pppp1ppp/4pn2/8/2PP4/2N5/PP2PPPP/R1BQKBNR b KQkq -",
                "f8b4");
        // 1.d4 Nf6 2.c4 e6 3.Nc3 Bb4 (Nimzo-Indian)
        add("rnbqk2r/pppp1ppp/4pn2/8/1bPP4/2N5/PP2PPPP/R1BQKBNR w KQkq -",
                "a2a3", "e2e3", "d1c2");
        // 1.d4 Nf6 2.c4 e6 3.Nf3
        add("rnbqkb1r/pppp1ppp/4pn2/8/2PP4/5N2/PP2PPPP/RNBQKB1R b KQkq -",
                "b7b6", "f8b4");

        // 1.d4 Nf6 2.c4 g6 (King's Indian setup)
        add("rnbqkb1r/pppppp1p/5np1/8/2PP4/8/PP2PPPP/RNBQKBNR w KQkq -",
                "b1c3", "g1f3");
        // 1.d4 Nf6 2.c4 g6 3.Nc3
        add("rnbqkb1r/pppppp1p/5np1/8/2PP4/2N5/PP2PPPP/R1BQKBNR b KQkq -",
                "f8g7");
        // 1.d4 Nf6 2.c4 g6 3.Nc3 Bg7
        add("rnbqk2r/ppppppbp/5np1/8/2PP4/2N5/PP2PPPP/R1BQKBNR w KQkq -",
                "e2e4");
        // 1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4
        add("rnbqk2r/ppppppbp/5np1/8/2PPP3/2N5/PP3PPP/R1BQKBNR b KQkq e3",
                "d7d6");
        // 1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 (Classical King's Indian)
        add("rnbqk2r/ppp1ppbp/3p1np1/8/2PPP3/2N5/PP3PPP/R1BQKBNR w KQkq -",
                "g1f3");

        // ===========================================================
        //  London System
        // ===========================================================

        // 1.d4 d5 2.Nf3
        add("rnbqkbnr/ppp1pppp/8/3p4/3P4/5N2/PPP1PPPP/RNBQKB1R b KQkq -",
                "g8f6");
        // 1.d4 d5 2.Nf3 Nf6
        add("rnbqkb1r/ppp1pppp/5n2/3p4/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq -",
                "c1f4", "c2c4");
        // 1.d4 d5 2.Nf3 Nf6 3.Bf4 (London)
        add("rnbqkb1r/ppp1pppp/5n2/3p4/3P1B2/5N2/PPP1PPPP/RN1QKB1R b KQkq -",
                "c7c5", "e7e6");

        // ===========================================================
        //  English deeper
        // ===========================================================

        // 1.c4 e5
        add("rnbqkbnr/pppp1ppp/8/4p3/2P5/8/PP1PPPPP/RNBQKBNR w KQkq e6",
                "b1c3", "g1f3");
        // 1.c4 Nf6
        add("rnbqkb1r/pppppppp/5n2/8/2P5/8/PP1PPPPP/RNBQKBNR w KQkq -",
                "b1c3", "d2d4");
        // 1.c4 c5 (Symmetrical)
        add("rnbqkbnr/pp1ppppp/8/2p5/2P5/8/PP1PPPPP/RNBQKBNR w KQkq c6",
                "g1f3", "b1c3");

        // ===========================================================
        //  Reti / Nf3 systems
        // ===========================================================

        // 1.Nf3 d5
        add("rnbqkbnr/ppp1pppp/8/3p4/8/5N2/PPPPPPPP/RNBQKB1R w KQkq d6",
                "c2c4", "d2d4", "g2g3");
        // 1.Nf3 Nf6
        add("rnbqkb1r/pppppppp/5n2/8/8/5N2/PPPPPPPP/RNBQKB1R w KQkq -",
                "c2c4", "d2d4");
    }

    private static void add(String positionKey, String... moves) {
        BOOK.put(positionKey, new ArrayList<>(Arrays.asList(moves)));
    }

    public static Move findBookMove(GameManager gameManager) {
        String key = positionKey(gameManager);
        List<String> moves = BOOK.get(key);
        if (moves == null || moves.isEmpty()) {
            return null;
        }

        // shuffle preferred order so the bot has variety across games
        List<String> shuffled = new ArrayList<>(moves);
        java.util.Collections.shuffle(shuffled, RANDOM);

        for (String uci : shuffled) {
            Move legal = resolveLegalMove(gameManager, uci);
            if (legal != null) {
                return legal;
            }
        }
        return null;
    }

    private static String positionKey(GameManager gm) {
        // strip the halfmove and fullmove counters so the key matches across move orders
        String fen = FenParser.toFen(gm);
        String[] parts = fen.split(" ");
        return parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3];
    }

    private static Move resolveLegalMove(GameManager gm, String uci) {
        if (uci == null || uci.length() < 4) {
            return null;
        }
        int fromCol = uci.charAt(0) - 'a';
        int fromRow = 8 - Character.getNumericValue(uci.charAt(1));
        int toCol = uci.charAt(2) - 'a';
        int toRow = 8 - Character.getNumericValue(uci.charAt(3));

        Square from = new Square(fromRow, fromCol);
        Piece piece = gm.getBoard().getPiece(from);
        if (piece == null || piece.getColor() != gm.getCurrentTurn()) {
            return null;
        }

        for (Move m : gm.getLegalMoves(from)) {
            if (m.getEnd().getRow() == toRow && m.getEnd().getCol() == toCol) {
                return m;
            }
        }
        return null;
    }
}
