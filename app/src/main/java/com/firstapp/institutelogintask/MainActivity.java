package com.firstapp.institutelogintask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_ONE_TAP = 101;
    private boolean showOneTapUI = true;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    SignInClient oneTapClient;
    BeginSignInRequest beginSignInRequest;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



            firebaseAuth=FirebaseAuth.getInstance();
            progressDialog=new ProgressDialog(this);
            progressDialog.setCancelable(false);

            oneTapClient = Identity.getSignInClient(this);
            beginSignInRequest = BeginSignInRequest.builder()
                    .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                            .setSupported(true)
                            .build())
                    .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            // Your server's client ID, not your Android client ID.
                            .setServerClientId(getString(R.string.default_web_client_id))
                            // Only show accounts previously used to sign in.
                            .setFilterByAuthorizedAccounts(false)
                            .build())
                    // Automatically sign in when exactly one credential is retrieved.
                    .setAutoSelectEnabled(true)
                    .build();
        }

        public void googlelogin11(View view) {
            progressDialog.show();
            progressDialog.setMessage("Google Login Please wait...");

            oneTapClient.beginSignIn(beginSignInRequest).addOnSuccessListener(new OnSuccessListener<BeginSignInResult>() {
                @Override
                public void onSuccess(BeginSignInResult beginSignInResult) {
                    progressDialog.dismiss();

                    try {
                        startIntentSenderForResult(
                                beginSignInResult.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e("TAG", "Couldn't start One Tap UI1: " + e.getLocalizedMessage());
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();

                    Log.e("TAG", "Couldn't start One Tap UI: " + e.getLocalizedMessage());


                }
            });
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case REQ_ONE_TAP:
                    try {
                        SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                        String idToken = credential.getGoogleIdToken();
                        String username = credential.getId();
                        String password = credential.getPassword();
                        if (idToken != null) {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            Log.d("TAG", "Got ID token.");

                            firebaseGoogleToken(idToken);

                        } else if (password != null) {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                            Log.d("TAG", "Got password.");
                        }
                    } catch (ApiException e) {
                        switch (e.getStatusCode()) {
                            case CommonStatusCodes.CANCELED:
                                Log.d("TAG", "One-tap dialog was closed.");
                                // Don't re-prompt the user.
                                showOneTapUI = false;
                                break;
                            case CommonStatusCodes.NETWORK_ERROR:
                                Log.d("TAG", "One-tap encountered a network error.");
                                // Try again or just ignore.
                                break;
                            default:
                                Log.d("TAG", "Couldn't get credential from result."
                                        + e.getLocalizedMessage());
                                break;
                        }
                        break;
                    }
            }
        }

        @Override
        protected void onStart() {
            super.onStart();
            FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
            if(firebaseUser!=null)
            {
                startActivity(new Intent(MainActivity.this,AddActivity.class));
                finish();
            }
            else
            {
                Toast.makeText(this, "Please Login", Toast.LENGTH_SHORT).show();
            }
        }

        private void firebaseGoogleToken(String idToken) {
            progressDialog.show();
            progressDialog.setMessage("got Gmail Token based on this we can push data to FirebaseAuth Users");

            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                startActivity(new Intent(MainActivity.this,AddActivity.class));
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG 8", "signInWithCredential:failure", task.getException());
                                //updateUI(null);
                            }
                        }
                    });
        }


}



