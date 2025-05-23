package ny.rina.controller;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import ny.rina.util.MediaUtil;
import ny.rina.util.VideoUtil;
import ny.rina.util.Util;

public class MediaController {
    Media media;
    MediaPlayer mediaPlayer;
    ContextMenu contextMenu;
    MenuItem playItem;
    MenuItem stopItem;
    MenuItem deleteItem;

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
    ListView<Media> listView;

    @FXML
    public void initialize(){
        playItem = new MenuItem("Play");
        stopItem = new MenuItem("Stop");
        deleteItem = new MenuItem("Delete");

        playItem.setOnAction(e -> {
            playItemAction(e);
        });

        stopItem.setOnAction(e -> {
            stop();
        });

        deleteItem.setOnAction(e -> {
            deleteItemAction();
        });
    }

    @FXML
    void increaseVolume(){
        double volume = mediaPlayer.getVolume();
        if (volume < 1) mediaPlayer.setVolume(volume + 0.1);
        refreshVolumeProgress();
    }

    @FXML
    void decreaseVolume(){
        double volume = mediaPlayer.getVolume();
        if (volume >= 0.1) {
            if((volume - 0.1) < 0.1) mediaPlayer.setVolume(0.0);
            else  mediaPlayer.setVolume(volume - 0.1);
        }
        refreshVolumeProgress();
    }
    @FXML
    void IncreaseOrDecreaseVlume(ScrollEvent event){
        if(event.getDeltaY() != 0){
            if (event.getDeltaY() > 0) increaseVolume();
            else decreaseVolume();
        }
       
    }

    @FXML
    void mute(){
        if(mediaPlayer.getVolume() == 0){
            mediaPlayer.setVolume(1.0);
        } else {
            mediaPlayer.setVolume(0.0);
        }

        refreshVolumeProgress();
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
                    playItem.setText("Pause");
                    break;
                case PLAYING:
                    mediaPlayer.pause();
                    playButton.setText("Play");
                    playItem.setText("Play");
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

    

    @FXML
    void select(ActionEvent event){
        File selectedFile = MediaUtil.selectFile();
        if (selectedFile != null) {
            String url = selectedFile.toURI().toString();
            setMediaOnMediaPlayer(url);
        }
    }

    @FXML
    void progressBarPressed(MouseEvent event){
        double mouseX = event.getX();
        double width = volumeProgress.getWidth();
        double percent = mouseX / width;

        mediaPlayer.setVolume(percent);
        refreshVolumeProgress();
    }

    @FXML
    void sliderPressed(MouseEvent event){
        mediaPlayer.seek(Duration.seconds(slider.getValue()));
    }

    @FXML
    void onMouseClicked(MouseEvent event){
        destroyContextMenu();
        Media media = listView.getSelectionModel().getSelectedItem();
        if (media != null) {
            if (event.getClickCount() == 2)
                setMediaOnMediaPlayer(media.getSource());
            if (event.getButton() == MouseButton.SECONDARY)
                showContextMenu(event.getScreenX(), event.getScreenY(), media);
        }
            
    }

    void destroyContextMenu(){
        if (contextMenu != null) {
            contextMenu.getItems().clear(); 
            contextMenu.hide();            
            contextMenu = null;  
        }
    }

    void showContextMenu(Double x_pos, Double y_pos, Media media){
        contextMenu = new ContextMenu();
        if (selectedMediaIsRunning()) {
            Status status = mediaPlayer.getStatus(); 
            switch (status) {
                case READY:
                case PAUSED:
                case STOPPED:
                    playItem.setText("Play");
                    break;
                case PLAYING:
                    playItem.setText("Pause");
                    break;
                default:
                    break;
            }
            stopItem.setDisable(false);
        } else {
            playItem.setText("Play");
            stopItem.setDisable(true);
        }

        contextMenu.getItems().addAll(playItem, stopItem, deleteItem);
        contextMenu.show(listView, x_pos, y_pos);
    }

    void deleteItemAction(){
        if (selectedMediaIsRunning()) {
            stop();
            mediaView.setMediaPlayer(null);
        }

        listView.getItems().remove(getSelectedMedia());
    }

    Media getSelectedMedia(){
        return listView.getSelectionModel().getSelectedItem();
    }

    void playItemAction(ActionEvent e){
        if (!selectedMediaIsRunning()){
            resetMediaPlayer();
            mediaPlayer = new MediaPlayer(getSelectedMedia());
            setUpMediaPlayer();
            mediaView.setMediaPlayer(mediaPlayer);
        }

        play(e);
    }

    boolean selectedMediaIsRunning(){
        if (mediaPlayer == null) return false;
        Media selelectMedia = listView.getSelectionModel().getSelectedItem();

        return mediaPlayer.getMedia().equals(selelectMedia);
    }

    void setMediaOnMediaPlayer(String url){
        resetMediaPlayer();

        if (!url.isEmpty()){
            media = new Media(url);   
            mediaPlayer = new MediaPlayer(media);       
        
            customListView();
            setUpMediaPlayer();           
            mediaView.setMediaPlayer(mediaPlayer);
        }
    }

    void customListView(){
        boolean mediaExists = listView.getItems().stream().anyMatch(m -> m.getSource().equals(media.getSource()));
        if (!mediaExists){
            listView.getItems().add(media);
            listView.setCellFactory(p->{
                return new ListCell<>(){
                    @Override   
                    protected void updateItem(Media media, boolean empty){
                        super.updateItem(media, empty);

                        if (media == null || empty){
                            setGraphic(null);
                        } else {
                            Path path = Paths.get(URI.create(media.getSource()));
                            Label fileName = new Label(path.getFileName().toString());
                            setGraphic(fileName);
                        }
                    }
                };
            });
        }
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

        mediaPlayerAutoPlay();
    }

    void mediaPlayerAutoPlay(){
        mediaPlayer.setAutoPlay(true);
        playButton.setText("Pause");
    }

    void mediaPlayerOnReady(){
        handleMediaPlayer();
        initilizeSlider();
        refreshVolumeProgress();
    }

    void refreshVolumeProgress(){
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
            totalDurationHoursLabel.setText(Util.formatValueDuration(totalDuration / 3600));
            totalDuration = totalDuration % 3600;
        }

        if (totalDuration >= 60) {
            totalDurationMinutesLabel.setText(Util.formatValueDuration(totalDuration / 60));
            totalDuration = totalDuration % 60;
        }

        totalDurationSecondsLabel.setText(Util.formatValueDuration(totalDuration));
    }

    void setCurrentDuration(Double newTime){
        if (newTime >= 3600) {
            currentDurationHoursLabel.setText(Util.formatValueDuration(newTime / 3600));
            newTime = newTime % 3600;
        }

        if (newTime >= 60) {
            currentDurationMinutesLabel.setText(Util.formatValueDuration(newTime / 60));
            newTime = newTime % 60;
        }

        currentDurationSecondsLabel.setText(Util.formatValueDuration(newTime));
    }

    void resetMediaPlayer(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer = null;
            playButton.setText("Play");
        }
    }

    /**
     * Fit size of media view if media is a video
     */
    void handleMediaPlayer(){
        MediaUtil mediaUtil = new MediaUtil(media);
        VideoUtil videoUtil = new VideoUtil(mediaView);

        if (mediaUtil.hasVideo()) 
            videoUtil.fitSizeProperty();
        else 
            videoUtil.unbindSizeProperty();
    }

    void resetCurrentDurationLabel(){
        currentDurationHoursLabel.setText("00");
        currentDurationMinutesLabel.setText("00");
        currentDurationSecondsLabel.setText("00");
    }
}