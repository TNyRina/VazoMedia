package ny.rina.controller;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import ny.rina.util.MediaUtil;
import ny.rina.util.VideoUtil;

public class MediaController {
    Media media;
    MediaPlayer mediaPlayer;

    @FXML
    MediaView mediaView;

    @FXML 
    Slider slider;

    @FXML
    Button playButton;


    @FXML
    Button stopButton;


    @FXML
    Button selecButton;

    @FXML
    Button muteButton;

    @FXML
    Label totalDurationHoursLabel;

    @FXML
    Label totalDurationMinutesLabel;

    @FXML
    Label totalDurationSecondsLabel;

    @FXML
    Label currentDurationHoursLabel;

    @FXML
    Label currentDurationMinutesLabel;

    @FXML
    Label currentDurationSecondsLabel;

    @FXML
    ProgressBar volumeProgress;

    @FXML
    Button increaseVolumeButton;

    @FXML
    Button decreaseVolumeButton;

    @FXML
    void increaseVolume(){
        double volume = mediaPlayer.getVolume();
        if (volume < 1) mediaPlayer.setVolume(volume + 0.1);
        handleVolume();
    }

    @FXML
    void decreaseVolume(){
        double volume = mediaPlayer.getVolume();
        if (volume >= 0.1) mediaPlayer.setVolume(volume - 0.1);
        handleVolume();
    }

    @FXML
    void mute(){
        if(mediaPlayer.getVolume() == 0){
            mediaPlayer.setVolume(1);
        } else {
            mediaPlayer.setVolume(0);
        }

        handleVolume();
    }

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
    void stop(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            playButton.setText("Play");
            resetCurrentDurationLabel();
        }
    }

    void resetCurrentDurationLabel(){
        currentDurationHoursLabel.setText("00");
        currentDurationMinutesLabel.setText("00");
        currentDurationSecondsLabel.setText("00");
    }

    @FXML
    void select(ActionEvent event){
        File selectedFile = selectFile();
        if (selectedFile != null) {
            String url = selectedFile.toURI().toString();
            setMediaOnMediaPlayer(url);
        }
    }

    @FXML
    void sliderPressed(MouseEvent event){
        mediaPlayer.seek(Duration.seconds(slider.getValue()));
    }

    File selectFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select media");

        return fileChooser.showOpenDialog(null);
    }

    void setMediaOnMediaPlayer(String url){
        if (mediaPlayer != null) resetMediaPlayer();

        media = new Media(url);            
        mediaPlayer = new MediaPlayer(media);

        setUpMediaPlayer();
        mediaPlayerAutoPlay();

        mediaView.setMediaPlayer(mediaPlayer);
    }

    void setUpMediaPlayer(){
        mediaPlayer.setOnReady(() -> {
            mediaPlayerOnReady();
        });

        mediaPlayer.setOnEndOfMedia(()->{
            mediaPlayerOnEndOfMedia();
        });

        mediaPlayer.currentTimeProperty().addListener(((obs, oldTime, newTime) -> {
            slider.setValue(newTime.toSeconds());
            resetCurrentDurationLabel();
            setCurrentDuration(newTime.toSeconds());
        }));
    }

    void mediaPlayerAutoPlay(){
        mediaPlayer.setAutoPlay(true);
        playButton.setText("Pause");
    }

    void mediaPlayerOnReady(){
        handleMediaPlayer();
        initilizeSlider();
        handleVolume();
    }

    void handleVolume(){
        volumeProgress.setProgress(mediaPlayer.getVolume());
    }

    void mediaPlayerOnEndOfMedia(){
        mediaPlayer.stop();
        playButton.setText("Play");
        resetCurrentDurationLabel();
    }

    void initilizeSlider(){
        Duration duration = media.getDuration();
        Double totalDuration = duration.toSeconds();
        slider.setMax(totalDuration);
        setTotalDuration(totalDuration);
    }

    void setTotalDuration(Double totalDuration){
        if (totalDuration >= 3600) {
            totalDurationHoursLabel.setText(formatValueDuration(totalDuration / 3600));
            totalDuration = totalDuration % 3600;
        }

        if (totalDuration >= 60) {
            totalDurationMinutesLabel.setText(formatValueDuration(totalDuration / 60));
            totalDuration = totalDuration % 60;
        }

        totalDurationSecondsLabel.setText(formatValueDuration(totalDuration));
    }

    void setCurrentDuration(Double newTime){
        if (newTime >= 3600) {
            currentDurationHoursLabel.setText(formatValueDuration(newTime / 3600));
            newTime = newTime % 3600;
        }

        if (newTime >= 60) {
            currentDurationMinutesLabel.setText(formatValueDuration(newTime / 60));
            newTime = newTime % 60;
        }

        currentDurationSecondsLabel.setText(formatValueDuration(newTime));
    }

    String formatValueDuration(Double value){
        return (value < 10) ? "0" + value.intValue() : String.valueOf(value.intValue());
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