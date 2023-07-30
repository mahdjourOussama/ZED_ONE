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

public class Sign_in_activity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText email_EditText,password_EditText;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        email_EditText= findViewById(R.id.signin_email_EditText);
        password_EditText=findViewById(R.id.sign_password_EditText);
        SignIn_btn=findViewById(R.id.signin_btn);
        mAuth = FirebaseAuth.getInstance();
        progressBar= findViewById(R.id.signin_progressbar);
        login=findViewById(R.id.LogIn_textView);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Log_in_activity.class);
                startActivity(intent);
                finish();
            }
        });

        SignIn_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        String email, password;
                        email=String.valueOf(email_EditText.getText());
                        password=String.valueOf(password_EditText.getText());
                        if(TextUtils.isEmpty(email))
                            Toast.makeText(Sign_in_activity.this,"Enter Username", Toast.LENGTH_SHORT).show();

                        if(TextUtils.isEmpty(password))
                            Toast.makeText(Sign_in_activity.this,"Enter Password", Toast.LENGTH_SHORT).show();

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
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
                }
        );
    }

}