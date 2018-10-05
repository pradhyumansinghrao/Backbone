package odin.backbone;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

class MyNotificationManager {
    private static  Context mCtx;
    private static MyNotificationManager mInstance;

    private MyNotificationManager(Context context)
    {
        mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context)
    {
          if(mInstance == null)
              return new MyNotificationManager(context);
          return mInstance;
    }


    public void displayNotification(String title,String body)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, MyNotificationConstants.CHANNEL_ID)
                .setSmallIcon(R.drawable.bone)
                .setContentTitle(title)
                .setContentText(body);

        Intent intent = new Intent(mCtx,Main.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager!=null)
        {
            notificationManager.notify(1,mBuilder.build());
        }
    }




}
