package odin.backbone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.net.URLEncoder;

public class OpenLink extends AppCompatActivity {

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    SimpleDraweeView img;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("post");
    TextView title,description,date,time;
    String postRef;
    PostFace postFace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_link);
        img = findViewById(R.id.img);
        title = findViewById(R.id.tit);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        description = findViewById(R.id.desc);

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if (deepLink != null) {
                            postRef = deepLink.toString().substring(25);
                            postRef = postRef.replace("/","");
                            setContent(postRef);

                        } else {
                        }
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });


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

    void setContent(String arg)
    {
        addViews();
        getCount();

        databaseReference.child(arg).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postFace = dataSnapshot.getValue(PostFace.class);
                title.setText(postFace.post_title);
                date.setText(postFace.post_date);
                setImage(postFace.post_photo,img);
                description.setText(postFace.post_description);
                time.setText(postFace.post_time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_link, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            shareLongDynamicLink();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void shareLongDynamicLink() {
        Intent intent = new Intent();
        String msg =  buildDynamicLink();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        intent.setType("text/plain");
        startActivity(intent);
    }

    private String buildDynamicLink() {

        return "https://backboneblog.page.link/?link=https://backboneblog.com/"+postRef+"/"+
                "&apn=" +getPackageName()+
                "&st=" + /*titleSocial*/
                URLEncoder.encode(title.getText().toString()) +
                "&utm_source=" + /*source*/
                "AndroidApp";

    }





        public void addViews()
        {
            WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            final String address = info.getMacAddress();
            databaseReference.child(postRef)
                    .child("views")
                    .child(address).setValue(address);

        }



    public void getCount()
    {

        databaseReference.child(postRef).child("views").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.child(postRef).child("post_views").setValue(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
