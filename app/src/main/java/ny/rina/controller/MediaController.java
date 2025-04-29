package ny.rina.controller;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import ny.rina.util.VideoUtil;

public class MediaController {
    Media media;
    MediaPlayer mediaPlayer;

    @FXML
    Button playButton;

    @FXML
    void play(){

    }

    @FXML
    Button stopButton;
    
    @FXML
    void stop(){

    }

    @FXML
    Button selecButton;

    @FXML
    void select(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select media");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String url = selectedFile.toURI().toString();
            media = new Media(url);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            VideoUtil videoUtil = new VideoUtil(mediaView);
            videoUtil.fitSizeProperty();

            mediaPlayer.setAutoPlay(false);
        }
    }

    @FXML
    MediaView mediaView;

}