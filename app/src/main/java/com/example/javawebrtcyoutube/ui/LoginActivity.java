package com.example.javawebrtcyoutube.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.javawebrtcyoutube.R;
import com.example.javawebrtcyoutube.databinding.ActivityLoginBinding;
import com.example.javawebrtcyoutube.repository.MainRepository;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding views;

    private MainRepository mainRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        init();
    }

    private void init(){
        mainRepository = MainRepository.getInstance();
        views.enterBtn.setOnClickListener(v->{
            // login to firebase here
            PermissionX.init(this)
                    .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                    .request((allGranted, grantedList, deniedList) -> {
                        if (allGranted){
                            mainRepository.login(
                                    views.username.getText().toString(), getApplicationContext(),() -> {
                                        startActivity(new Intent(LoginActivity.this, CallActivity.class));
                                    }

                            );
                        }
                    });
        });
    }
}