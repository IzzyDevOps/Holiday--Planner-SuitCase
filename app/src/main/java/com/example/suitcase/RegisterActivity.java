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

public class RegisterActivity extends AppCompatActivity {

    //initialized all the variables
    EditText inputufirstname, inputulastname, inputuemail, inputupassword, inputuconfpassword, inputucontactno;
    Button btnRegister;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}";
    //ProgressDialog progressDialog;


    // ProgressBar declaration
    ProgressBar progressBar;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    public void goToLogin(View view) {
        // Handle the click event for the "Login" link
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputufirstname=findViewById(R.id.userFirstName);
        inputulastname=findViewById(R.id.userLastName);
        inputuemail=findViewById(R.id.userEmailAddress);
        inputupassword=findViewById(R.id.userPassword);
        inputuconfpassword=findViewById(R.id.userConfirmPassword);
        inputucontactno=findViewById(R.id.userContactNumber);
        btnRegister=findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerforAuth();

            }

            private void PerforAuth() {
                String firstname=inputufirstname.getText().toString();
                String lastname=inputulastname.getText().toString();
                String email=inputuemail.getText().toString();
                String password=inputupassword.getText().toString();
                String comfirmpassword=inputuconfpassword.getText().toString();
                String contactno=inputucontactno.getText().toString();

                if (!email.matches(emailPattern))
                {
                    inputuemail.setError("Enter Correct Email");
                } else if (password.isEmpty() || password.length()<6)
                {
                    inputupassword.setError("Enter Proper Password");
                } else if (!password.equals(comfirmpassword))
                {
                    inputuconfpassword.setError("Password Not match Both field");
                }else
                {
                    // Create an instance of the User class and populate it with user input data
                    User newUser = new User();
                    newUser.setFirstName(firstname);
                    newUser.setLastName(lastname);
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    newUser.setContactNumber(contactno);

                    // Show ProgressBar while performing authentication
                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.INVISIBLE);

                            if (task.isSuccessful()) {
                                // Registration successful, you can use newUser object as needed
                                sendUserToNextActivity();
                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                // Registration failed, handle the error
                                Toast.makeText(RegisterActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        private void sendUserToNextActivity() {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
}
