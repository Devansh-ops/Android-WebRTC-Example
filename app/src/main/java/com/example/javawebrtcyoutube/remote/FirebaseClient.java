package com.example.javawebrtcyoutube.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.javawebrtcyoutube.utils.DataModel;
import com.example.javawebrtcyoutube.utils.ErrorCallBack;
import com.example.javawebrtcyoutube.utils.NewEventCallBack;
import com.example.javawebrtcyoutube.utils.SuccessCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Objects;

public class FirebaseClient {

    private final Gson gson = new Gson();
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private String currentUsername;

    private static final String LATEST_EVENT_FIELD_NAME = "latest_event";

    public void login(String username, SuccessCallBack callBack){
        dbRef.child(username).setValue("").addOnCompleteListener(task -> {
            currentUsername = username;
            callBack.onSuccess();
        });

    }

    public void sendMessageToOtherUser(DataModel dataModel, ErrorCallBack errorCallBack){
        Log.i("Message", "called sendMessageToOtherUser function in FirebaseClient.java");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(dataModel.getTarget()).exists()){
                    //send the signal to other user
                    Log.i("Message", "inside dbRef.addListenerForSingleValueEvent : onDataChange in FirebaseClient.java");
                    dbRef.child(dataModel.getTarget()).child(LATEST_EVENT_FIELD_NAME)
                            .setValue(gson.toJson(dataModel));

                }else {
                    errorCallBack.onError();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorCallBack.onError();
            }
        });
    }

    public void observeIncomingLatestEvent(NewEventCallBack callBack){
        dbRef.child(currentUsername).child(LATEST_EVENT_FIELD_NAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String data = Objects.requireNonNull(snapshot.getValue()).toString();
                    DataModel dataModel = gson.fromJson(data, DataModel.class);
                    callBack.onNewEventReceived(dataModel);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}