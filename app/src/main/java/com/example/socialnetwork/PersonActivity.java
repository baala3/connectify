package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonActivity extends AppCompatActivity {
    CircleImageView settings_profile_image;
    TextView settings_profile_status,settings_user_name,settings_user_fullname,settings_user_country,
            settings_dob,settings_gender,settings_relation_status;
    Button sendRequest, declineRequest;
    String sender_user_id,receiver_user_id;
    DatabaseReference friendReqRef,userRef,friendsRf;
    String saveCurrentDate,saveCurrentTime;
    String CURRENT_STATE;
    Toolbar toolbar;
    final String NOT_FRIENDS="notFriends",REQUEST_SENT="requestSent",REQUEST_RECEIVED= "requestReceived",FRIENDS="friends";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        toolbar=findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("People");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        receiver_user_id=getIntent().getStringExtra("visit_user_id");
        sender_user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        friendReqRef= FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        friendsRf= FirebaseDatabase.getInstance().getReference().child("Friends");
        UI();
        userRef.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {

                    String name=snapshot.child("name").getValue().toString();
                    String fullName=snapshot.child("fullName").getValue().toString();
                    String dob=snapshot.child("dob").getValue().toString();
                    String country=snapshot.child("country").getValue().toString();
                    String gender=snapshot.child("gender").getValue().toString();
                    String profileImage=snapshot.child("profileImage").getValue().toString();
                    String relationShipStatus=snapshot.child("relationShipStatus").getValue().toString();
                    String status=snapshot.child("status").getValue().toString();
                    Picasso.with(PersonActivity.this).load(profileImage).placeholder(R.drawable.profile).into(settings_profile_image);
                    settings_profile_status.setText(status);
                    settings_user_name.setText("@"+name);
                    settings_user_fullname.setText(fullName);
                    settings_dob.setText(dob);
                    settings_user_country.setText(country);
                    settings_gender.setText(gender);
                    settings_relation_status.setText(relationShipStatus);
                    sendRequestAndDecline();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        declineRequest.setVisibility(View.GONE);
        declineRequest.setEnabled(false);
        if(!receiver_user_id.equals(sender_user_id))
        {
            sendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendRequest.setEnabled(false);
                    if(CURRENT_STATE.equals(NOT_FRIENDS))
                    {
                        sendFriendReq();
                    }
                    else if(CURRENT_STATE.equals(REQUEST_SENT))
                    {
                        cancelFriendRequest();
                    }
                    else if(CURRENT_STATE.equals(REQUEST_RECEIVED))
                    {
                        acceptReques();
                    }
                    else if(CURRENT_STATE.equals(FRIENDS))
                    {
                        unFriendAnExistingFriend();
                    }

                }
            });

        }
        else{
            declineRequest.setVisibility(View.GONE);
            sendRequest.setVisibility(View.GONE);

        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void unFriendAnExistingFriend() {


        friendsRf.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    friendsRf.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                sendRequest.setEnabled(true);
                                sendRequest.setText("Send Friend Request");
                                CURRENT_STATE=NOT_FRIENDS;
                                declineRequest.setVisibility(View.GONE);
                                declineRequest.setEnabled(false);

                            }else {

                            }

                        }
                    });

                }
                else
                {

                }

            }
        });
    }

    private void acceptReques() {
        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd MMM yyyy z");
         saveCurrentDate=currentDate.format(calForDate.getTime());




                    friendsRf.child(sender_user_id).child(receiver_user_id).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                friendsRf.child(receiver_user_id).child(sender_user_id).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {


                                            friendReqRef.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        friendReqRef.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    sendRequest.setEnabled(true);
                                                                    sendRequest.setText("Un Friend");
                                                                    CURRENT_STATE=FRIENDS;
                                                                    declineRequest.setVisibility(View.GONE);
                                                                    declineRequest.setEnabled(false);

                                                                }else {

                                                                }

                                                            }
                                                        });

                                                    }
                                                    else
                                                    {

                                                    }

                                                }
                                            });
                                        }
                                        else
                                        {

                                        }
                                    }
                                });
                            }
                            else
                            {

                            }
                        }
                    });



    }

    private void cancelFriendRequest() {

        friendReqRef.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    friendReqRef.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                sendRequest.setEnabled(true);
                                sendRequest.setText("Send Friend Request");
                                CURRENT_STATE=NOT_FRIENDS;
                                declineRequest.setVisibility(View.GONE);
                                declineRequest.setEnabled(false);

                            }else {

                            }

                        }
                    });

                }
                else
                {

                }

            }
        });
    }

    private void sendRequestAndDecline() {
        friendReqRef.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(receiver_user_id))
                {
                    String request_type=snapshot.child(receiver_user_id).child("request_type").getValue().toString();
                    if(request_type.equals("sent"))
                    {
                        CURRENT_STATE=REQUEST_SENT;
                        sendRequest.setText("Cancel Friend Request");
                        declineRequest.setVisibility(View.GONE);
                        declineRequest.setEnabled(false);

                    }
                            else if(request_type.equals("request"))
                    {
                        CURRENT_STATE=REQUEST_RECEIVED;
                        sendRequest.setText("Accept Request");
                        declineRequest.setVisibility(View.VISIBLE);
                        declineRequest.setEnabled(true);
                        declineRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelFriendRequest();
                            }
                        });

                    }

                }
                else
                {
                    friendsRf.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(receiver_user_id))
                            {
                                CURRENT_STATE=FRIENDS;
                                sendRequest.setText("UnFriend");
                                declineRequest.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendFriendReq() {
        friendReqRef.child(sender_user_id).child(receiver_user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    friendReqRef.child(receiver_user_id).child(sender_user_id).child("request_type").setValue("request").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                sendRequest.setEnabled(true);
                                sendRequest.setText("Cancel Request");
                                CURRENT_STATE=REQUEST_SENT;
                                declineRequest.setVisibility(View.GONE);
                                declineRequest.setEnabled(false);

                            }else {

                            }

                        }
                    });

                }
                else
                {

                }

            }
        });
    }

    void UI()
    {
        CURRENT_STATE=NOT_FRIENDS;
        settings_profile_status=findViewById(R.id.settings_profile_status);
        settings_user_name=findViewById(R.id.settings_user_name);
        settings_user_fullname=findViewById(R.id.settings_user_fullname);
        settings_user_country=findViewById(R.id.settings_user_country);
        settings_dob=findViewById(R.id.settings_dob);
        settings_gender=findViewById(R.id.settings_gender);
        settings_relation_status=findViewById(R.id.settings_relation_status);
        settings_profile_image=findViewById(R.id.settings_profile_image);
        sendRequest =findViewById(R.id.person_accept_request);
        declineRequest =findViewById(R.id.person_decline_request);
    }
}