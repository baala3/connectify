package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    RecyclerView postCommentrecyclerView;
    EditText postCommentEditText;
    ImageButton postCommentisendButton;
    String postKey;
    DatabaseReference userRef,postRef;
    String currentUserId;
    String Cname,Cpost,Cimage;
    CircleImageView cimage;
    TextView cname;
    ImageView cpost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        postKey=getIntent().getStringExtra("postKey");
        Cname=getIntent().getStringExtra("cname");
        Cpost=getIntent().getStringExtra("cpost");
        Cimage=getIntent().getStringExtra("cimage");
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        postRef= FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("comments");
        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();


        cimage=findViewById(R.id.cimage);
        cpost=findViewById(R.id.cpost);
        cname=findViewById(R.id.cname);

        cname.setText(Cname);
        Picasso.with(this).load(Cimage).placeholder(R.drawable.profile).into(cimage);
        Picasso.with(this).load(Cpost).placeholder(R.drawable.profile).into(cpost);

        postCommentrecyclerView=findViewById(R.id.postCommentrecyclerView);
        postCommentEditText=findViewById(R.id.postCommentEditText);
        postCommentisendButton=findViewById(R.id.postCommentisendButton);
        postCommentrecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(CommentsActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postCommentrecyclerView.setLayoutManager(linearLayoutManager);
        postCommentisendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String name=snapshot.child("name").getValue().toString();
                            validateComments(name);
                            postCommentEditText.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    private void validateComments(String name) {
        String commentText=postCommentEditText.getText().toString();
        if(commentText.trim().length()==0)
        {
            Toast.makeText(this,"Please enter comment",Toast.LENGTH_LONG).show();
        }
        else
        {
            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDate=new SimpleDateFormat("dd MMM yyyy z");
            final String saveCurrentDate=currentDate.format(calForDate.getTime());

            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss");
            final String saveCurrentTime=currentTime.format(calForTime.getTime());

            final String randomKey=currentUserId+saveCurrentDate+saveCurrentTime;
            HashMap commentsMap=new HashMap();
            commentsMap.put("uid",currentUserId);
            commentsMap.put("comment",commentText);
            commentsMap.put("date",saveCurrentDate);
            commentsMap.put("time",saveCurrentTime);
            commentsMap.put("name",name);
            postRef.child(randomKey).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(CommentsActivity.this,"Success",Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        Toast.makeText(CommentsActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Comments> options=new FirebaseRecyclerOptions.Builder<Comments>().setQuery(postRef,Comments.class).build();


        FirebaseRecyclerAdapter<Comments,commentsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Comments, commentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull commentsViewHolder holder, int position, @NonNull Comments model) {

                holder.setData(model.getName(),model.getDate(),model.getTime(),model.getComment());

            }

            @NonNull
            @Override
            public commentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(CommentsActivity.this).inflate(R.layout.all_comments_layout,parent,false);
                return  new commentsViewHolder(v);
            }
        };
        postCommentrecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public static class commentsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView cUserName,cDate,cTime,cText;
        public commentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            cUserName=mView.findViewById(R.id.cUserName);
            cDate=mView.findViewById(R.id.cDate);
            cTime=mView.findViewById(R.id.cTime);
            cText=mView.findViewById(R.id.cText);
        }
        void setData(String name,String date,String time,String text){
            cText.setText("Comment:"+text);
            cUserName.setText("@:  "+name);
            cTime.setText(time);
            cDate.setText(date);

        }
    }
}