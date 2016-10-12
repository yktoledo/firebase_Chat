package com.yendry.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.yendry.firebase.R.id.imgMsg;

/**
 * Created by User on 10/5/2016.
 */
public class Chat_Room extends AppCompatActivity{

    private Button btn_send_msg;
    private EditText input_msg;
    //private TextView chat_conversation;
    private RecyclerView recyclerView;

    private static final int CAMERA_CODE = 1;
    public static  String user_name, room_name;
    private DatabaseReference root;
    private String temp_key;
    FirebaseStorage storage;
    StorageReference storageRef;
    ArrayList<Message> msgList = new ArrayList<>();
    ConverseView converseView;
    ProgressDialog progressBar;
    StorageReference backendStorage;
    Uri uriSavedImage;
    ImageButton uploadImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);
        storage =  FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://fir-82de7.appspot.com");
        StorageReference imagesRef = storageRef.child("images");
        backendStorage = FirebaseStorage.getInstance().getReference();

        btn_send_msg = (Button) findViewById(R.id.btnSendId);
        input_msg = (EditText) findViewById(R.id.editSendId);
        //chat_conversation = (TextView) findViewById(R.id.text_view_chat_room);
        recyclerView = (RecyclerView) findViewById(R.id.converseView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        uploadImg = (ImageButton) findViewById(R.id.imageButton);
        progressBar = new ProgressDialog(this);

        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();

        setTitle("Room - "+room_name);
        root = FirebaseDatabase.getInstance().getReference().child("chat").child(room_name);

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushToFB(user_name,input_msg.getText().toString());
                /*Map<String, Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);

                Map<String, Object> map2 = new HashMap<>();
                map2.put("name", user_name);
                map2.put("msg", input_msg.getText().toString());
                input_msg.setText("");
                message_root.updateChildren(map2);*/

            }
        });
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (uriSavedImage != null) {
            outState.putString("cameraImageUri", uriSavedImage.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            uriSavedImage = Uri.parse(savedInstanceState.getString("cameraImageUri"));
        }
    }

    private String chat_msg, chat_user_name;
    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        //msgList.clear();
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
            Message m = new Message();
            m.setSender(chat_user_name);
            m.setMessage(chat_msg);
            msgList.add(m);

            /*chat_conversation.append*///Log.d("room",chat_user_name+", "+chat_msg);


        }
        if(msgList.size()>0){
            //Log.d("room","Inside nonzero");
            converseView = new ConverseView(Chat_Room.this,msgList);
            recyclerView.setAdapter(converseView);
            recyclerView.scrollToPosition(msgList.size()-1);
        }
    }

    private void pushToFB(String user,String message){
        Map<String, Object> map = new HashMap<String, Object>();
        temp_key = root.push().getKey();
        root.updateChildren(map);

        DatabaseReference message_root = root.child(temp_key);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", user);
        map2.put("msg", message);
        input_msg.setText("");
        message_root.updateChildren(map2);
    }
    private void captureImage(){
        Calendar calendar = Calendar.getInstance();
        long stamp = calendar.getTimeInMillis();
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        uriSavedImage=Uri.fromFile(new File("/sdcard/demo/flashImage_"+stamp+".png"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        int result = ContextCompat.checkSelfPermission(Chat_Room.this, android.Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED){
            startActivityForResult(intent,CAMERA_CODE);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Chat_Room.this, android.Manifest.permission.CAMERA)){
                Toast.makeText(Chat_Room.this, "Camera permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(Chat_Room.this,new String[]{android.Manifest.permission.CAMERA},CAMERA_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //progressBar.setMessage("Uploading Image ...");
        //progressBar.show();
        if(requestCode==CAMERA_CODE && resultCode==RESULT_OK){
            Uri uri = uriSavedImage;

            StorageReference filePath = backendStorage.child("image").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //progressBar.dismiss();
                    Toast.makeText(Chat_Room.this,"Uploaded successfully",Toast.LENGTH_LONG).show();

                    pushToFB(user_name,taskSnapshot.getDownloadUrl().toString());
                    //Uri downloadImg = taskSnapshot.getDownloadUrl();
                    //Picasso.with(Chat_Room.this).load(downloadImg).fit().centerCrop().into();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Camera",e.getMessage());
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Calendar calendar = Calendar.getInstance();
                    long stamp = calendar.getTimeInMillis();
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    uriSavedImage=Uri.fromFile(new File("/sdcard/demo/flashImage_"+stamp+".png"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                    startActivityForResult(intent,CAMERA_CODE);
                }
        }
    }
}
