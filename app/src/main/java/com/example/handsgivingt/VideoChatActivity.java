package com.example.handsgivingt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.media.Image;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {

    private static String API_Key = "47191224";
    private static String SESSION_ID = "1_MX40NzE5MTIyNH5-MTYxOTM1NjY0NzAwNH5vYUQyVzRjeWcxQWdnWVlCWnJpOVBBcmJ-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NzE5MTIyNCZzaWc9MzM4YjRhZTYyMDgyNzRjMDAwM2MzYzdkMWQxOWIxNWJmMTMzNTJjNzpzZXNzaW9uX2lkPTFfTVg0ME56RTVNVEl5Tkg1LU1UWXhPVE0xTmpZME56QXdOSDV2WVVReVZ6UmplV2N4UVdkbldWbENXbkpwT1ZCQmNtSi1mZyZjcmVhdGVfdGltZT0xNjE5MzU2NzQ5Jm5vbmNlPTAuMzM4NjIyNzUxMDUxMTYzOSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNjIxOTQ4NzQ5JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final String LOG_TAG = VideoChatActivity.class.getSimpleName();
    private static final int RC_VIDEO_APP_PERM = 124;
    private DatabaseReference usersRef;

    private FrameLayout mPublisherViewController;
    private FrameLayout mSubscriberViewController;
    //****
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private ImageView closeVideoChatBtn;
    private String userID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        closeVideoChatBtn = findViewById(R.id.close_video_chat_btn);
        closeVideoChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(userID).hasChild("Ringing"))
                        {
                            usersRef.child(userID).child("Ringing").removeValue();

                            if (mPublisher != null)
                            {
                                System.out.println("mPublisher destroyed");
                                mPublisher.destroy();
                            }

                            if (mSubscriber != null)
                            {
                                System.out.println("mSubscriber destroyed");
                                mSubscriber.destroy();
                            }

                            //**
                            startActivity(new Intent(VideoChatActivity.this, MainActivity.class));
                            finish();
                        }
                        if (dataSnapshot.child(userID).hasChild("Calling"))
                        {
                            usersRef.child(userID).child("Calling").removeValue();

                            if (mPublisher != null)
                            {
                                System.out.println("mPublisher2 destroyed");
                                mPublisher.destroy();
                            }

                            if (mSubscriber != null)
                            {
                                System.out.println("mSubscriber2 destroyed");
                                mSubscriber.destroy();
                            }

                            //**
                            startActivity(new Intent(VideoChatActivity.this, MainActivity.class));
                            finish();
                        }
                        else
                        {

                            if (mPublisher != null)
                            {
                                System.out.println("mPublisher3 destroyed");
                                mPublisher.destroy();
                            }

                            if (mSubscriber != null)
                            {
                                System.out.println("mSubscriber3 destroyed");
                                mSubscriber.destroy();
                            }
                            startActivity(new Intent(VideoChatActivity.this, MainActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, VideoChatActivity.this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions()
    {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms))
        {
            mPublisherViewController = findViewById(R.id.publisher_container);
            mSubscriberViewController = findViewById(R.id.subscriber_container);

            //1. Initilize and Connect to Session
            mSession = new Session.Builder(this, API_Key, SESSION_ID).build();
            mSession.setSessionListener(VideoChatActivity.this);
            mSession.connect(TOKEN);
        }
        else
        {
            EasyPermissions.requestPermissions(this, "This app needs mic and camera permission. Please allow.", RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    //2.Publishing a stream to the session.
    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(VideoChatActivity.this);
        mPublisherViewController.addView(mPublisher.getView());

        if (mPublisher.getView() instanceof GLSurfaceView)
        {
            ((GLSurfaceView) mPublisher.getView() ).setZOrderOnTop(true);
        }
        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Stream Disconnected");

    }

    //3.Subscribing to the streams.
    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");

        if (mSubscriber == null)
        {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewController.addView(mSubscriber.getView());

        }

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");
        if (mSubscriber != null)
        {
            mSubscriber = null;
            mSubscriberViewController.removeAllViews();
        }

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.i(LOG_TAG, "Stream Error");

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}