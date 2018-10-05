package odin.backbone;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.firebase.ui.auth.AuthUI.TAG;

public class Write extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    SimpleDraweeView postImage;
    EditText title,description;
    PostFace postFace;
    private Uri imageFileUri;
    UserFace userFace;
    Button submit;
    RadioGroup privacy;
    CheckBox annonymously;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String wName,wPhoto,wBio,pTitle,pDes,pDate,pTime,uid,pPrivacy,pAnnonymously;
    private DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("post");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        postImage = findViewById(R.id.imagee);
        title = findViewById(R.id.tit);
        description = findViewById(R.id.des);
        privacy = findViewById(R.id.radio_group);
        submit = findViewById(R.id.sub);
        annonymously = findViewById(R.id.annoymously);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setRoundAsCircle(true);
        postImage.getHierarchy().setRoundingParams(roundingParams);
        postImage.getHierarchy().setPlaceholderImage(R.drawable.camera_post);

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkEmpty()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadPost();
                        }
                    }).start();
                }
            }
        });


    }

    void chooseImage() {
        String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
        File imageFile = new File(imageFilePath);
        imageFileUri = Uri.fromFile(imageFile);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        Intent chooser = Intent.createChooser(galleryIntent, "Select Source");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { cameraIntent });
        startActivityForResult(chooser, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            Uri uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        else if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK)
        {
            Uri uri = imageFileUri;
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                postImage.setImageURI(filePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private String uploadImage() {
        Toasty.Config.getInstance().setSuccessColor(getResources().getColor(R.color.colorPrimaryDark)).apply();

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(Write.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toasty.success(getApplicationContext(), "Post Submitted", Toast.LENGTH_LONG, true).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toasty.error(getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG, true).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int progress = (int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + progress + "%");
                            if(progress == 100)
                                finish();
                        }
                    });

            return ref.getPath();
        }

        return "";

    }

    void uploadPost()
    {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userFace = dataSnapshot.getValue(UserFace.class);
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                SimpleDateFormat tf = new SimpleDateFormat("hh:mm a");
                  wName = ""+userFace.name;
                  wPhoto = ""+userFace.photo;
                  wBio = ""+userFace.bio;
                  pTitle = ""+title.getText().toString();
                  pDes = ""+description.getText().toString();
                  pDate = ""+df.format(c);
                  pTime = ""+tf.format(c);
                  uid = user.getUid();
                  pPrivacy = ((RadioButton)findViewById(privacy.getCheckedRadioButtonId()))
                                .getText().toString();

                if (annonymously.isChecked()) {
                    pAnnonymously = "true";
                    postFace = new PostFace(pAnnonymously, pDate, pDes,uploadImage(),pTime, pTitle, "0", pPrivacy, uid, "Anonymous", "/default/anon.jpg","This is official anonymous account from backbone any one can write here");
                    firebaseDatabase.push().setValue(postFace);
                }else {
                    pAnnonymously = "false";
                    postFace = new PostFace(pAnnonymously, pDate, pDes,uploadImage(),pTime, pTitle, "0", pPrivacy, uid, wName, wPhoto,wBio);
                    firebaseDatabase.push().setValue(postFace);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        };

        profileRef.addListenerForSingleValueEvent(postListener);
    }

    static boolean isEditTextEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }


    public boolean checkEmpty()
    {
          String msg;
          if(filePath == null)
          {
               msg = "Please choose your post image";
               showAlert(msg,Write.this);
               return false;
          }
          else if(isEditTextEmpty(title))
          {
              msg = "Please fill the all text fields";
              showAlert(msg,Write.this);
              return false;
          }
          else if(isEditTextEmpty(description))
          {
              msg = "Please fill the all text fields";
              showAlert(msg,Write.this);
              return false;
          }

              return true;

    }


    public static void showAlert(String msg, Context context)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(msg).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseDatabase = null;

    }

}
