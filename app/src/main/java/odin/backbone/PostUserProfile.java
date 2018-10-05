package odin.backbone;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostUserProfile extends AppCompatActivity {

    SimpleDraweeView uPhoto;
    EditText uName,uBio;
    Intent intent;
    String photo,uUid;
    Button userPost;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        uPhoto = findViewById(R.id.uPhoto);
        uName = findViewById(R.id.uName);
        uBio = findViewById(R.id.uBio);
        userPost = findViewById(R.id.user_post);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setRoundAsCircle(true);
        uPhoto.getHierarchy().setRoundingParams(roundingParams);
        intent = getIntent();
        photo = intent.getStringExtra("uPhoto");
        uUid = intent.getStringExtra("uUid");
        uName.setText(intent.getStringExtra("uName"));
        uBio.setText(intent.getStringExtra("uBio"));
        setImage(intent.getStringExtra("uPhoto"),uPhoto);
        uPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostUserProfile.this,FullScreenImage.class);
                intent.putExtra("name",uName.getText().toString());
                intent.putExtra("photo",photo);
                startActivity(intent);
            }
        });

        userPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUserPost();
            }
        });

        if(uName.getText().toString().equals("Anonymous"))
        {
            userPost.setVisibility(View.GONE);
        }

    }

    public void setImage(final String pars, final SimpleDraweeView simpleDraweeView)
    {

        final StorageReference ref = storageReference.child(pars);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                simpleDraweeView.setImageURI(uri);

            }
        });

    }


    void goToUserPost()
    {
         Intent goToUserPost = new Intent(PostUserProfile.this,UserPost.class);
         goToUserPost.putExtra("userName",uName.getText().toString());
         goToUserPost.putExtra("userUid",uUid);
         startActivity(goToUserPost);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
