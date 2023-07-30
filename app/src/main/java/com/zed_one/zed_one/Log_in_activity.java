package com.zed_one.zed_one;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class Log_in_activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email_EditText,password_EditText;
    private Button LogIn_btn;
    private ProgressBar progressBar;
    private TextView signin;

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
        setContentView(R.layout.activity_log_in);
        email_EditText= findViewById(R.id.login_email_EditText);
        password_EditText=findViewById(R.id.login_password_EditText);
        LogIn_btn=findViewById(R.id.login_btn);
        mAuth = FirebaseAuth.getInstance();
        progressBar= findViewById(R.id.login_progressbar);
        signin=findViewById(R.id.LogIn_textView);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Sign_in_activity.class);
                startActivity(intent);
                finish();
            }
        });

        LogIn_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        String email, password;
                        email=String.valueOf(email_EditText.getText());
                        password=String.valueOf(password_EditText.getText());
                        if(TextUtils.isEmpty(email))
                            Toast.makeText(Log_in_activity.this,"Enter Username", Toast.LENGTH_SHORT).show();

                        if(TextUtils.isEmpty(password))
                            Toast.makeText(Log_in_activity.this,"Enter Password", Toast.LENGTH_SHORT).show();


                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(Log_in_activity.this,"LOG IN", Toast.LENGTH_SHORT).show();
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // If sign in fails, display a message to the user.

                                            Toast.makeText(Log_in_activity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
        );
    }
}