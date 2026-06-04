package jchess.config;
import jchess.model.Piece;
import java.util.List;//it sets all kind of information taken from json so chessapp doesnt have to
public class AppConfig {
    private PiecesConfig pieces; //map (figure type->png
    private List<BoardTheme> boardThemes; //board motives and one default
    private String defaultBoardThemeId;
    private UiConfig ui; //colors of everything in app so it isn't hardcoded
    public void setPieces(PiecesConfig pieces) { this.pieces = pieces; }
    public List<BoardTheme> getBoardThemes() { return boardThemes; }
    public void setBoardThemes(List<BoardTheme> boardThemes) { this.boardThemes = boardThemes; }
    public String getDefaultBoardThemeId() { return defaultBoardThemeId; }
    public void setDefaultBoardThemeId(String defaultBoardThemeId) { this.defaultBoardThemeId = defaultBoardThemeId; }
    public UiConfig getUi() { return ui; }
    public void setUi(UiConfig ui) { this.ui = ui; }
    public BoardTheme getThemeById(String id) {
        for(BoardTheme t : boardThemes) //look for motive chosen by player
            if(t.getId().equals(id)) return t;

        return boardThemes.getFirst();
    }       //gets path to file
    public String getPieceImagePath(Piece piece) {
        String key = piece.getColor().name().toLowerCase() + "_" + piece.getType().getPieceName();
        String file = pieces.getImages().get(key);
        return pieces.getImageBasePath() + file;
    }
}