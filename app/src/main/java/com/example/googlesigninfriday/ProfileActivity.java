package com.example.googlesigninfriday;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



@SuppressWarnings("ALL")
public class ProfileActivity extends AppCompatActivity  {
    LoginButton loginButton;
    CallbackManager callbackManager;
    private static final String TAG = "ProfileActivity";
    FirebaseAuth firebaseAuth;
    ImageView profileimage;
    TextView email,name,id;
    Button signoutbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();

        connextxml();


        loginButton.setReadPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handfacboologinwithfirebase(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

                updateUI(null);
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "facebook:onError"+ exception);

                updateUI(null);
            }
        });
        signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                LoginManager.getInstance().logOut();

                updateUI(null);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void handfacboologinwithfirebase(AccessToken accessToken) {


        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user =  firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {

                            Toast.makeText(ProfileActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
            id.setText(user.getUid());

            String imageure = user.getPhotoUrl().toString();
            if(user.getPhotoUrl().toString() == null)
            {
                Toast.makeText(this, "There is no url in image", Toast.LENGTH_SHORT).show();
            }else
            {
                Glide.with(getApplicationContext()).load(imageure).into(profileimage);
            }



            loginButton.setVisibility(View.GONE);
            signoutbtn.setVisibility(View.VISIBLE);
        } else {


            loginButton.setVisibility(View.VISIBLE);
            signoutbtn.setVisibility(View.GONE);
        }
    }



    private void connextxml() {
        loginButton = findViewById(R.id.login_button);
        profileimage = findViewById(R.id.profileImage);
        email =findViewById(R.id.email);
        name = findViewById(R.id.name);
        id = findViewById(R.id.userId);
        signoutbtn = findViewById(R.id.facesignout);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}