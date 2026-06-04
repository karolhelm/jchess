
package jchess.config;
import java.util.Map;
public class PiecesConfig {
    private String imageBasePath;
    private Map<String, String> images; //mapping  pieces to images
    public String getImageBasePath() { return imageBasePath; }
    public void setImageBasePath(String imageBasePath) { this.imageBasePath = imageBasePath; }
    public Map<String, String> getImages() { return images; }
    public void setImages(Map<String, String> images) { this.images = images; }
}