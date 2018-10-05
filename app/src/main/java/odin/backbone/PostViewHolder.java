package odin.backbone;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostViewHolder extends RecyclerView.ViewHolder{

    public View itemView;
    public SimpleDraweeView postImage;
    public TextView postTitle;
    public Context context;
    public TextView postViews;
    public SimpleDraweeView writterImage;
    public TextView writterName;
    public TextView postDate;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();


    PostViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        postImage = itemView.findViewById(R.id.post_image);
        postTitle =  itemView.findViewById(R.id.post_title);
        postViews =  itemView.findViewById(R.id.post_views);
        writterImage = itemView.findViewById(R.id.writter_image);
        writterName = itemView.findViewById(R.id.writter_name);
        postDate = itemView.findViewById(R.id.post_date);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setRoundAsCircle(true);
        writterImage.getHierarchy().setRoundingParams(roundingParams);
    }

}
