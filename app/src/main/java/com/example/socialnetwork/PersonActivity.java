package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonActivity extends AppCompatActivity {
    CircleImageView settings_profile_image;
    TextView settings_profile_status,settings_user_name,settings_user_fullname,settings_user_country,
            settings_dob,settings_gender,settings_relation_status;
    Button sendRequest, declineRequest;
    String sender_user_id,receiver_user_id;
    DatabaseReference friendReqRef,userRef,friendsRf,notifRef;
    String saveCurrentDate,saveCurrentTime;
    String CURRENT_STATE;
    Toolbar toolbar;
    final String NOT_FRIENDS="notFriends",REQUEST_SENT="requestSent",REQUEST_RECEIVED= "requestReceived",FRIENDS="friends";


  //////
  long countFrds=0,countPosts=0;
  boolean open=false;
  RecyclerView my_post_list;
    String currentUserId;
    DatabaseReference postRef;
    DatabaseReference likeRef,frdRef;
    Button myPosts,myFriends;
    Boolean likeChecker=false,editChecker=false;
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
        notifRef= FirebaseDatabase.getInstance().getReference().child("Notifications");
        postRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        UI();

        myPosts=findViewById(R.id.person_posts);
        postRef.orderByChild("uid").startAt(receiver_user_id).endAt(receiver_user_id+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    countPosts=snapshot.getChildrenCount();
                    myPosts.setText(countPosts+" Posts");

                }
                else
                {
                    myPosts.setText("No Posts");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myFriends=findViewById(R.id.person_friends);
        myFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PersonActivity.this,MainActivity2.class);
                MainActivity2.frde=2222;
                i.putExtra("id",receiver_user_id);
                startActivity(i);
            }
        });
        frdRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        frdRef.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    countFrds=snapshot.getChildrenCount();
                    if(countFrds==1)
                    myFriends.setText(countFrds+" Friend");
                    else
                    myFriends.setText(countFrds+" Friends");

                }
                else
                {
                    myFriends.setText("No Friends");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userRef.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {


                    String name=snapshot.child("name").getValue().toString();
                    String fullName=snapshot.child("fullName").getValue().toString();
                    getSupportActionBar().setTitle(fullName);
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



        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        likeRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        my_post_list=findViewById(R.id.my_post_list);
        my_post_list.setHasFixedSize(false);
        my_post_list.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(PersonActivity.this,2);

        my_post_list.setLayoutManager(linearLayoutManager);

        displayMyPosts();

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
                                HashMap<String,String>  chatNotification=new HashMap<>();
                                chatNotification.put("from",sender_user_id);
                                chatNotification.put("type","request");
                                notifRef.child(receiver_user_id).push().setValue(chatNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            sendRequest.setEnabled(true);
                                            sendRequest.setText("Cancel Request");
                                            CURRENT_STATE=REQUEST_SENT;
                                            declineRequest.setVisibility(View.GONE);
                                            declineRequest.setEnabled(false);


                                        }
                                        else
                                        {

                                        }
                                    }
                                });
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

    private void displayMyPosts() {
        Query sortMyPost=  postRef.orderByChild("uid").startAt(receiver_user_id).endAt(receiver_user_id+"\uf8ff");

        FirebaseRecyclerOptions<Post> options=new FirebaseRecyclerOptions.Builder<Post>().setQuery(sortMyPost,Post.class).build();
        FirebaseRecyclerAdapter<Post, MyPostViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Post, MyPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MyPostViewHolder holder, final int position, @NonNull final Post model) {
                /*holder.setPostImage(holder.itemView.getContext(),model.getPostImage());*/
                Picasso.with(holder.itemView.getContext()).load(model.getPostImage()).into(holder.grid_image);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(holder.itemView.getContext(),ImageViewActivity.class);
                        i.putExtra("postKey",getRef(position).getKey());
                        i.putExtra("uid",model.getUid());
                        i.putExtra("comment","false");
                        holder.itemView.getContext().startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public MyPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(PersonActivity.this).inflate(R.layout.all_grid_layout,parent,false);
                return  new MyPostViewHolder(v);

            }
        };
        my_post_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }
    public static class MyPostViewHolder extends RecyclerView.ViewHolder{
        ImageView grid_image;
        public MyPostViewHolder(@NonNull View itemView) {
            super(itemView);
            grid_image=itemView.findViewById(R.id.grid_image);
        }


    }
}