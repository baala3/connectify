package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    public static int SIGN_IN=0,SIGN_UP=1;
  public   static int currentFrag=0,prevFrag=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        frameLayout=findViewById(R.id.registerframelayout);
        setDefaultFragment(new SignInFragment(),-1,SIGN_IN);
    }

    private void setDefaultFragment(Fragment fragment,int PrevFrag,int CurrentFrag) {
        currentFrag=CurrentFrag;
        prevFrag=PrevFrag;
        getSupportFragmentManager().beginTransaction().replace(frameLayout.getId(),fragment).commit();
    }
    private void setFragment(Fragment fragment,int PrevFrag,int CurrentFrag) {
        currentFrag=CurrentFrag;
        prevFrag=PrevFrag;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_left,R.anim.slide_out_from_right).replace(frameLayout.getId(),fragment).commit();
    }

    @Override
    public void onBackPressed() {

        if(currentFrag==SIGN_UP)
        {
            setFragment(new SignInFragment(),SIGN_UP,SIGN_IN);
        }
        else
        {
            super.onBackPressed();
        }
    }
}