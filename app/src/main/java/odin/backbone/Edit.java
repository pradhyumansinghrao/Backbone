package odin.backbone;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import es.dmoral.toasty.Toasty;

public class Edit extends Fragment{

    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<PostFace,EditViewHolder> adapter;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public Edit() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit, container, false);

        recyclerView = v.findViewById(R.id.edit_list);
        layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mSwipeRefreshLayout = v.findViewById(R.id.edit_swife_refresh);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        refresh();
        return v;
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
                },4000);
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
                },4000);
            }
        });
    }
    public static Edit newInstance() {
        Edit edit = new Edit();
        return edit;
    }


    public void setRecyclerViewData()
    {
        Toasty.Config.getInstance().setSuccessColor(getResources().getColor(R.color.colorPrimaryDark)).apply();
        final Intent[] i = new Intent[1];
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("post").orderByChild("uid").equalTo(FirebaseAuth.getInstance().getUid());
        FirebaseRecyclerOptions<PostFace> options =
                new FirebaseRecyclerOptions.Builder<PostFace>()
                        .setQuery(query, PostFace.class)
                        .build();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    recyclerView.setBackgroundResource(R.drawable.nothing);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
            adapter = new FirebaseRecyclerAdapter<PostFace, EditViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final EditViewHolder holder, final int position, @NonNull final PostFace model) {

                holder.postTitle.setText(model.post_title);
                holder.postViews.setText(model.post_views+" views");
                setImage(model.post_photo,holder.postImage);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle("Delete").setMessage("are you sure delete this post ?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getRef(getItemCount() - 1 - position).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toasty.success(getContext(), "Post Deleted", Toast.LENGTH_LONG, true).show();

                                                }
                                            }
                                        });
                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                    }
                });

                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        i[0] = new Intent(getContext(),ReEdit.class);
                        i[0].putExtra("ref",""+getRef(getItemCount() - 1 - position).getKey());
                        i[0].putExtra("title",model.post_title);
                        i[0].putExtra("details",model.post_description);
                        i[0].putExtra("photo",model.post_photo);
                        i[0].putExtra("annonymously",model.annonymously);
                        i[0].putExtra("privacy",model.privacy);
                        i[0].putExtra("views",model.post_views);
                        startActivity(i[0]);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        i[0] = new Intent(getActivity(),PostDet.class);
                        i[0].putExtra("title",model.post_title);
                        i[0].putExtra("description",model.post_description);
                        i[0].putExtra("photo",model.post_photo);
                        i[0].putExtra("date",model.post_date);
                        i[0].putExtra("time",model.post_time);
                        i[0].putExtra("ref", getRef(getItemCount() - 1 - position).getKey());
                        startActivity(i[0]);
                    }
                });

            }

            @NonNull
            @Override
            public EditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.edit_post_layout, parent, false);
                return new EditViewHolder(view);
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
            recyclerView.setAdapter(adapter);
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
