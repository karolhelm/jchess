package jchess.config;
public class BoardTheme {
    private String id; //info about one board theme
    private String displayName;
    private String lightSquare;
    private String darkSquare;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getLightSquare() { return lightSquare; }
    public void setLightSquare(String lightSquare) { this.lightSquare = lightSquare; }
    public String getDarkSquare() { return darkSquare; }
    public void setDarkSquare(String darkSquare) { this.darkSquare = darkSquare; }
}