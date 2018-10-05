package odin.backbone;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static odin.backbone.Post.getYesterdayDateString;

public class Save extends AppCompatActivity {

    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    String date,yesterDay;
    String currentUserId = FirebaseAuth.getInstance().getUid();
    SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseRecyclerAdapter<PostFace,PostViewHolder> adapter;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        recyclerView = findViewById(R.id.post_list);
        layoutManager = new LinearLayoutManager(Save.this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mSwipeRefreshLayout = findViewById(R.id.post_swife_refresh);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        refresh();
        date = df.format(c);
        yesterDay = getYesterdayDateString();
    }

    public void refresh()
    {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                if(mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
                setRecyclerViewData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                },3000);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRecyclerViewData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                },3000);
            }
        });
    }

    void setRecyclerViewData()
    {
        final Intent i = new Intent(Save.this,PostDet.class);
        final Intent in = new Intent(Save.this,PostUserProfile.class);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("post").orderByChild("save/"+currentUserId).equalTo(currentUserId);

        FirebaseRecyclerOptions<PostFace> options =
                new FirebaseRecyclerOptions.Builder<PostFace>()
                        .setQuery(query, PostFace.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<PostFace, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PostViewHolder holder, final int position, @NonNull final PostFace model) {


                holder.postTitle.setText(model.post_title);
                setImage(model.post_photo,holder.postImage);
                if(date.equals(model.post_date))
                {
                    holder.postDate.setText("today "+model.post_time);
                }
                else if(yesterDay.equals(model.post_date))
                {
                    holder.postDate.setText("yesterday "+model.post_time);
                }
                else
                {
                    holder.postDate.setText(model.post_date);
                }
                holder.writterName.setText(model.writter_name);
                setImage(model.writter_photo,holder.writterImage);
                holder.postViews.setText(model.post_views +" views");
                holder.writterImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        in.putExtra("uName",model.writter_name);
                        in.putExtra("uBio",model.writter_bio);
                        in.putExtra("uPhoto",model.writter_photo);
                        in.putExtra("uUid",model.uid);
                        startActivity(in);

                    }
                });

                holder.writterName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        in.putExtra("uName",model.writter_name);
                        in.putExtra("uBio",model.writter_bio);
                        in.putExtra("uPhoto",model.writter_photo);
                        in.putExtra("uUid",model.uid);
                        startActivity(in);

                    }
                });

                holder.postDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        in.putExtra("uName",model.writter_name);
                        in.putExtra("uBio",model.writter_bio);
                        in.putExtra("uPhoto",model.writter_photo);
                        in.putExtra("uUid",model.uid);
                        startActivity(in);

                    }
                });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        i.putExtra("title", model.post_title);
                        i.putExtra("description", model.post_description);
                        i.putExtra("photo", model.post_photo);
                        i.putExtra("date", model.post_date);
                        i.putExtra("time", model.post_time);
                        i.putExtra("ref", getRef(getItemCount() - 1 - position).getKey());
                        startActivity(i);
                    }
                });



            }



            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_layout, parent, false);

                return new PostViewHolder(view);
            }

            @NonNull
            @Override
            public PostFace getItem(int position) {
                return super.getItem(getItemCount() -1 - position);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                adapter.notifyDataSetChanged();
            }



        };
        adapter.startListening();
        if (adapter != null) {
            recyclerView.setAdapter(adapter);
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
}
