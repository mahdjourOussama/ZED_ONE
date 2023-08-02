package com.zed_one.zed_one;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class Sign_in_activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase db =null;
    private EditText email_EditText,password_EditText,username_EditText;
    private Button SignIn_btn;
    private ProgressBar progressBar;
    private TextView login;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //  initialize the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();

        // fetching the UI elements
        email_EditText= findViewById(R.id.signin_email_EditText);
        username_EditText= findViewById(R.id.signin_username_EditText);
        password_EditText=findViewById(R.id.sign_password_EditText);
        SignIn_btn=findViewById(R.id.signin_btn);
        progressBar= findViewById(R.id.signin_progressbar);
        login=findViewById(R.id.LogIn_textView);

        // Setting Up Login Functions
        login.setOnClickListener( view -> {
            Intent intent = new Intent(getApplicationContext(), Log_in_activity.class);
            startActivity(intent);
            finish();
        });


        // Setting Up Sign Up Function
        SignIn_btn.setOnClickListener(
                view -> {
                    progressBar.setVisibility(View.VISIBLE);
                    String email, password, username;
                    email=String.valueOf(email_EditText.getText());
                    password=String.valueOf(password_EditText.getText());
                    username = String.valueOf(username_EditText.getText());

                    if(TextUtils.isEmpty(email))
                        Toast.makeText(Sign_in_activity.this,"Enter Email", Toast.LENGTH_SHORT).show();

                    else if(TextUtils.isEmpty(username))
                        Toast.makeText(Sign_in_activity.this,"Enter Username", Toast.LENGTH_SHORT).show();

                    else if(TextUtils.isEmpty(password))
                        Toast.makeText(Sign_in_activity.this,"Enter Password", Toast.LENGTH_SHORT).show();
                    else
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            // Handling database connection
                                            db = FirebaseDatabase.getInstance();
                                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                            String UID = firebaseUser.getUid();
                                            DatabaseReference reference= db.getReference();

                                            String userID;
                                            do {
                                                userID = generateRandomUserID(8); // Change the length as needed
                                            } while (isUserIDExistsInDatabase(userID));


                                            // Creating a  new user in the realtime DB
                                            User user= new User(username,userID,email);
                                            reference.child(userID).setValue(user);

                                            // Creating refrence to authentification with userID to account ID
                                            HashMap<String, String> userMap = new HashMap<>();
                                            userMap.put( userID,UID);
                                            reference.child("Users").setValue(userMap);

                                            progressBar.setVisibility(View.GONE);

                                            Toast.makeText(Sign_in_activity.this,"user created", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), Log_in_activity.class);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            progressBar.setVisibility(View.GONE);
                                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                            Toast.makeText(Sign_in_activity.this, "Authentication failed. "+email+"  "+password,
                                                    Toast.LENGTH_SHORT).show();
                                            if (task.getException() != null) {
                                                Log.e(TAG, "Exception: " + task.getException().getMessage());
                                            }
                                        }
                                    }
                                });
                }
        );
    }

    // Method to generate a random userID
    private String generateRandomUserID(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomID = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomID.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomID.toString();
    }

    private boolean user_exist;
    private boolean isUserIDExistsInDatabase(String userID) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference();
        usersRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If the dataSnapshot exists, it means the userID already exists in the database
                // Return true to indicate that the userID exists
                user_exist =dataSnapshot.exists();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur while accessing the database
                user_exist=false;
                Log.e(TAG, "Error checking userID existence: " + databaseError.getMessage());
            }
        });

        // Since Firebase queries are asynchronous, we cannot return the result directly here
        // Instead, return false as a default value and handle the result in onDataChange()
        return user_exist;
    }

}