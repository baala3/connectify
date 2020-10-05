package com.example.socialnetwork;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    RecyclerView myFrdList;
    DatabaseReference friendRef,userRef,friendsRf;
   public    String currentUserId="";
    TextView textView4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_friends, container, false);
      if(currentUserId.equals(""))  currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendRef= FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserId);
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        friendsRf= FirebaseDatabase.getInstance().getReference().child("Friends");
        myFrdList=v.findViewById(R.id.frdRecycler);
        textView4=v.findViewById(R.id.textView4);
        myFrdList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFrdList.setLayoutManager(linearLayoutManager);
        displayAllFriends();
        return  v;
    }

    private void displayAllFriends() {
        FirebaseRecyclerOptions<FriendsModel> options=new FirebaseRecyclerOptions.Builder<FriendsModel>().setQuery(friendRef, FriendsModel.class).build();

        FirebaseRecyclerAdapter<FriendsModel,FriendsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FriendsModel, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull FriendsModel model) {
                Log.d("WWWW",model.getDate());
           holder.setDate(model.getDate());
                final String frdId=getRef(position).getKey();
                userRef.child(frdId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            final String uname=snapshot.child("fullName").getValue().toString();
                            String uimage=snapshot.child("profileImage").getValue().toString();
                            holder.setFullName(uname);
                            holder.setProfileImage(getActivity(),uimage);
                            String type;
                            if(snapshot.hasChild("userState"))
                            {
                                type=snapshot.child("userState").child("type").getValue().toString();
                                if(type.equals("online"))
                                  {  holder.online.setVisibility(View.VISIBLE);}
                                else
                                {
                                    holder.online.setVisibility(View.INVISIBLE);
                                }

                            }
                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(MainActivity2.currentfrag==MainActivity2.MESSAGES)
                                    {
                                        textView4.setVisibility(View.GONE);
                                        Intent personI=new Intent(getActivity(),ChatActivity.class);
                                        personI.putExtra("messageReceiverId",frdId);
                                        personI.putExtra("messageReceiverName",uname);
                                        startActivity(personI);
                                    }
                                    else
                                    {
                                        textView4.setVisibility(View.VISIBLE);
                                        textView4.setText(uname+" Friends");
                                        CharSequence options[]=new CharSequence[]{
                                                uname+"'s Profile",
                                                "Send Message",
                                                "Un friend"+uname
                                        };
                                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                        builder.setTitle("select options");
                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if(which==0)
                                                {
                                                    Intent personI=new Intent(getActivity(),PersonActivity.class);
                                                    personI.putExtra("visit_user_id",frdId);
                                                    startActivity(personI);

                                                }
                                                if(which==1)
                                                {
                                                    Intent personI=new Intent(getActivity(),ChatActivity.class);
                                                    personI.putExtra("messageReceiverId",frdId);
                                                    personI.putExtra("messageReceiverName",uname);
                                                    startActivity(personI);
                                                }
                                                if(which==2)
                                                {
                                                    unFriendAnExistingFriend(frdId);
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
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(getActivity()).inflate(R.layout.all_users_display_layout,parent,false);
                return  new FriendsViewHolder(v);
            }
        };
        myFrdList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public  static class FriendsViewHolder extends RecyclerView.ViewHolder{

        ImageView online;
        View mView;
        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            online=mView.findViewById(R.id.online);
        }
        public void setProfileImage(Context ctx, String all_users_profile_image) {
            CircleImageView all_users_profile_imag=mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(all_users_profile_image).placeholder(R.drawable.profile).into(all_users_profile_imag);
        }
        public void setFullName(String fullName) {
            TextView myName=mView.findViewById(R.id.all_users_name);
            myName.setText(fullName);
        }
        public void setDate(String date) {
            TextView myDate=mView.findViewById(R.id.all_users_status);
            myDate.setText("Friends since : "+date);
        }
    }

    private void unFriendAnExistingFriend(final  String receiver_user_id) {
     final    String sender_user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
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

}