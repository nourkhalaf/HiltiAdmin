package com.hiltiapp.hiltiadmin.Devices;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiltiapp.hiltiadmin.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UpdateDeviceProductActivity extends AppCompatActivity {

    private String deviceProductId,deviceCategoryId, Name, Code,Description , Price, ExtraPrice, BulkPrice;
    private Button UpdateProductButton;
    private ImageView UpdateImage;
    private EditText UpdateDeviceProductName, UpdateDeviceProductCode, UpdateDeviceProductDescription,UpdateDeviceProductPrice, UpdateDeviceProductExtraPrice, UpdateDeviceProductBulkPrice;


     private DatabaseReference DeviceProductsRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_wood_product);


        deviceProductId = getIntent().getStringExtra("deviceProductId");
        deviceCategoryId = getIntent().getStringExtra("deviceCategoryId");


         DeviceProductsRef = FirebaseDatabase.getInstance().getReference().child("Device Products").child(deviceCategoryId).child(deviceProductId);


        UpdateProductButton = (Button) findViewById(R.id.save_update_wood_product_btn);
        UpdateImage = (ImageView) findViewById(R.id.update_wood_product_image);
        UpdateDeviceProductName = (EditText) findViewById(R.id.update_wood_product_name);
        UpdateDeviceProductCode = (EditText) findViewById(R.id.update_wood_product_code);
        UpdateDeviceProductDescription = (EditText) findViewById(R.id.update_wood_product_description);
        UpdateDeviceProductPrice = (EditText) findViewById(R.id.update_wood_product_price);
        UpdateDeviceProductExtraPrice = (EditText) findViewById(R.id.update_wood_product_extra_price);
        UpdateDeviceProductBulkPrice = (EditText) findViewById(R.id.update_wood_product_bulk_price);



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
        Name = UpdateDeviceProductName.getText().toString();
        Code = UpdateDeviceProductCode.getText().toString();
        Description = UpdateDeviceProductDescription.getText().toString();
        Price = UpdateDeviceProductPrice.getText().toString();
        ExtraPrice = UpdateDeviceProductExtraPrice.getText().toString();
        BulkPrice = UpdateDeviceProductBulkPrice.getText().toString();




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


        DeviceProductsRef.updateChildren(itemMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(UpdateDeviceProductActivity.this, getString(R.string.toast_update_product_successfully) , Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(UpdateDeviceProductActivity.this, getString(R.string.toast_add_product_fail) + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void displaySpecificItemInfo() {

        DeviceProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    String DeviceName = snapshot.child("name").getValue().toString();
                    String DeviceCode = snapshot.child("code").getValue().toString();
                    String DeviceDescription = snapshot.child("description").getValue().toString();
                    String DevicePrice = snapshot.child("price").getValue().toString();
                    String DeviceExtraPrice = snapshot.child("extraPrice").getValue().toString();
                    String DeviceBulkPrice = snapshot.child("bulkPrice").getValue().toString();

                    String WoodImage = snapshot.child("image").getValue().toString();


                    UpdateDeviceProductName.setText(DeviceName);
                    UpdateDeviceProductCode.setText(DeviceCode);
                    UpdateDeviceProductDescription.setText(DeviceDescription);
                    UpdateDeviceProductPrice.setText(DevicePrice);
                    UpdateDeviceProductExtraPrice.setText(DeviceExtraPrice);
                    UpdateDeviceProductBulkPrice.setText(DeviceBulkPrice);

                    Picasso.get().load(WoodImage).into(UpdateImage);


                 }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
