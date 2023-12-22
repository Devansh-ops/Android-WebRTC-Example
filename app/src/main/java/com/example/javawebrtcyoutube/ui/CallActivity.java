package com.example.javawebrtcyoutube.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javawebrtcyoutube.R;
import com.example.javawebrtcyoutube.databinding.ActivityCallBinding;
import com.example.javawebrtcyoutube.repository.MainRepository;
import com.example.javawebrtcyoutube.utils.DataModelType;

public class CallActivity extends AppCompatActivity implements MainRepository.Listener{

    private ActivityCallBinding views;
    private MainRepository mainRepository;

    public Boolean isMicroPhoneMuted = false;

    private Boolean isCameraMuted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());
        init();

    }

    private void init(){
        mainRepository = MainRepository.getInstance();
        views.callBtn.setOnClickListener(v->{
            //start a call request here
            Log.i("Message", "Clicked on callBtn");
            mainRepository.sendCallRequest(views.targetUserNameEt.getText().toString(),()->{
                Toast.makeText(this, "Couldn't find the target", Toast.LENGTH_SHORT).show();
            });

        });

        mainRepository.initLocalView(views.localView);
        mainRepository.initRemoteView(views.remoteView);
        mainRepository.listener = this;

        mainRepository.subscribeForLatestEvent(data->{
            if (data.getType()== DataModelType.StartCall){
                runOnUiThread(()->{
                    views.incomingNameTV.setText(data.getSender()+" is Calling you");
                    views.incomingCallLayout.setVisibility(View.VISIBLE);
                    views.acceptButton.setOnClickListener(v->{
                        //star the call here
                        mainRepository.startCall(data.getSender());
                        views.incomingCallLayout.setVisibility(View.GONE);
                    });
                    views.rejectButton.setOnClickListener(v->{
                        views.incomingCallLayout.setVisibility(View.GONE);
                    });
                });
            }
        });

        views.switchCameraButton.setOnClickListener(v -> {
            mainRepository.switchCamera();
        });

        views.micButton.setOnClickListener( v -> {
            if (isMicroPhoneMuted){
                views.micButton.setImageResource(R.drawable.ic_baseline_mic_off_24);
            } else {
                views.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
            }
            mainRepository.toggleAudio(isMicroPhoneMuted);
            isMicroPhoneMuted = !isMicroPhoneMuted;
        });

        views.videoButton.setOnClickListener( v -> {
            if (isCameraMuted){
                views.videoButton.setImageResource(R.drawable.ic_baseline_videocam_off_24);
            } else {
                views.videoButton.setImageResource(R.drawable.ic_baseline_videocam_24);
            }
            mainRepository.toggleVideo(isCameraMuted);
            isCameraMuted = !isCameraMuted;
        });

        views.endCallButton.setOnClickListener(v -> {
            mainRepository.endCall();
            finish();
        });
    }

    @Override
    public void webrtcConnected(){
        runOnUiThread(() -> {
            views.incomingCallLayout.setVisibility(View.GONE);
            views.whoToCallLayout.setVisibility(View.GONE);
            views.callLayout.setVisibility(View.VISIBLE);
        });

    }
    @Override
    public void webrtcClosed(){
        runOnUiThread(this::finish);

    }
}
