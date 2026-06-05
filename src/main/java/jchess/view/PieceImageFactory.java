package jchess.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jchess.config.AppConfig;
import jchess.model.Piece;

import java.net.URL;

public class PieceImageFactory {
    private final AppConfig appConfig;
    private final Class<?> resourceRoot;
    private final int imageSize;

    public PieceImageFactory(AppConfig appConfig, Class<?> resourceRoot, int imageSize) {
        this.appConfig = appConfig;
        this.resourceRoot = resourceRoot;
        this.imageSize = imageSize;
    }

    public ImageView create(Piece piece) {
        String path = appConfig.getPieceImagePath(piece);
        if (path == null) {
            return null;
        }

        URL imageUrl = resourceRoot.getResource(path);
        if (imageUrl == null) {
            return null;
        }

        Image image = new Image(imageUrl.toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(imageSize);
        imageView.setFitHeight(imageSize);
        imageView.setPreserveRatio(true);
        return imageView;
    }
}
