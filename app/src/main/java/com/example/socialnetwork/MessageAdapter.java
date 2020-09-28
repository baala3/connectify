package com.example.socialnetwork;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    List<Messages> userMesaageList;
    FirebaseAuth mAuth;
    DatabaseReference userRef;

    public MessageAdapter(List<Messages> userMesaageList) {
        this.userMesaageList = userMesaageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false);
    mAuth=FirebaseAuth.getInstance();
      return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Messages messages=userMesaageList.get(position);
        String message_sender_id=mAuth.getCurrentUser().getUid();
        String from_user_id=messages.getFrom();
        String fromMessageType=messages.getType();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(from_user_id);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {

                String image=snapshot.child("profileImage").getValue().toString();
                Picasso.with(holder.message_image.getContext()).load(image).placeholder(R.drawable.profile).into(holder.message_image);
            }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(messages.getType().equals("text"))
        {
            holder.message_image.setVisibility(View.GONE);
            holder.lll1.setVisibility(View.GONE);
            if(from_user_id.equals(message_sender_id))
            {
                holder.sender_message_text.setBackgroundResource(R.drawable.sender_message_text);
                holder.sender_message_text.setTextColor(Color.WHITE);
                holder.sender_message_text.setGravity(Gravity.LEFT);
                holder.sender_message_text.setText(messages.getMessageText());
                holder.sender_message_time.setText(messages.getTime());

            }
            else
            {
                holder.lll2.setVisibility(View.GONE);
                holder.lll1.setVisibility(View.VISIBLE);
                holder.message_image.setVisibility(View.VISIBLE);
                holder.receiver_message_text.setBackgroundResource(R.drawable.receiver_message_text);
                holder.receiver_message_text.setTextColor(Color.WHITE);
                holder.receiver_message_text.setGravity(Gravity.LEFT);
                holder.receiver_message_text.setText(messages.getMessageText());
                holder.receiver_message_time.setText(messages.getTime());
            }
        }
     }

    @Override
    public int getItemCount() {
        return userMesaageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView receiver_message_text,sender_message_text;
        TextView receiver_message_time,sender_message_time;
        LinearLayout lll1,lll2;
        CircleImageView message_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            receiver_message_text=itemView.findViewById(R.id.receiver_message_text);
            sender_message_text=itemView.findViewById(R.id.sender_message_text);
            message_image=itemView.findViewById(R.id.message_image);


            receiver_message_time=itemView.findViewById(R.id.receiver_message_time);
            sender_message_time=itemView.findViewById(R.id.sender_message_time);
            lll1=itemView.findViewById(R.id.lll1);
            lll2=itemView.findViewById(R.id.lll2);
        }

        public void setData(String date, String from, String messageText, String time, String type) {

        }
    }
}
