package odin.backbone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static odin.backbone.Post.getYesterdayDateString;

public class SearchActivity extends AppCompatActivity {

    ActionBar actionBar;
    LayoutInflater inflator;
    View v;
    EditText searchQuery;
    FirebaseRecyclerAdapter<PostFace,PostViewHolder> adapter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    String date,yesterDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflator.inflate(R.layout.search_layout, null);
        searchQuery = v.findViewById(R.id.search_query);
        actionBar.setCustomView(v);

        recyclerView =  findViewById(R.id.search_list);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layoutManager);

        date = df.format(c);
        yesterDay = getYesterdayDateString();


        searchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                searchText(editable.toString());
            }
        });


    }



    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideKeyboard(this);
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

    @Override
    protected void onPause() {
        super.onPause();
        hideKeyboard(this);
    }


    public void searchText(String searchVal)
    {

        final Intent i = new Intent(SearchActivity.this,PostDet.class);
        final Intent in = new Intent(SearchActivity.this,PostUserProfile.class);


        Query query = FirebaseDatabase.getInstance().getReference().child("post").
                orderByChild("post_title")
                .startAt(searchVal).endAt(searchVal+"\uf8ff");

        FirebaseRecyclerOptions<PostFace> options =
                new FirebaseRecyclerOptions.Builder<PostFace>()
                        .setQuery(query, PostFace.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<PostFace, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PostViewHolder holder, final int position, @NonNull final PostFace model) {

                if(model.privacy.equals("Private"))
                {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
                else {

                    holder.postTitle.setText(model.post_title);
                    setImage(model.post_photo, holder.postImage);
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
                    setImage(model.writter_photo, holder.writterImage);
                    holder.postViews.setText(model.post_views + " views");
                    holder.writterImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            in.putExtra("uName", model.writter_name);
                            in.putExtra("uBio", model.writter_bio);
                            in.putExtra("uPhoto", model.writter_photo);
                            in.putExtra("uUid",model.uid);
                            startActivity(in);

                        }
                    });

                    holder.writterName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            in.putExtra("uName", model.writter_name);
                            in.putExtra("uBio", model.writter_bio);
                            in.putExtra("uPhoto", model.writter_photo);
                            in.putExtra("uUid",model.uid);
                            startActivity(in);

                        }
                    });

                    holder.postDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            in.putExtra("uName", model.writter_name);
                            in.putExtra("uBio", model.writter_bio);
                            in.putExtra("uPhoto", model.writter_photo);
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


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            searchQuery.requestFocus();
        }
    }

}
