package com.example.suitcase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button userLoginButton;
    private EditText inputuemail, inputupassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public void goToSignUp(View view) {
        // Handle the click event for "Do not have an account? Sign Up"
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputuemail = findViewById(R.id.userEmail);
        inputupassword = findViewById(R.id.userPassword);
        progressBar = findViewById(R.id.progressBar);
        userLoginButton = findViewById(R.id.userLoginButton);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Set onClickListener for the login button
        userLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the login action here
                performLogin();
            }
        });
    }

    private void performLogin() {
        String email = inputuemail.getText().toString();
        String password = inputupassword.getText().toString();

        if (!email.matches("[a-zA-Z0-9._-]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}")) {
            inputuemail.setError("Enter Correct Email");
        } else if (password.isEmpty() || password.length() < 6) {
            inputupassword.setError("Enter Proper Password");
        } else {
            progressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.INVISIBLE);

                    if (task.isSuccessful()) {
                        // Get the logged-in user's email
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userEmail = user.getEmail();
                            Toast.makeText(LoginActivity.this, "Login Successful\nLogged in as: " + userEmail, Toast.LENGTH_SHORT).show();
                        }

                        sendUserToNextActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    }


