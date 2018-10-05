package odin.backbone;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.net.URLEncoder;

public class PostDet extends AppCompatActivity {
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    SimpleDraweeView img;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("post");
    TextView title,description,date,time;
    String currentUserUid = FirebaseAuth.getInstance().getUid();
    String postRef;
    boolean saveAlready;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_det);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        img = findViewById(R.id.img);
        title = findViewById(R.id.tit);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        description = findViewById(R.id.desc);
        postRef = getIntent().getStringExtra("ref");
        setContent();
        checkAlredySave();

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



    void setContent()
    {
        final Intent i = getIntent();

        addViews();
        getCount();

        title.setText(i.getStringExtra("title"));
        date.setText(i.getStringExtra("date"));

        setImage(i.getStringExtra("photo"),img);

        description.setText(i.getStringExtra("description"));
        time.setText(i.getStringExtra("time"));
    }


    public void addViews()
    {
        databaseReference.child(postRef)
                .child("views")
                .child(currentUserUid).setValue(currentUserUid);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_det, menu);
        if(saveAlready)
        {
            menu.getItem(0).setIcon(R.drawable.savefill);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            shareLongDynamicLink();
            return true;
        }
        else if(id == R.id.action_save)
        {
            if(saveAlready)
            {
                item.setIcon(R.drawable.save);
                saveAlready = false;
                removeIt();
            }
            else {
                saveAlready = true;
                item.setIcon(R.drawable.savefill);
                saveIt();
            }
            return true;
        }
        else if(id == item.getItemId())
        {
            finish();
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

        return "https://backboneblog.page.link/?link=https://backboneblog.com/"+getIntent().getStringExtra("ref")+"/"+
                "&apn=" +getPackageName()+
                "&st=" + /*titleSocial*/
                URLEncoder.encode(title.getText().toString()) +
                "&utm_source=" + /*source*/
                "AndroidApp";

    }

    public void saveIt()
    {
        databaseReference.child(postRef).child("save").child(currentUserUid).setValue(currentUserUid);
    }
    public void removeIt()
    {
        databaseReference.child(postRef).child("save").child(currentUserUid).setValue(null);
    }

    public void checkAlredySave()
    {
        databaseReference.child(postRef).child("save")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(currentUserUid))
                        {
                            saveAlready = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
