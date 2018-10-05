package odin.backbone;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class EditViewHolder extends RecyclerView.ViewHolder {

    public View itemView;
    public SimpleDraweeView postImage;
    public ImageView delete,edit;
    public TextView postTitle;
    public Context context;
    public TextView postViews;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();



    public EditViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        postImage = itemView.findViewById(R.id.edit_post_image);
        postTitle =  itemView.findViewById(R.id.edit_post_title);
        edit = itemView.findViewById(R.id.edit_e);
        delete = itemView.findViewById(R.id.delete_e);
        postViews = itemView.findViewById(R.id.post_views);
    }

}
