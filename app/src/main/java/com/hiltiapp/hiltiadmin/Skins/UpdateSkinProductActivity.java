package com.hiltiapp.hiltiadmin.Skins;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;

public class UpdateSkinProductActivity extends AppCompatActivity {
    private String skinId, modelId, Name, Code,Description , Price, ExtraPrice, BulkPrice;
    private Button UpdateProductButton;
    private ImageView UpdateImage;
    private EditText UpdateSkinName, UpdateSkinCode, UpdateSkinDescription, UpdateSkinPrice, UpdateSkinExtraPrice, UpdateSkinBulkPrice;


     private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_skin_product);


        skinId = getIntent().getStringExtra("skinId");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Skin Products").child(skinId);


        UpdateProductButton = (Button) findViewById(R.id.save_update_skin_btn);
        UpdateImage = (ImageView) findViewById(R.id.update_skin_image);
        UpdateSkinName = (EditText) findViewById(R.id.update_skin_name);
        UpdateSkinCode = (EditText) findViewById(R.id.update_skin_code);
        UpdateSkinDescription = (EditText) findViewById(R.id.update_skin_description);
        UpdateSkinPrice = (EditText) findViewById(R.id.update_skin_price);
        UpdateSkinExtraPrice = (EditText) findViewById(R.id.update_skin_extra_price);
        UpdateSkinBulkPrice = (EditText) findViewById(R.id.update_skin_bulk_price);



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
        Name = UpdateSkinName.getText().toString();
        Code = UpdateSkinCode.getText().toString();
        Description = UpdateSkinDescription.getText().toString();
        Price = UpdateSkinPrice.getText().toString();
        ExtraPrice = UpdateSkinExtraPrice.getText().toString();
        BulkPrice = UpdateSkinBulkPrice.getText().toString();


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


        ProductsRef.updateChildren(itemMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(UpdateSkinProductActivity.this, getString(R.string.toast_update_product_successfully) , Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(UpdateSkinProductActivity.this, getString(R.string.toast_add_product_fail) + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void displaySpecificItemInfo() {

        ProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    String skinName = snapshot.child("name").getValue().toString();
                    String skinCode = snapshot.child("code").getValue().toString();
                    String skinDescription = snapshot.child("description").getValue().toString();
                    String skinPrice = snapshot.child("price").getValue().toString();
                    String skinExtraPrice = snapshot.child("extraPrice").getValue().toString();
                    String skinBulkPrice = snapshot.child("bulkPrice").getValue().toString();
                    String skinImage = snapshot.child("image").getValue().toString();


                    UpdateSkinName.setText(skinName);
                    UpdateSkinCode.setText(skinCode);
                    UpdateSkinDescription.setText(skinDescription);
                    UpdateSkinPrice.setText(skinPrice);
                    UpdateSkinExtraPrice.setText(skinExtraPrice);
                    UpdateSkinBulkPrice.setText(skinBulkPrice);

                    Picasso.get().load(skinImage).into(UpdateImage);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
