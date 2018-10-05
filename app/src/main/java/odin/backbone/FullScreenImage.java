package odin.backbone;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FullScreenImage extends AppCompatActivity {

    SimpleDraweeView showImage;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        showImage = findViewById(R.id.showImage);
        setTitle(getIntent().getStringExtra("name"));
        setImage(getIntent().getStringExtra("photo"),showImage);
    }

    public void setImage(final String pars, final SimpleDraweeView simpleDraweeView) {

        final StorageReference ref = storageReference.child(pars);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                simpleDraweeView.setImageURI(uri);

            }
        });
    }
}
