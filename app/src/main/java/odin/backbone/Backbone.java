package odin.backbone;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;

import io.fabric.sdk.android.Fabric;


public class Backbone extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Fresco.initialize(this);
        FirebaseApp.initializeApp(this);

    }
}
