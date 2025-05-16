package ny.rina.util;

import java.io.File;
import java.util.List;
import java.util.Map;

import javafx.scene.media.AudioTrack;
import javafx.scene.media.Media;
import javafx.scene.media.Track;
import javafx.scene.media.VideoTrack;
import javafx.stage.FileChooser;

public class MediaUtil {
    Media media;
    String  audioName;
    String audioEncoding;
    boolean audioEnabled;
    String  videoName;
    String videoEncoding;
    boolean videoEnabled;


    public String getVideoName(){
        return videoName;
    }

    List<Track> tracks;
    public List<Track> getTracks(){
        return tracks;
    }

    public MediaUtil(Media media){
        this.media = media;
        tracks = media.getTracks();
        setMetadata();
    };

    public boolean hasVideo(){
        return tracks.stream().anyMatch(track -> track instanceof VideoTrack);
    }

    private void setMetadata(){
        Map<String, Object> metadata;
        for (Track track : tracks) {
            metadata = track.getMetadata();
            for (Map.Entry<String, Object> entry : metadata.entrySet()){
                switch (entry.getKey()) {
                    case "name":
                        if (track instanceof VideoTrack)
                            videoName = String.valueOf(entry.getValue());   
                        else if (track instanceof AudioTrack)
                            audioName = String.valueOf(entry.getValue());   
                        break;
                    case "encoding":
                        if (track instanceof VideoTrack)
                            videoEncoding = String.valueOf(entry.getValue());        
                        else if (track instanceof AudioTrack)
                            audioEncoding = String.valueOf(entry.getValue());           
                        break;
                    case "enabled":
                        String enabledValue = String.valueOf(entry.getValue());
                        if (track instanceof VideoTrack) 
                            videoEnabled = Boolean.valueOf(enabledValue); 
                        else if (track instanceof AudioTrack)
                            audioEnabled = Boolean.valueOf(enabledValue); 
                        break;
                
                    default:
                        break;
                }
            } 
        }
    }
    

    public boolean isEnabled(){
        if (this.hasVideo()) return videoEnabled && audioEnabled;
        
        return audioEnabled;
    }

    public static File selectFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select media");

        return fileChooser.showOpenDialog(null);
    }
}
