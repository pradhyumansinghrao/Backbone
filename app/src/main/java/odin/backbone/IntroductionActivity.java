package odin.backbone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

public class IntroductionActivity extends TutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment(new Step.Builder().setTitle("Introduction to Backbone")
                .setContent("A way to sharing your thoughts and experiences with peoples")
                .setBackgroundColor(Color.parseColor("#ff5c5c")) // int background color
                .setDrawable(R.drawable.bone) // int top drawable
                .build());

        addFragment(new Step.Builder().setTitle("People")
                .setContent("A human relation is most important , sharing your first thoughts about people write what you like what you don't like about peoples")
                .setBackgroundColor(Color.parseColor("#ff5c5c")) // int background color
                .setDrawable(R.drawable.people) // int top drawable
                .build());

        addFragment(new Step.Builder().setTitle("Write Annonymously")
                .setContent("Backbone provide a feature to write annonmous")
                .setBackgroundColor(Color.parseColor("#ff5c5c")) // int background color
                .setDrawable(R.drawable.anon) // int top drawable
                .build());
    }

    @Override
    public void finishTutorial() {
        Intent intent = new Intent(getApplicationContext(),SplashScreen.class);
        startActivity(intent);
        finish();
    }
}
