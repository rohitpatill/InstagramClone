package com.firstapp.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button register;
    private TextView loginUser;

     private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        loginUser = findViewById(R.id.loginuser);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);

        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textUsername = username.getText().toString();
                String  textName = name.getText().toString();
                String  textEmail = email.getText().toString();
                String  textPassword = password.getText().toString();

                if (TextUtils.isEmpty(textUsername) || TextUtils.isEmpty(textName)
                    || TextUtils.isEmpty(textEmail) || TextUtils.isEmpty(textPassword)){
                    Toast.makeText(RegisterActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                } else if (textPassword.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password to short!", Toast.LENGTH_SHORT).show();
                } else {
                   registerUser(textUsername , textName , textEmail , textPassword);
                }
            }
        });
    }

    private void registerUser(String username, String name, String email, String password) {
        pd.setMessage("Please wait");
        pd.show();
      mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
          @Override
          public void onSuccess(AuthResult authResult) {
              HashMap<String, String> map = new HashMap<>();
              map.put("name" , name);
              map.put("email" , email);
              map.put("username" , username);
              map.put("id", mAuth.getCurrentUser().getUid());
              map.put("bio" , "");
              map.put("imageurl", "default");

              mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()){
                          pd.dismiss();
                          Toast.makeText(RegisterActivity.this, "Update the profile" +
                                  "for better Experience", Toast.LENGTH_SHORT).show();

                          Intent intent = new Intent(RegisterActivity.this , MainActivity.class);
                          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                          startActivity(intent);
                          finish();
                      }
                  }
              });

          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
              pd.dismiss();
              Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
          }
      });
    }
}