package jchess.config;
public class UiConfig {
    private String background;
    private String accent;
    private String highlight;
    private String coordinate;
    private String buttonBackground;                //jackson sets info and then app config gets it
    private String textPrimary;
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    public String getAccent() { return accent; }
    public void setAccent(String accent) { this.accent = accent; }
    public String getHighlight() { return highlight; }
    public void setHighlight(String highlight) { this.highlight = highlight; }
    public String getCoordinate() { return coordinate; }
    public void setCoordinate(String coordinate) { this.coordinate = coordinate; }
    public String getButtonBackground() { return buttonBackground; }
    public void setButtonBackground(String buttonBackground) { this.buttonBackground = buttonBackground; }
    public String getTextPrimary() { return textPrimary; }
    public void setTextPrimary(String textPrimary) { this.textPrimary = textPrimary; }
}