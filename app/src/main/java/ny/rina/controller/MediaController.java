package ny.rina.controller;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import ny.rina.util.MediaUtil;
import ny.rina.util.VideoUtil;

public class MediaController {
    Media media;
    MediaPlayer mediaPlayer;

    @FXML
    Button playButton;

    @FXML
    void play(ActionEvent event){
        if (mediaPlayer != null) {
            Status status = mediaPlayer.getStatus();
            switch (status) {
                case READY:
                case PAUSED:
                case STOPPED:
                    mediaPlayer.play();
                    playButton.setText("Pause");
                    break;
                case PLAYING:
                    mediaPlayer.pause();
                    playButton.setText("Play");
                    break;
                default:
                    break;
            }
        }
    }

    @FXML
    Button stopButton;
    
    @FXML
    void stop(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            playButton.setText("Play");
        }
    }

    @FXML
    Button selecButton;

    @FXML
    void select(ActionEvent event){
        File selectedFile = selectFile();
        if (selectedFile != null) {
            String url = selectedFile.toURI().toString();
            setMediaOnMediaPlayer(url);
        }
    }

    @FXML
    MediaView mediaView;

    File selectFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select media");
        return fileChooser.showOpenDialog(null);
    }

    void setMediaOnMediaPlayer(String url){
        if (mediaPlayer != null) resetMediaPlayer();

        media = new Media(url);            
        mediaPlayer = new MediaPlayer(media);
        
        mediaPlayer.setOnReady(() -> {
            handleMediaPlayer();
        });

        mediaView.setMediaPlayer(mediaPlayer);
    }

    void resetMediaPlayer(){
        mediaPlayer.stop();
        playButton.setText("Play");
    }

    void handleMediaPlayer(){
        MediaUtil mediaUtil = new MediaUtil(media);
        VideoUtil videoUtil = new VideoUtil(mediaView);

        if (mediaUtil.hasVideo()) 
            videoUtil.fitSizeProperty();
        else 
            videoUtil.unbindSizeProperty();
    }
}