package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
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
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ProgressBar progressBar;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseAuth mAuth;
    DatabaseReference userRef;
    CircleImageView nav_profile_image;
    TextView nav_user_full_name;
    String currentUserId;
    FrameLayout frameLayout;
    ///confirmDialog
    Dialog confirmDialog;
    ProgressDialog loadingBar;
    TextView confirmDialogTitle;
    Button confirmDialogNo,confirmDialogYes;
    ///confirmDialog
    public static int currentfrag=-1;
    public  static int ADD_POST=3,PROFILE=6,HOME=0,FRIENDS=4,FIND_FRIENDS=5,MESSAGES=1,SETTINGS=7,MY_POST=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ///confirmDialog
        confirmDialog=new Dialog(MainActivity.this);
        confirmDialog.setContentView(R.layout.confirm_dialog);
        confirmDialog.setCancelable(true);
        confirmDialogNo=confirmDialog.findViewById(R.id.confirmDialogNoBtn);
        confirmDialogYes=confirmDialog.findViewById(R.id.confirmDialogYesBtn);
        confirmDialogTitle=confirmDialog.findViewById(R.id.confirmDialogTitle);
        confirmDialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(i);
                finish();

                confirmDialog.dismiss();
            }
        });
        confirmDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });
        ///confirmDialog



            toolbar=findViewById(R.id.main_page_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Home");




        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");



        frameLayout=findViewById(R.id.main_container);

        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.navigation_view);
        actionBarDrawerToggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.getMenu().getItem(0).setChecked(true);
        setfragment("home",new HomeFragment(),HOME);
        View navView=navigationView.getHeaderView(0);
        nav_profile_image=navView.findViewById(R.id.nav_profile_image);
        nav_user_full_name=navView.findViewById(R.id.nav_user_full_name);
        progressBar=navView.findViewById(R.id.progressBar);
        updateUserStatus("online");
        loadingBar=new ProgressDialog(MainActivity.this);
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.setTitle("Please wait");
        loadingBar.setMessage("working on it");
        loadingBar.show();
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {

                    if(dataSnapshot.hasChild("fullName"))
                   {
                       loadingBar.dismiss();
                       String  fullName=dataSnapshot.child("fullName").getValue().toString();
                       nav_user_full_name.setText(fullName);
                   }
                    else
                    {loadingBar.dismiss();
                        mT("profile name does not exits");
                    }
                    if(dataSnapshot.hasChild("profileImage"))
                    {loadingBar.dismiss();
                        String  image=dataSnapshot.child("profileImage").getValue().toString();
                        Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.find_people).into(nav_profile_image);
                        progressBar.setVisibility(View.GONE);
                    }
                    else
                    {loadingBar.dismiss();
                        mT("profile image does not exits");
                        progressBar.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();

            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               userSelectedItem(item);
                return true;
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);

        }
        else
        {
            if(currentfrag!=HOME)
            {
                setfragment("Home",new HomeFragment(),HOME);
            }
            else
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        if(item.getItemId()==R.id.menuAddPost) {
            setfragment("Add Post",new PostFragment(),ADD_POST);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public  void setfragment(String title,Fragment fragment,int prevFrag)
    {
        if(currentfrag!=prevFrag)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
            if(currentfrag!=-1)
                navigationView.getMenu().getItem(currentfrag).setChecked(false);
            navigationView.getMenu().getItem(prevFrag).setChecked(true);
            currentfrag=prevFrag;
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.fade_n,R.anim.fade_out).replace(frameLayout.getId(),fragment).commit();

        }
      }
////

//////

    void userSelectedItem(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.nav_profile:
                setfragment("Profile",new ProfileFragment(),PROFILE);
            //    Toast.makeText(MainActivity.this,"nav_profile",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_home:
                setfragment("Home",new HomeFragment(),HOME);
            //    Toast.makeText(MainActivity.this,"nav_home",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_friends:
                setfragment("Friends",new FriendsFragment(),FRIENDS);
               // Toast.makeText(MainActivity.this,"nav_friends",Toast.LENGTH_LONG).show();
                break;
           case R.id.nav_post:
            setfragment("Post",new PostFragment(),ADD_POST);
            break;
            case R.id.nav_my_post:
                setfragment("My Posts",new MyPostsFragment(),MY_POST);
             //   Toast.makeText(MainActivity.this,"nav_post",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_find_friends:
                setfragment("Find Freinds",new FindFriendsFragment(),FIND_FRIENDS);
               // Toast.makeText(MainActivity.this,"nav_find_friends",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_messages:
                setfragment("messgages",new FriendsFragment(),MESSAGES);
               // Toast.makeText(MainActivity.this,"nav_messages",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_settings:
                setfragment("settings",new SettingsFragment(),SETTINGS);
             //   Toast.makeText(MainActivity.this,"nav_settings",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_logout:
                updateUserStatus("offline");
                confirmDialog.show();
             //  Toast.makeText(MainActivity.this,"nav_logout",Toast.LENGTH_LONG).show();
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);


    }
    void mT(String s)
    {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserStatus("offline");
    }

    void  updateUserStatus(String state)
    {

        String savecurrentDate,savecurrentTime;
        Calendar calforDate=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("MMM dd, yyyy");
        savecurrentDate=currentdate.format(calforDate.getTime());

        Calendar calforTime=Calendar.getInstance();
        SimpleDateFormat currenttime=new SimpleDateFormat("hh:mm a");
        savecurrentTime=currenttime.format(calforTime.getTime());
        Map saveState =new HashMap();
        saveState.put("time",savecurrentTime);
        saveState.put("date",savecurrentDate);
        saveState.put("type",state);

        userRef.child(currentUserId).child("userState").updateChildren(saveState);
    }
}