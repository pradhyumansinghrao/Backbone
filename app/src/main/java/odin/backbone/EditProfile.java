package odin.backbone;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

import static com.firebase.ui.auth.AuthUI.TAG;

public class EditProfile extends AppCompatActivity {

    EditText writterName, writterBio;
    SimpleDraweeView writterImage;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Uri filePath;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    ProgressBar pbar;
    UserFace userFace;
    private Uri imageFileUri;
    boolean firstTimeLogin = false;
    private DatabaseReference post = FirebaseDatabase.getInstance().getReference().child("post");
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        writterImage = findViewById(R.id.writter_image);
        writterName = findViewById(R.id.name);
        writterBio = findViewById(R.id.bio);
        pbar = findViewById(R.id.progressBar1);
        pbar.setVisibility(View.GONE);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setRoundAsCircle(true);
        writterImage.getHierarchy().setRoundingParams(roundingParams);
        setUserProfile();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImage();
            }
        });
        firstTimeLogin = getIntent().getBooleanExtra("firstTimeLogin",false);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit:
                if(Write.isEditTextEmpty(writterName)) {
                    Write.showAlert("Please fill your name",EditProfile.this);
                }
                else if(Write.isEditTextEmpty(writterBio))
                {
                    Write.showAlert("Please fill your bio",EditProfile.this);
                }
                else
                {
                    if (firstTimeLogin) {
                        changeName();
                        chageBio();
                        startActivity(new Intent(EditProfile.this, Main.class));
                        finish();
                    } else {
                        changeName();
                        chageBio();
                        finish();
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void takeImage() {
        String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
        File imageFile = new File(imageFilePath);
        imageFileUri = Uri.fromFile(imageFile);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        Intent chooser = Intent.createChooser(galleryIntent, "Select Source");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        startActivityForResult(chooser, PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setRoundAsCircle(true);
        writterImage.getHierarchy().setRoundingParams(roundingParams);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    .start(this);

        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri uri = imageFileUri;
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    .start(this);

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                writterImage.setImageURI(filePath);
                if (filePath != null) {
                    uploadImage();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.d("ImageNotGet","ImageNotGet");
            }
        }
    }

    public void setUserProfile() {
        final boolean[] con = {true};
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userFace = dataSnapshot.getValue(UserFace.class);
                writterName.setText(""+userFace.name);
                writterBio.setText(""+userFace.bio);
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

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        };
        firebaseDatabase.addListenerForSingleValueEvent(listener);

    }

    private void uploadImage() {

        Toasty.Config.getInstance().setSuccessColor(getResources().getColor(R.color.colorPrimaryDark)).apply();

        if (filePath != null) {
//            storageReference.child(ImageUrl).delete();
            final StorageReference ref = storageReference.child("profile/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            firebaseDatabase.child("photo").setValue("" + ref.getPath());
                            post.orderByChild("uid").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        dataSnapshot1.getRef().orderByValue().equalTo("false")
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            post.child(dataSnapshot1.getKey())
                                                                    .child("writter_photo")
                                                                    .setValue("" + ref.getPath());
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            pbar.setVisibility(View.INVISIBLE);
                            Toasty.success(getApplicationContext(), "Profile Photo Updated", Toast.LENGTH_LONG, true).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(getApplicationContext(), "Failed", Toast.LENGTH_LONG, true).show();
                            Toasty.error(getApplicationContext(), "Failed", Toast.LENGTH_LONG, true).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            pbar.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }


    public void changeName() {

        firebaseDatabase.child("name").setValue(writterName.getText().toString());
        post.orderByChild("uid").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    dataSnapshot1.getRef().orderByValue().equalTo("false")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        post.child(dataSnapshot1.getKey())
                                                .child("writter_name")
                                                .setValue(writterName.getText().toString());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void chageBio() {
        firebaseDatabase.child("bio").setValue(writterBio.getText().toString());
        post.orderByChild("uid").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    dataSnapshot1.getRef().orderByValue().equalTo("false")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        post.child(dataSnapshot1.getKey())
                                                .child("writter_bio")
                                                .setValue(writterBio.getText().toString());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
