package com.hiltiapp.hiltiadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

 import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiltiapp.hiltiadmin.Model.Admin;
import com.hiltiapp.hiltiadmin.Prevalent.Prevalent;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText InputAdminName, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private CheckBox chkBoxRememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Paper.init(this);

        String UserNameKey = Paper.book().read(Prevalent.AdminNameKey);
        String UserPasswordKey = Paper.book().read(Prevalent.AdminPasswordKey);

        if (!TextUtils.isEmpty(UserNameKey) && !TextUtils.isEmpty(UserPasswordKey))
        {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        InputAdminName = (EditText) findViewById(R.id.login_admin_name_input);



        loadingBar = new ProgressDialog(this);


        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);




        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginAdmin();
            }
        });
    }

    private void LoginAdmin()
    {
        String name = InputAdminName.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, getString(R.string.toast_login_add_name), Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, getString(R.string.toast_login_add_password), Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle(getString(R.string.loading_login));
            loadingBar.setMessage(getString(R.string.loading_login_message));
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            AllowAccessToAccount(name, password);
        }
    }

    private void AllowAccessToAccount(final String name, final String password)
    {

        if(chkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.AdminNameKey, name);
            Paper.book().write(Prevalent.AdminPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("Admins").child(name).exists())
                {
                    Admin adminData = dataSnapshot.child("Admins").child(name).getValue(Admin.class);

                    if (adminData.getName().equals(name))
                    {
                        if (adminData.getPassword().equals(password))
                        {
                            Toast.makeText(LoginActivity.this,getString(R.string.toast_login_success), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();


                            Prevalent.currentOnlineUser = adminData;
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, getString(R.string.toast_login_wrong_password), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, getString(R.string.toast_login_not_found), Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}