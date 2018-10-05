package odin.backbone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Arrays;
import java.util.List;

public class Login extends Activity {
    private static final int RC_SIGN_IN = 123;
    UserFace userFace;
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isNetworkConnected()) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.FacebookBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setLogo(R.drawable.bone)
                            .setTheme(R.style.LoginTheme)
                            .build(),
                    RC_SIGN_IN);
        }
        else
        {
               showAlert("No internet connection !!");
        }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            user = FirebaseAuth.getInstance().getCurrentUser();
            checkUserAlready();
        }

            else {
                this.finish();
            }

    }




    public void checkUserAlready()
    {
        firebaseDatabase.orderByChild("uid").equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){
                            Intent intent = new Intent(Login.this,Main.class);
                            startActivity(intent);
                            finish();

                        }else{
                            addUserDetails();
                            Intent intent = new Intent(Login.this,EditProfile.class);
                            intent.putExtra("firstTimeLogin",true);
                            startActivity(intent);
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
        });


    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    public void showAlert(String msg)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).show();
    }

    private void addUserDetails() {
            userFace = new UserFace(user.getDisplayName(), user.getEmail(), "default/avatar.png", user.getPhoneNumber(),"I am "+user.getDisplayName());
            firebaseDatabase.child(user.getUid()).setValue(userFace);
            firebaseDatabase.child(user.getUid()).child("uid").setValue(user.getUid());
    }



}
