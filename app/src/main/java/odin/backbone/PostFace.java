package odin.backbone;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class PostFace {

    public String annonymously;
    public String post_date;
    public String post_description;
    public String post_photo;
    public String post_time;
    public String post_title;
    public String post_views;
    public String privacy;
    public String uid;
    public String writter_name;
    public String writter_photo;
    public String writter_bio;

    public PostFace() {

    }


    PostFace(String annonymously, String post_date, String post_description, String post_photo,String post_time, String post_title, String post_views, String privacy, String uid, String writter_name, String writter_photo,String writter_bio) {
        this.annonymously = annonymously;
        this.post_date = post_date;
        this.post_description = post_description;
        this.post_photo = post_photo;
        this.post_time = post_time;
        this.post_title = post_title;
        this.post_views = post_views;
        this.privacy = privacy;
        this.uid = uid;
        this.writter_name = writter_name;
        this.writter_photo = writter_photo;
        this.writter_bio = writter_bio;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("annonymously",annonymously);
        result.put("post_date",post_date);
        result.put("post_description",post_description);
        result.put("post_photo",post_photo);
        result.put("post_time",post_time);
        result.put("post_title",post_title);
        result.put("post_views",post_views);
        result.put("privacy",privacy);
        result.put("uid",uid);
        result.put("writter_name",writter_name);
        result.put("writter_photo",writter_photo);
        result.put("writter_bio",writter_bio);
        return result;
    }
}
