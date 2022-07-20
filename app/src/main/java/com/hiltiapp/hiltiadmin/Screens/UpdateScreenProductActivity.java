package com.hiltiapp.hiltiadmin.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hiltiapp.hiltiadmin.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class UpdateScreenProductActivity extends AppCompatActivity {
    private String screenId, Name, Code,Description , Price, ExtraPrice, BulkPrice;
    private Button UpdateProductButton;
    private ImageView UpdateImage;
    private EditText UpdateScreenName, UpdateScreenCode, UpdateScreenDescription,UpdateScreenPrice, UpdateScreenExtraPrice, UpdateScreenBulkPrice;

    private DatabaseReference ScreensRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_screen_product);


        screenId = getIntent().getStringExtra("screenId");
        ScreensRef = FirebaseDatabase.getInstance().getReference().child("Screen Products").child(screenId);



        UpdateProductButton = (Button) findViewById(R.id.save_update_screen_btn);
        UpdateImage = (ImageView) findViewById(R.id.update_screen_image);
        UpdateScreenName = (EditText) findViewById(R.id.update_screen_name);
        UpdateScreenCode = (EditText) findViewById(R.id.update_screen_code);
        UpdateScreenDescription = (EditText) findViewById(R.id.update_screen_description);
        UpdateScreenPrice = (EditText) findViewById(R.id.update_screen_price);
        UpdateScreenExtraPrice = (EditText) findViewById(R.id.update_screen_extra_price);
        UpdateScreenBulkPrice = (EditText) findViewById(R.id.update_screen_bulk_price);



        displaySpecificItemInfo();


        loadingBar = new ProgressDialog(this);





        UpdateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateProductData();
            }
        });

    }






    private void ValidateProductData()
    {
        Name = UpdateScreenName.getText().toString();
        Code = UpdateScreenCode.getText().toString();
        Description = UpdateScreenDescription.getText().toString();
        Price = UpdateScreenPrice.getText().toString();
        ExtraPrice = UpdateScreenExtraPrice.getText().toString();
        BulkPrice = UpdateScreenBulkPrice.getText().toString();


         if (TextUtils.isEmpty(Name))
        {
            Toast.makeText(this,getString(R.string.toast_add_product_name) , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Code))
        {
            Toast.makeText(this,getString(R.string.toast_add_product_code) , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this,getString(R.string.toast_add_product_description) , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price))
        {
            Toast.makeText(this,getString(R.string.toast_add_product_price), Toast.LENGTH_SHORT).show();
        }
         else if (TextUtils.isEmpty(ExtraPrice))
         {
             Toast.makeText(this,getString(R.string.toast_add_product_extra_price), Toast.LENGTH_SHORT).show();
         }
         else if (TextUtils.isEmpty(BulkPrice))
         {
             Toast.makeText(this,getString(R.string.toast_add_product_bulk_price), Toast.LENGTH_SHORT).show();
         }
        else
        {
            StoreProductInformation();
        }
    }



    private void StoreProductInformation()
    {
        loadingBar.setTitle(getString(R.string.loading_update_product_title));
        loadingBar.setMessage(getString(R.string.loading_update_product_message) );
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        SaveProductInfoToDatabase();

    }

    private void SaveProductInfoToDatabase()
    {
        HashMap<String, Object> itemMap = new HashMap<>();
          itemMap.put("name", Name);
         itemMap.put("code", Code);
         itemMap.put("description", Description);
         itemMap.put("price", Price);
         itemMap.put("extraPrice", ExtraPrice);
        itemMap.put("bulkPrice", BulkPrice);


        ScreensRef.updateChildren(itemMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(UpdateScreenProductActivity.this, getString(R.string.toast_update_product_successfully) , Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(UpdateScreenProductActivity.this, getString(R.string.toast_add_product_fail) + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void displaySpecificItemInfo() {

        ScreensRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    String screenName = snapshot.child("name").getValue().toString();
                    String screenCode = snapshot.child("code").getValue().toString();
                    String screenDescription = snapshot.child("description").getValue().toString();
                    String screenPrice = snapshot.child("price").getValue().toString();
                    String screenExtraPrice = snapshot.child("extraPrice").getValue().toString();
                    String screenBulkPrice = snapshot.child("bulkPrice").getValue().toString();
                    String screenImage = snapshot.child("image").getValue().toString();


                    UpdateScreenName.setText(screenName);
                    UpdateScreenCode.setText(screenCode);
                    UpdateScreenDescription.setText(screenDescription);
                    UpdateScreenPrice.setText(screenPrice);
                    UpdateScreenExtraPrice.setText(screenExtraPrice);
                    UpdateScreenBulkPrice.setText(screenBulkPrice);

                    Picasso.get().load(screenImage).into(UpdateImage);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
