package com.example.socialnetwork;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    RecyclerView all_users_post_list,story_list;
    DatabaseReference postRef;
    DatabaseReference likeRef,friendsRf;
    Boolean likeChecker=false,editChecker=false;
    String currentUserId;

    List<story> shoriesList=new ArrayList<>();
    List<String> postKeys=new ArrayList<>();
    Layout include;Uri resultUri;   ProgressDialog loadingBar;
    String saveCurrentDate,saveCurrentTime, postRandomName; String downloadUrl;
    CircleImageView add_story_image; static  final  int GALLERY_PICK=1; StorageReference postImageRef;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home, container, false);
        all_users_post_list=v.findViewById(R.id.all_users_post_list);
        story_list=v.findViewById(R.id.recylerstriy);
        all_users_post_list.setHasFixedSize(true);
            story_list.setHasFixedSize(true);
        currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        postRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        friendsRf= FirebaseDatabase.getInstance().getReference().child("Friends");
        postImageRef= FirebaseStorage.getInstance().getReference();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        all_users_post_list.setLayoutManager(linearLayoutManager);
        LinearLayoutManager anager=new LinearLayoutManager(getActivity());
        anager.setOrientation(RecyclerView.HORIZONTAL);
        story_list.setLayoutManager(anager);
        retrieveStory();
        displayAllUserPosts();

        loadingBar=new ProgressDialog(getActivity());
        add_story_image=v.findViewById(R.id.add_story_image);
        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Picasso.with(getContext()).load(snapshot.child("profileImage").getValue().toString()).into(add_story_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        add_story_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // openGallery();
              Intent i=new Intent(getActivity(),AddStoryActivity.class);
                startActivity(i);
            }
        });
        return  v;
    }
    private void openGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_PICK);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PICK && resultCode== Activity.RESULT_OK && data!=null) {
            Uri ImageUri = data.getData();
            CropImage.activity(ImageUri)
                    .start(getActivity(),this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                resultUri = result.getUri();
                if(resultUri==null)
                {
                    Toast.makeText(getActivity(),   "please select image",Toast.LENGTH_LONG).show();
                }
                else
                {
                    loadingBar.setTitle("Add new Post");
                    loadingBar.setMessage("Please wait");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    storeImageToStorage();
                }
            }
        }
    }

    private void storeImageToStorage() {
        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd MMM yyyy z");
        saveCurrentDate=currentDate.format(calForDate.getTime());

        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime=currentTime.format(calForTime.getTime());

        postRandomName =saveCurrentDate+saveCurrentTime;

        StorageReference filePath=postImageRef.child("Story Images").child(resultUri.getLastPathSegment()+postRandomName+".jpg");

        filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    FirebaseStorage.getInstance().getReference().child("Story Images").child(resultUri.getLastPathSegment()+postRandomName+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl=uri.toString();
                            savingPostInfoToDataBase();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                    Toast.makeText(getContext(),"Uploaded success",Toast.LENGTH_LONG).show();

                }
                else
                {
                    Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    private void savingPostInfoToDataBase() {

        DatabaseReference storyRef=FirebaseDatabase.getInstance().getReference().child("story");
        String storyKey= storyRef.push().getKey().toString();
        long timeend = System.currentTimeMillis()+86400000;
        HashMap postMap=new HashMap();
        postMap.put("imageurl",downloadUrl);
        postMap.put("timestart", ServerValue.TIMESTAMP);
        postMap.put("timeend",timeend);
        postMap.put("storyid",storyKey);
        postMap.put("userid",currentUserId);
        storyRef.child(storyKey).updateChildren(postMap);
        loadingBar.dismiss();
       Toast.makeText(getContext(),"Success",Toast.LENGTH_LONG).show();
    }
    private void retrieveStory() {
        Query sortLatestPost=FirebaseDatabase.getInstance().getReference().child("story").child(currentUserId);
        FirebaseRecyclerOptions<story> options=new FirebaseRecyclerOptions.Builder<story>().setQuery(sortLatestPost,story.class).build();
FirebaseRecyclerAdapter<story,storyHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<story, storyHolder>(options) {
    @Override
    protected void onBindViewHolder(@NonNull final storyHolder holder, int position, @NonNull story model) {
        Picasso.with(holder.itemView.getContext()).load(model.getImageurl()).into(holder.story_image_seen);
        Picasso.with(holder.itemView.getContext()).load(model.getImageurl()).into(holder.story_image_seen);
        long end=System.currentTimeMillis();

        holder.seenStory(holder,model.getUserid());

        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getUserid()).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.story_name.setText(snapshot.child("fullName").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(holder.itemView.getContext(),FullStoryActivity.class);
                i.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                holder.itemView.getContext().startActivity(i);

            }
        });

    }

    @NonNull
    @Override
    public storyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.story_layout,parent,false);
        return  new storyHolder(v);
    }
};
story_list.setAdapter(firebaseRecyclerAdapter);
firebaseRecyclerAdapter.startListening();

    }

public  static class   storyHolder extends RecyclerView.ViewHolder{
    View mView;
    CircleImageView story_image_seen,story_image;
    TextView story_name;
    public storyHolder(@NonNull View itemView) {
        super(itemView);
        mView=itemView;
         story_name=mView.findViewById(R.id.text_user_name);
        story_image_seen=mView.findViewById(R.id.add_story_image_seen);
        story_image=mView.findViewById(R.id.add_story_image);

    }
    void myStories(final TextView textView, final ImageView imageView, final boolean click)
    {
        FirebaseDatabase.getInstance().getReference().
                child("story").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int counter=0;
                        long currentTime=System.currentTimeMillis();
                        for(DataSnapshot d:snapshot.getChildren())
                        {
                            story storee=d.getValue(story.class);

                            if(currentTime>storee.getTimestart()&&currentTime<storee.getTimeend())
                            {
                                counter++;
                            }

                        }
                        if(click)
                        {
                            if(counter>0)
                            {
                                final Dialog dialog=new Dialog(itemView.getContext());
                                dialog.setContentView(R.layout.confirm_dialog);
                                dialog.setCancelable(true);
                                Button confirmDialogNoBtn=dialog.findViewById(R.id.confirmDialogNoBtn);
                                Button confirmDialogYesBtn=dialog.findViewById(R.id.confirmDialogYesBtn);
                                confirmDialogYesBtn.setText("View Story");
                                confirmDialogNoBtn.setText("Add Story");
                                confirmDialogYesBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent i=new Intent(itemView.getContext(),FullStoryActivity.class);
                                        i.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        itemView.getContext().startActivity(i);
                                        dialog.dismiss();
                                    }
                                });
                                confirmDialogNoBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i=new Intent(itemView.getContext(),AddStoryActivity.class);
                                        i.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        itemView.getContext().startActivity(i);
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();

                            }
                            else
                            {
                                Intent i=new Intent(itemView.getContext(),AddStoryActivity.class);
                                i.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                itemView.getContext().startActivity(i);

                            }

                        }
                        else
                        {
                            if(counter>0)
                            {
                                textView.setText("My story");
                                imageView.setVisibility(View.GONE);

                            }
                            else
                            {
                                textView.setText("Add story");
                                imageView.setVisibility(View.VISIBLE);

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    void seenStory(final storyHolder holder, String userId)
    {
        FirebaseDatabase.getInstance().getReference().child("story").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
                for(DataSnapshot d:snapshot.getChildren())
                {
                    if(!d.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()&&
                            System.currentTimeMillis()<d.getValue(story.class).getTimestart())
                    {
                        i=i+1;

                    }
                }
                if(i>0)
                {
                    holder.story_image.setVisibility(View.VISIBLE);
                    holder.story_image_seen.setVisibility(View.GONE);

                }
                else
                {
                    holder.story_image.setVisibility(View.GONE);
                    holder.story_image_seen.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {



            }
        });
    }
}



    private void displayAllUserPosts() {
        Query sortLatestPost=postRef.orderByChild("counter");
        FirebaseRecyclerOptions<Post> options=new FirebaseRecyclerOptions.Builder<Post>().setQuery(sortLatestPost,Post.class).build();

        FirebaseRecyclerAdapter<Post,postViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Post, postViewHolder>(
           options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull final postViewHolder holder, final int position, @NonNull final Post model) {


               final String postKey=getRef(position).getKey();
                holder.setFullName(model.getFullName());
                holder.setTime(model.getTime());
                holder.setDate(model.getDate());
                holder.setDescription(model.getDescription(),model.getFullName());
                holder.setProfileImage(getActivity(),model.getProfileImage(),model.getUid());
                holder.setPostImage(getActivity(),model.getPostImage());
                holder.setLikes(postKey);
                holder.checkSavedStatus(postKey,holder.savepost_btn);
                holder.likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeChecker=true;
                        likeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                             if(likeChecker.equals(true))
                             {
                                 if(snapshot.child(postKey).hasChild(currentUserId))
                                 {
                                     likeChecker=false;
                                     likeRef.child(postKey).child(currentUserId).removeValue();

                                 }
                                 else
                                 {
                                     likeChecker=false;
                                     likeRef.child(postKey).child(currentUserId).setValue(true);
                                 }
                             }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentsIntnent=new Intent(getActivity(),ImageViewActivity.class);
                        commentsIntnent.putExtra("postKey",postKey);
                        commentsIntnent.putExtra("comment","true");
                        commentsIntnent.putExtra("uid",model.getUid());
                    //    commentsIntnent.putExtra("cpost",model.getPostImage());
                     //   commentsIntnent.putExtra("cimage",model.getProfileImage());
                        startActivity(commentsIntnent);

                    }
                });
                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editChecker=true;
                        FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(editChecker.equals(true))
                                {
                                    editChecker=false;
                                    if(snapshot.exists())
                                    {
                                        final String  description=snapshot.child("description").getValue().toString();
                                        final String  dname=snapshot.child("fullName").getValue().toString();
                                        String  image=snapshot.child("postImage").getValue().toString();
                                        holder.setDescription(description,dname);
                                        holder.setPostImage(holder.itemView.getContext(),image);
                                        String  databaseUserId=snapshot.child("uid").getValue().toString();
                                        CharSequence options[];
                                        if(databaseUserId.equals(currentUserId))
                                        {
                                            options=new CharSequence[]{
                                                    "Edit Post",
                                                    "Delete Post",
                                                    "comment Post",
                                                    "like Post"};
                                            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                            builder.setTitle("select options");
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, final int which) {
                                                    switch (which)
                                                    {
                                                        case 0:
                                                            holder.editCurrentPost(description,postKey);
                                                            break;
                                                        case 1:
                                                            getRef(position).removeValue();
                                                            break;
                                                        case 2:

                                                            Intent commentsIntnent=new Intent(getActivity(),ImageViewActivity.class);
                                                            commentsIntnent.putExtra("postKey",postKey);
                                                            commentsIntnent.putExtra("comment","true");
                                                            commentsIntnent.putExtra("uid",model.getUid());

                                                            startActivity(commentsIntnent);
                                                            break;
                                                        case 3:
                                                            likeChecker=true;
                                                            likeRef.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                    if(likeChecker.equals(true))
                                                                    {
                                                                        if(snapshot.child(postKey).hasChild(currentUserId))
                                                                        {
                                                                            likeChecker=false;
                                                                            likeRef.child(postKey).child(currentUserId).removeValue();

                                                                        }
                                                                        else
                                                                        {
                                                                            likeChecker=false;
                                                                            likeRef.child(postKey).child(currentUserId).setValue(true);
                                                                        }
                                                                    }
                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                            break;
                                                        default:
                                                    }
                                                }
                                            });
                                            builder.show();
                                        }
                                        else
                                        {
                                            options=new CharSequence[]{
                                                    "show Profile",
                                                    "comment Post",
                                                    "like Post"};
                                            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                            builder.setTitle("select options");
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which)
                                                    {

                                                        case 0:
                                                            String visit_user_id=model.getUid();
                                                            Intent PersonI=new Intent(getActivity(),PersonActivity.class);
                                                            PersonI.putExtra("visit_user_id",visit_user_id);
                                                            startActivity(PersonI);
                                                            break;
                                                        case 1:

                                                            Intent commentsIntnent=new Intent(getActivity(),ImageViewActivity.class);
                                                            commentsIntnent.putExtra("postKey",postKey);
                                                            commentsIntnent.putExtra("comment","true");
                                                            commentsIntnent.putExtra("uid",model.getUid());
                                                            startActivity(commentsIntnent);
                                                            break;
                                                        case 2:
                                                            likeChecker=true;
                                                            likeRef.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                    if(likeChecker.equals(true))
                                                                    {
                                                                        if(snapshot.child(postKey).hasChild(currentUserId))
                                                                        {
                                                                            likeChecker=false;
                                                                            likeRef.child(postKey).child(currentUserId).removeValue();

                                                                        }
                                                                        else
                                                                        {
                                                                            likeChecker=false;
                                                                            likeRef.child(postKey).child(currentUserId).setValue(true);
                                                                        }
                                                                    }
                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                            break;
                                                        default:
                                                    }
                                                }
                                            });
                                            builder.show();
                                        }
                      }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                holder.savepost_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.savepost_btn.getTag().toString().equals("save"))
                        {
                            FirebaseDatabase.getInstance().getReference().child("Saved").child(currentUserId).child(postKey).setValue(model);
                        }
                        else
                        {
                            FirebaseDatabase.getInstance().getReference().child("Saved").child(currentUserId).child(postKey).removeValue();
                        }


                    }
                });


            }

            @NonNull
            @Override
            public postViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(getActivity()).inflate(R.layout.all_post_layout,parent,false);
                return  new postViewHolder(v);
            }
        };
        all_users_post_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    public static class postViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView no_of_likes;
        ImageButton likes,comment;
        int count_likes;
        DatabaseReference LikesRef;
        String currentUserId;
        ImageButton more;
        ImageButton savepost_btn;
        public postViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            no_of_likes=mView.findViewById(R.id.no_of_likes);
            more=mView.findViewById(R.id.moreBtn);
            likes=mView.findViewById(R.id.like);
            savepost_btn=mView.findViewById(R.id.savepost_btn);
            comment=mView.findViewById(R.id.comments);
            LikesRef= FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        }
        public void setFullName(String fullName) {
            TextView userName=mView.findViewById(R.id.post_user_name);
            userName.setText(fullName);
        }
        public void setProfileImage(Context ctx,String profileImage,final String visit_user_id) {
            CircleImageView image=mView.findViewById(R.id.post_profile_image);
            Picasso.with(ctx).load(profileImage).into(image);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent PersonI=new Intent(itemView.getContext(),PersonActivity.class);
                    PersonI.putExtra("visit_user_id",visit_user_id);
                    itemView.getContext().startActivity(PersonI);
                }
            });

        }
        public void setTime(String time) {
            TextView ptime=mView.findViewById(R.id.post_time);
            ptime.setText("   "+time);
        }
        public void setDate(String date) {
            TextView pdate=mView.findViewById(R.id.post_date);
            pdate.setText(date);
        }
        public void setDescription(String description,String name) {
            TextView pdis=mView.findViewById(R.id.click_post_description);
            TextView pname=mView.findViewById(R.id.click_post_des_name);
            pdis.setText(description);
            pname.setText(name);
        }
        public void setPostImage(Context ctx,String postImage) {
            ImageView image=mView.findViewById(R.id.click_post_image);
            Picasso.with(ctx).load(postImage).into(image);

        }
        public void setLikes(final  String PostKey) {

            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if(snapshot.child(PostKey).hasChild(currentUserId))
                    {

                        count_likes = (int) snapshot.child(PostKey).getChildrenCount();
                        likes.setImageResource(R.drawable.like);
                        no_of_likes.setText(count_likes +" Likes");
                    }
                    else
                    {

                        count_likes = (int) snapshot.child(PostKey).getChildrenCount();
                        likes.setImageResource(R.drawable.dislike);
                        no_of_likes.setText(count_likes +" Likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        private void editCurrentPost(String description,final String postKey) {
            androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Edit Post");
            final EditText inputField=new EditText(itemView.getContext());
            inputField.setText(description);
            builder.setView(inputField);
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("description").setValue(inputField.getText().toString());
                    Toast.makeText(itemView.getContext(),"Updated ",Toast.LENGTH_LONG).show();

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });
            Dialog dialog=builder.create();
            dialog.show();
        }

        public void checkSavedStatus(final String postKey, final ImageButton savepost_btn) {
         DatabaseReference saveRef= FirebaseDatabase.getInstance().getReference().child("Saved").
                 child(currentUserId);
         saveRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(snapshot.child(postKey).exists())
                 {
                     savepost_btn.setImageResource(R.drawable.ic_baseline_bookmark_24);
                     savepost_btn.setTag("saved");
                 }
                 else
                 {
                     savepost_btn.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                     savepost_btn.setTag("save");
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

        }
    }

}


/*
*     private void displayAllUserPosts() {
        Query sortLatestPost=postRef.orderByChild("counter");
        FirebaseRecyclerOptions<Post> options=new FirebaseRecyclerOptions.Builder<Post>().setQuery(sortLatestPost,Post.class).build();

        FirebaseRecyclerAdapter<Post,postViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Post, postViewHolder>(
           options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull final postViewHolder holder, final int position, @NonNull final Post model) {

               final String postKey=getRef(position).getKey();
                holder.setFullName(model.getFullName());
                holder.setTime(model.getTime());
                holder.setDate(model.getDate());
                holder.setDescription(model.getDescription(),model.getFullName());
                holder.setProfileImage(getActivity(),model.getProfileImage(),model.getUid());
                holder.setPostImage(getActivity(),model.getPostImage());
                holder.setLikes(postKey);
                holder.likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeChecker=true;
                        likeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                             if(likeChecker.equals(true))
                             {
                                 if(snapshot.child(postKey).hasChild(currentUserId))
                                 {
                                     likeChecker=false;
                                     likeRef.child(postKey).child(currentUserId).removeValue();

                                 }
                                 else
                                 {
                                     likeChecker=false;
                                     likeRef.child(postKey).child(currentUserId).setValue(true);
                                 }
                             }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentsIntnent=new Intent(getActivity(),CommentsActivity.class);
                        commentsIntnent.putExtra("postKey",postKey);
                        commentsIntnent.putExtra("cname",model.getFullName());
                        commentsIntnent.putExtra("cpost",model.getPostImage());
                        commentsIntnent.putExtra("cimage",model.getProfileImage());
                        startActivity(commentsIntnent);

                    }
                });
                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editChecker=true;
                        FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(editChecker.equals(true))
                                {
                                    editChecker=false;
                                    if(snapshot.exists())
                                    {
                                        final String  description=snapshot.child("description").getValue().toString();
                                        final String  dname=snapshot.child("fullName").getValue().toString();
                                        String  image=snapshot.child("postImage").getValue().toString();
                                        holder.setDescription(description,dname);
                                        holder.setPostImage(holder.itemView.getContext(),image);
                                        String  databaseUserId=snapshot.child("uid").getValue().toString();
                                        CharSequence options[];
                                        if(databaseUserId.equals(currentUserId))
                                        {
                                            options=new CharSequence[]{
                                                    "Edit Post",
                                                    "Delete Post",
                                                    "comment Post",
                                                    "like Post"};
                                            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                            builder.setTitle("select options");
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, final int which) {
                                                    switch (which)
                                                    {
                                                        case 0:
                                                            holder.editCurrentPost(description,postKey);
                                                            break;
                                                        case 1:
                                                            getRef(position).removeValue();
                                                            break;
                                                        case 2:

                                                            Intent commentsIntnent=new Intent(getActivity(),CommentsActivity.class);
                                                            commentsIntnent.putExtra("postKey",postKey);
                                                            commentsIntnent.putExtra("cname",model.getFullName());
                                                            commentsIntnent.putExtra("cpost",model.getPostImage());
                                                            commentsIntnent.putExtra("cimage",model.getProfileImage());
                                                            startActivity(commentsIntnent);
                                                            break;
                                                        case 3:
                                                            likeChecker=true;
                                                            likeRef.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                    if(likeChecker.equals(true))
                                                                    {
                                                                        if(snapshot.child(postKey).hasChild(currentUserId))
                                                                        {
                                                                            likeChecker=false;
                                                                            likeRef.child(postKey).child(currentUserId).removeValue();

                                                                        }
                                                                        else
                                                                        {
                                                                            likeChecker=false;
                                                                            likeRef.child(postKey).child(currentUserId).setValue(true);
                                                                        }
                                                                    }
                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                            break;
                                                        default:
                                                    }
                                                }
                                            });
                                            builder.show();
                                        }
                                        else
                                        {
                                            options=new CharSequence[]{
                                                    "show Profile",
                                                    "comment Post",
                                                    "like Post"};
                                            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                            builder.setTitle("select options");
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which)
                                                    {

                                                        case 0:
                                                            String visit_user_id=model.getUid();
                                                            Intent PersonI=new Intent(getActivity(),PersonActivity.class);
                                                            PersonI.putExtra("visit_user_id",visit_user_id);
                                                            startActivity(PersonI);
                                                            break;
                                                        case 1:

                                                            Intent commentsIntnent=new Intent(getActivity(),CommentsActivity.class);
                                                            commentsIntnent.putExtra("postKey",postKey);
                                                            commentsIntnent.putExtra("cname",model.getFullName());
                                                            commentsIntnent.putExtra("cpost",model.getPostImage());
                                                            commentsIntnent.putExtra("cimage",model.getProfileImage());
                                                            startActivity(commentsIntnent);
                                                            break;
                                                        case 2:
                                                            likeChecker=true;
                                                            likeRef.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                    if(likeChecker.equals(true))
                                                                    {
                                                                        if(snapshot.child(postKey).hasChild(currentUserId))
                                                                        {
                                                                            likeChecker=false;
                                                                            likeRef.child(postKey).child(currentUserId).removeValue();

                                                                        }
                                                                        else
                                                                        {
                                                                            likeChecker=false;
                                                                            likeRef.child(postKey).child(currentUserId).setValue(true);
                                                                        }
                                                                    }
                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                            break;
                                                        default:
                                                    }
                                                }
                                            });
                                            builder.show();
                                        }
                      }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });


            }

            @NonNull
            @Override
            public postViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(getActivity()).inflate(R.layout.all_post_layout,parent,false);
                return  new postViewHolder(v);
            }
        };
        all_users_post_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    public static class postViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView no_of_likes;
        ImageButton likes,comment;
        int count_likes;
        DatabaseReference LikesRef;
        String currentUserId;
        ImageButton more;
        public postViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            no_of_likes=mView.findViewById(R.id.no_of_likes);
            more=mView.findViewById(R.id.moreBtn);
            likes=mView.findViewById(R.id.like);
            comment=mView.findViewById(R.id.comments);
            LikesRef= FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        }
        public void setFullName(String fullName) {
            TextView userName=mView.findViewById(R.id.post_user_name);
            userName.setText(fullName);
        }
        public void setProfileImage(Context ctx,String profileImage,final String visit_user_id) {
            CircleImageView image=mView.findViewById(R.id.post_profile_image);
            Picasso.with(ctx).load(profileImage).into(image);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent PersonI=new Intent(itemView.getContext(),PersonActivity.class);
                    PersonI.putExtra("visit_user_id",visit_user_id);
                    itemView.getContext().startActivity(PersonI);
                }
            });

        }
        public void setTime(String time) {
            TextView ptime=mView.findViewById(R.id.post_time);
            ptime.setText("   "+time);
        }
        public void setDate(String date) {
            TextView pdate=mView.findViewById(R.id.post_date);
            pdate.setText(date);
        }
        public void setDescription(String description,String name) {
            TextView pdis=mView.findViewById(R.id.click_post_description);
            TextView pname=mView.findViewById(R.id.click_post_des_name);
            pdis.setText(description);
            pname.setText(name);
        }
        public void setPostImage(Context ctx,String postImage) {
            ImageView image=mView.findViewById(R.id.click_post_image);
            Picasso.with(ctx).load(postImage).into(image);

        }
        public void setLikes(final  String PostKey) {

            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if(snapshot.child(PostKey).hasChild(currentUserId))
                    {

                        count_likes = (int) snapshot.child(PostKey).getChildrenCount();
                        likes.setImageResource(R.drawable.like);
                        no_of_likes.setText(count_likes +" Likes");
                    }
                    else
                    {

                        count_likes = (int) snapshot.child(PostKey).getChildrenCount();
                        likes.setImageResource(R.drawable.dislike);
                        no_of_likes.setText(count_likes +" Likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        private void editCurrentPost(String description,final String postKey) {
            androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Edit Post");
            final EditText inputField=new EditText(itemView.getContext());
            inputField.setText(description);
            builder.setView(inputField);
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("description").setValue(inputField.getText().toString());
                    Toast.makeText(itemView.getContext(),"Updated ",Toast.LENGTH_LONG).show();

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });
            Dialog dialog=builder.create();
            dialog.show();
        }
    }*/