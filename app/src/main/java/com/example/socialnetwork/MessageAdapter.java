package com.example.socialnetwork;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Messages messages=userMesaageList.get(position);

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
            holder.receiver_message_image.setVisibility(View.GONE);
            holder.sender_message_image.setVisibility(View.GONE);


            if(from_user_id.equals(message_sender_id))
            {
                holder.sender_message_text.setBackgroundResource(R.drawable.sender_message_text);
                //holder.sender_message_text.setTextColor(Color.BLACK);
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
            //    holder.receiver_message_text.setTextColor(Color.BLACK);
                holder.receiver_message_text.setGravity(Gravity.LEFT);
                holder.receiver_message_text.setText(messages.getMessageText());
                holder.receiver_message_time.setText(messages.getTime());
            }
        }
     else  if(messages.getType().equals("image"))
        {
            holder.message_image.setVisibility(View.GONE);
            holder.lll1.setVisibility(View.GONE);
            holder.lll2.setVisibility(View.GONE);
            holder.receiver_message_image.setVisibility(View.GONE);

            if(from_user_id.equals(message_sender_id))
            {
                holder.sender_message_image.setVisibility(View.VISIBLE);
                Picasso.with(holder.itemView.getContext()).load(messages.getMessageText()).placeholder(R.drawable.profile_icon).into(holder.sender_message_image);

            }
            else
            {
                holder.message_image.setVisibility(View.VISIBLE);
                holder.receiver_message_image.setVisibility(View.VISIBLE);
                holder.sender_message_image.setVisibility(View.GONE);
                Picasso.with(holder.itemView.getContext()).load(messages.getMessageText()).placeholder(R.drawable.profile_icon).into(holder.receiver_message_image);

            }
        }
     else if (messages.getType().equals("pdf")||messages.getType().equals("docs"))
        {
            holder.message_image.setVisibility(View.GONE);
            holder.lll1.setVisibility(View.GONE);
            holder.lll2.setVisibility(View.GONE);
            holder.receiver_message_image.setVisibility(View.GONE);

            if(from_user_id.equals(message_sender_id))
            {
                holder.sender_message_image.setVisibility(View.VISIBLE);
                holder.sender_message_image.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
               // Picasso.with(holder.itemView.getContext()).load(messages.getMessageText()).placeholder(R.drawable.profile_icon).into(holder.sender_message_image);

            }
            else
            {
                holder.message_image.setVisibility(View.VISIBLE);
                holder.receiver_message_image.setVisibility(View.VISIBLE);
                holder.sender_message_image.setVisibility(View.GONE);
                holder.receiver_message_image.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
         /*       holder.receiver_message_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse(messages.getMessageText()));
                        holder.itemView.getContext().startActivity(i);
                    }
                });*/
              //  Picasso.with(holder.itemView.getContext()).load(messages.getMessageText()).placeholder(R.drawable.profile_icon).into(holder.receiver_message_image);

            }
        }



        if(from_user_id.equals(message_sender_id))
          {
         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(messages.getType().equals("text"))
                 {

                     CharSequence options[]=new CharSequence[]
                             {
                                     "delete for me",
                                     "delete for everyone",
                                     "cancel"

                             };
                     AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                     builder.setTitle("Select Options");
                     builder.setItems(options, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {

                             if(which==0)
                             {
                                 holder.deleteSentMessage(position,holder);
                             }
                             else if(which==1)
                             {
                                 holder.deleteMessageForEveryOne(position,holder);

                             }
                         }
                     });
                     builder.show();
                 }
                 else if(messages.getType().equals("pdf") || messages.getType().equals("docs"))
                 {
                     CharSequence options[]=new CharSequence[]
                             {
                                     "delete for me",
                                     "delete for everyone",
                                     "open",
                                     "cancel"

                             };
                     AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                     builder.setTitle("Select Options");
                     builder.setItems(options, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {

                             if(which==0)
                             {
                               holder.deleteSentMessage(position,holder);

                             }
                             else if(which==1)
                             {

                                 holder.deleteMessageForEveryOne(position,holder);
                             }
                             else if(which==2)
                             {
                                 Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse(messages.getMessageText()));
                                 holder.itemView.getContext().startActivity(i);

                             }
                         }
                     });
                     builder.show();
                 }
                 else if(messages.getType().equals("image"))
                 {
                     CharSequence options[]=new CharSequence[]
                             {
                                     "delete for me",
                                     "delete for everyone",
                                     "open",
                                     "cancel"

                             };
                     AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                     builder.setTitle("Select Options");
                     builder.setItems(options, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {

                             if(which==0)
                             {
                                 holder.deleteSentMessage(position,holder);
                             }
                             else if(which==1)
                             {
                                 holder.deleteMessageForEveryOne(position,holder);

                             }
                             else if(which==2)
                             {
                                 Intent ii=new Intent(holder.itemView.getContext(),ImageViewActivity.class);
                                 ii.putExtra("comment","false");
                                 ii.putExtra("image",messages.getMessageText());
                                 holder.itemView.getContext().startActivity(ii);
                             }
                         }
                     });
                     builder.show();
                 }
             }
         });
     }
      else
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(messages.getType().equals("text"))
                    {

                        CharSequence options[]=new CharSequence[]
                                {
                                        "delete for me",
                                        "cancel"

                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which==0)
                                {

                                    holder.deleteReceiveMessage(position,holder);
                                }
                            }
                        });
                        builder.show();
                    }
                    else if(messages.getType().equals("pdf") || messages.getType().equals("docs"))
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "delete for me",
                                        "open",
                                        "cancel"

                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which==0)
                                {
                                    holder.deleteReceiveMessage(position,holder);

                                }
                                else if(which==1)
                                {
                                    Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse(messages.getMessageText()));
                                    holder.itemView.getContext().startActivity(i);
                                }


                            }
                        });
                        builder.show();
                    }
                    else if(messages.getType().equals("image"))
                    {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "delete for me",
                                        "open",
                                        "cancel"

                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which==0)
                                {
                                    holder.deleteReceiveMessage(position,holder);

                                }
                                else if(which==1)
                                {
                                    Intent ii=new Intent(holder.itemView.getContext(),ImageViewActivity.class);
                                    ii.putExtra("comment","false");
                                    ii.putExtra("image",messages.getMessageText());
                                    holder.itemView.getContext().startActivity(ii);


                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }
     }

    @Override
    public int getItemCount() {
        return userMesaageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView receiver_message_text,sender_message_text;
        TextView receiver_message_time,sender_message_time;
        ImageView receiver_message_image,sender_message_image;
        LinearLayout lll1,lll2;
        CircleImageView message_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            receiver_message_text=itemView.findViewById(R.id.receiver_message_text);
            sender_message_text=itemView.findViewById(R.id.sender_message_text);
            message_image=itemView.findViewById(R.id.message_image);

            receiver_message_image=itemView.findViewById(R.id.receiver_message_image);
            sender_message_image=itemView.findViewById(R.id.sender_message_image);



            receiver_message_time=itemView.findViewById(R.id.receiver_message_time);
            sender_message_time=itemView.findViewById(R.id.sender_message_time);
            lll1=itemView.findViewById(R.id.lll1);
            lll2=itemView.findViewById(R.id.lll2);
        }

        void  deleteSentMessage(final  int postion,final ViewHolder holder)
        {
            DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();

            rootRef.child("messages").child(userMesaageList.get(postion).getFrom())
                    .child(userMesaageList.get(postion).getTo())
                    .child(userMesaageList.get(postion).getMid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(holder.itemView.getContext(),"success",Toast.LENGTH_LONG).show();
                        }
                    else
                    {
                        Toast.makeText(holder.itemView.getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        void  deleteReceiveMessage(final  int postion,final ViewHolder holder)
        {
            DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();

            rootRef.child("messages").child(userMesaageList.get(postion).getTo())
                    .child(userMesaageList.get(postion).getFrom())
                    .child(userMesaageList.get(postion).getMid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(holder.itemView.getContext(),"success",Toast.LENGTH_LONG).show();
                         }
                    else
                    {
                        Toast.makeText(holder.itemView.getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        void  deleteMessageForEveryOne(final  int postion,final ViewHolder holder)
        {
           final DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();

            rootRef.child("messages").child(userMesaageList.get(postion).getTo())
                    .child(userMesaageList.get(postion).getFrom())
                    .child(userMesaageList.get(postion).getMid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {

                        rootRef.child("messages").child(userMesaageList.get(postion).getFrom())
                                .child(userMesaageList.get(postion).getTo())
                                .child(userMesaageList.get(postion).getMid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(holder.itemView.getContext(),"success",Toast.LENGTH_LONG).show();
                                }
                                else
                                    Toast.makeText(holder.itemView.getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(holder.itemView.getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
