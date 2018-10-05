package odin.backbone;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserFace {

    public String email;
    public String name;
    public String photo;
    public String phone;
    public String bio;
    public UserFace() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    UserFace(String name, String email,String photo,String phone,String bio) {
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.phone = phone;
        this.bio = bio;
    }





}
