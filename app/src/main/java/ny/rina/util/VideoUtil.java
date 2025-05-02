package ny.rina.util;

import javafx.scene.Scene;
import javafx.scene.media.MediaView;

public class VideoUtil {
    Scene scene;
    MediaView mediaView;
    public VideoUtil(MediaView mediaView){
        this.mediaView = mediaView;
        scene = mediaView.getScene();
    }

    public void fitSizeProperty(){
        mediaView.fitHeightProperty().bind(scene.heightProperty());
        mediaView.fitWidthProperty().bind(scene.widthProperty());
    }

    public void unbindSizeProperty() {
        mediaView.fitHeightProperty().unbind();
        mediaView.fitWidthProperty().unbind();
        mediaView.setFitWidth(200);
        mediaView.setFitHeight(200);
    }
}
