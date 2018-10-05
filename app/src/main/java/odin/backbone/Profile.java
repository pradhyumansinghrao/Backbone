package odin.backbone;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import static com.firebase.ui.auth.AuthUI.TAG;

public class Profile extends Fragment{

    Button submit,cancel,editProfile;
    TextView writterName,writterBio;
    SimpleDraweeView writterImage;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    ProgressBar pbar;
    UserFace userFace;
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
    public Profile() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        writterImage = view.findViewById(R.id.writter_image);
        writterName = view.findViewById(R.id.name);
        writterBio = view.findViewById(R.id.bio);
        editProfile = view.findViewById(R.id.edit_profile);
        pbar = view.findViewById(R.id.progressBar1);
        pbar.setVisibility(View.GONE);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setRoundAsCircle(true);
        writterImage.getHierarchy().setRoundingParams(roundingParams);
        setUserProfile();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),EditProfile.class);
                startActivity(intent);
            }
        });

        writterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),FullScreenImage.class);
                intent.putExtra("name",writterName.getText().toString());
                intent.putExtra("photo",userFace.photo);
                startActivity(intent);
            }
        });
        return view;
    }

    public static Profile newInstance() {
        Profile profile = new Profile();
        return profile;
    }


    public void setUserProfile()
    {
        final boolean[] con = {true};
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    userFace = dataSnapshot.getValue(UserFace.class);
                    writterName.setText(userFace.name);
                    writterBio.setText(userFace.bio);
                    storageReference.child(userFace.photo).getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    writterImage.setImageURI(uri);
                                    con[0] = false;
                                }
                            });
                    if (con[0]) {
                        writterImage.setImageURI(userFace.photo);
                    }
                }
                else
                {
                    FirebaseAuth.getInstance().signOut();
                    getActivity().finish();
                    startActivity(new Intent(getActivity(),Login.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        };
        firebaseDatabase.addValueEventListener(listener);

    }


}
