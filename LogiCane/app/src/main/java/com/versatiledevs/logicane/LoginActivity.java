/*
    Android app is developed by VersatileDevs
        LUis Chunga
        Sina Serati
        Jordan Dowling
        PJ
        Geo

        Description : This is the login activity that allows only authorized users

        Latest update on Oct/6/2016

 */

package com.versatiledevs.logicane;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

import static android.R.attr.data;
import static android.R.attr.value;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignIN;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    public static int admin = 1;
    public static int patient = 2;
    public static int therapist = 3;
    public static int role = 0;


    // This fuction is called when the App is opened, aka its the apps constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            //PJ: I commented this code out because if I was already logged in
            // it would take me back to this activity which is not what we want.
            //However, with it commented out it takes us back to the login screen which I think
            // is more appropriate.

            /*startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();*/
        }
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void loginPublic(View view){
        login();
    }



    // this method will log user into firebase
    private void login() {



        editTextEmail = (EditText) findViewById(R.id.edit_email_text);
        editTextPassword = (EditText) findViewById(R.id.edit_password_text);
        buttonSignIN = (Button) findViewById(R.id.sign_in_button);
        progressDialog = new ProgressDialog(this);
        String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging in....");
        progressDialog.show();

        //authenticate user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            // there was an error
                            if (password.length() < 6) {
                                editTextPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            // MainActivity = sign out
                            Toast.makeText(LoginActivity.this, "WELCOME!", Toast.LENGTH_SHORT).show();

                            /************************
                             * This is the beginning of getting the role objects value from the Database.
                             * I'm currently able to get the information from the database but
                             * I'm unable to get the true value on the if statement below.
                             */
                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                            //Is this NULL?
                            String uid = user.getUid();

                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users").child(uid);
                            DatabaseReference fName = myRef.child("role");
                            fName.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    String temp = new String(dataSnapshot.getValue(String.class));
                                    if(temp.equals("admin"))
                                    {
                                        LoginActivity.role = LoginActivity.admin;
                                    }
                                    //Log.d("Role:",role);
                                    //Log.d("Admin?: ", admin);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            //Log.d("Role1",role);
                            //Log.d("Admin1", admin);
                            /*
                            *The if statement below is not producing the correct true value.
                            * I'm still trying to figure this out.
                            *
                             */
                            if (LoginActivity.role == LoginActivity.admin) //if user == admin go to admin activity
                            {
                                Intent intent = new Intent(LoginActivity.this, Admin.class);
                                startActivity(intent);
                                finish();

                            }else if(false) //User is a patient
                            {

                            }else //User is a doctor.
                            {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }



                        }
                    }
                });
    }


}
