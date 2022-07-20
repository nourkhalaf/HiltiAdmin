package com.hiltiapp.hiltiadmin.WoodDesigns;

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

public class UpdateWoodProductActivity extends AppCompatActivity {

    private String woodProductId,woodCategoryId, Name, Code,Description , Price, ExtraPrice, BulkPrice;
    private Button UpdateProductButton;
    private ImageView UpdateImage;
    private EditText UpdateWoodProductName, UpdateWoodProductCode, UpdateWoodProductDescription,UpdateWoodProductPrice, UpdateWoodProductExtraPrice, UpdateWoodProductBulkPrice;


     private DatabaseReference WoodProductsRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_wood_product);


        woodProductId = getIntent().getStringExtra("woodProductId");
        woodCategoryId = getIntent().getStringExtra("woodCategoryId");


         WoodProductsRef = FirebaseDatabase.getInstance().getReference().child("Wood Products").child(woodCategoryId).child(woodProductId);


        UpdateProductButton = (Button) findViewById(R.id.save_update_wood_product_btn);
        UpdateImage = (ImageView) findViewById(R.id.update_wood_product_image);
        UpdateWoodProductName = (EditText) findViewById(R.id.update_wood_product_name);
        UpdateWoodProductCode = (EditText) findViewById(R.id.update_wood_product_code);
        UpdateWoodProductDescription = (EditText) findViewById(R.id.update_wood_product_description);
        UpdateWoodProductPrice = (EditText) findViewById(R.id.update_wood_product_price);
        UpdateWoodProductExtraPrice = (EditText) findViewById(R.id.update_wood_product_extra_price);
        UpdateWoodProductBulkPrice = (EditText) findViewById(R.id.update_wood_product_bulk_price);



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
        Name = UpdateWoodProductName.getText().toString();
        Code = UpdateWoodProductCode.getText().toString();
        Description = UpdateWoodProductDescription.getText().toString();
        Price = UpdateWoodProductPrice.getText().toString();
        ExtraPrice = UpdateWoodProductExtraPrice.getText().toString();
        BulkPrice = UpdateWoodProductBulkPrice.getText().toString();




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


        WoodProductsRef.updateChildren(itemMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(UpdateWoodProductActivity.this, getString(R.string.toast_update_product_successfully) , Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(UpdateWoodProductActivity.this, getString(R.string.toast_add_product_fail) + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void displaySpecificItemInfo() {

        WoodProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    String WoodName = snapshot.child("name").getValue().toString();
                    String WoodCode = snapshot.child("code").getValue().toString();
                    String WoodDescription = snapshot.child("description").getValue().toString();
                    String WoodPrice = snapshot.child("price").getValue().toString();
                    String WoodExtraPrice = snapshot.child("extraPrice").getValue().toString();
                    String WoodBulkPrice = snapshot.child("bulkPrice").getValue().toString();

                    String WoodImage = snapshot.child("image").getValue().toString();


                    UpdateWoodProductName.setText(WoodName);
                    UpdateWoodProductCode.setText(WoodCode);
                    UpdateWoodProductDescription.setText(WoodDescription);
                    UpdateWoodProductPrice.setText(WoodPrice);
                    UpdateWoodProductExtraPrice.setText(WoodExtraPrice);
                    UpdateWoodProductBulkPrice.setText(WoodBulkPrice);

                    Picasso.get().load(WoodImage).into(UpdateImage);


                 }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
