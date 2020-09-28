package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    RecyclerView chatList;
    ImageButton chatImage,chatSend;
    EditText chatInput;
      String messageReceiverId,messageReceiverName,messageSenderId;
    Toolbar toolbar;
    TextView custom_profile_name,custom_user_last_seen;
    CircleImageView custom_profile_image;
    DatabaseReference rooRef;
    String saveCurrentDate,saveCurrentTime;
    List<Messages> messagesList=new ArrayList<>();
    MessageAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageSenderId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        messageReceiverId=getIntent().getStringExtra("messageReceiverId");
        messageReceiverName=getIntent().getStringExtra("messageReceiverName");
        toolbarSetUp();
        rooRef= FirebaseDatabase.getInstance().getReference();
        UI();
        DisplayReceiverData();
        chatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchMessages();
    }

    private void fetchMessages() {
        rooRef.child("messages").child(messageSenderId).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists())
                {
                    Messages messages=snapshot.getValue(Messages.class);
                    messagesList.add(messages);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage() {

        String messageText=chatInput.getText().toString();
        chatInput.setText("");
        if(messageText.trim().length()==0)
        {

        }
        else
        {
            String message_sender_ref="messages/"+messageSenderId+"/"+messageReceiverId;
            String message_receiver_ref="messages/"+messageReceiverId+"/"+messageSenderId;
            DatabaseReference user_message_key=rooRef.child("messages").child(messageSenderId).child(messageReceiverId).push();
            String message_push_id=user_message_key.getKey();

            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDate=new SimpleDateFormat("dd MMM yyyy");
            saveCurrentDate=currentDate.format(calForDate.getTime());

            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm a");
            saveCurrentTime=currentTime.format(calForTime.getTime());
            Map messageTextBody=new HashMap();
            messageTextBody.put("messageText",messageText);
            messageTextBody.put("time",saveCurrentTime);
            messageTextBody.put("date",saveCurrentDate);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderId);

            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(message_sender_ref+"/"+message_push_id,messageTextBody);
            messageBodyDetails.put(message_receiver_ref+"/"+message_push_id,messageTextBody);
            rooRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this,"Sent Success",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }

                }
            });

        }
    }

    private void DisplayReceiverData() {
        custom_profile_name.setText(messageReceiverName);
        rooRef.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    final String Pimage=snapshot.child("profileImage").getValue().toString();
                    Picasso.with(ChatActivity.this).load(Pimage).placeholder(R.drawable.profile).into(custom_profile_image);

                    final  String type=snapshot.child("userState").child("type").getValue().toString();
                    final  String lastDate=snapshot.child("userState").child("date").getValue().toString();
                    final  String lastTime=snapshot.child("userState").child("time").getValue().toString();
                    if(type.equals("online"))
                    {
                        custom_user_last_seen.setText("Online");
                    }
                    else
                    {
                        custom_user_last_seen.setText("last seen:"+lastTime+" "+lastDate);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UI() {
        chatList=findViewById(R.id.chatList);
        chatImage=findViewById(R.id.chatImage);
        chatSend=findViewById(R.id.chatSend);
        chatInput=findViewById(R.id.chatInput);
        linearLayoutManager=new LinearLayoutManager(ChatActivity.this);
        chatList.setHasFixedSize(true);
           chatList.setLayoutManager(linearLayoutManager);
        adapter=new MessageAdapter(messagesList);
        chatList.setAdapter(adapter);
    }

    private void toolbarSetUp() {
        toolbar=findViewById(R.id.chat_bar_layout);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.chat_custom_bar_layout,null);
        actionBar.setCustomView(view);
        custom_profile_name=findViewById(R.id.custom_profile_name);
        custom_user_last_seen=findViewById(R.id.custom_user_last_seen);
        custom_profile_image=findViewById(R.id.custom_profile_image);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



}