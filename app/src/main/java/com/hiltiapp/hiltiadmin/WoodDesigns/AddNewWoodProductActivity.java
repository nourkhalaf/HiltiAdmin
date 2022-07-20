package com.hiltiapp.hiltiadmin.WoodDesigns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hiltiapp.hiltiadmin.R;
import com.hiltiapp.hiltiadmin.Screens.AddNewScreenActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddNewWoodProductActivity extends AppCompatActivity {
    private String categoryId, Name, Code,Description , Price, ExtraPrice, BulkPrice;
    private String productRandomKey, saveCurrentDate, saveCurrentTime, downloadImageUrl;
    private Button AddNewProductButton;
    private ImageView InputImage;
    private EditText InputWoodProductName, InputWoodProductCode, InputWoodProductDescription,InputWoodProductPrice, InputWoodProductExtraPrice,  InputWoodProductBulkPrice;

    private static final int GalleryPick = 1;
    private Uri ImageUri;

    private StorageReference WoodProductsImagesRef;
    private DatabaseReference WoodProductsRef;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_wood_product);

        categoryId = getIntent().getStringExtra("woodCategoryId");



        WoodProductsImagesRef = FirebaseStorage.getInstance().getReference().child("Wood Products Images");
        WoodProductsRef = FirebaseDatabase.getInstance().getReference().child("Wood Products").child(categoryId);


        AddNewProductButton = (Button) findViewById(R.id.save_new_wood_product_btn);
        InputImage = (ImageView) findViewById(R.id.add_new_wood_product_image);
        InputWoodProductName = (EditText) findViewById(R.id.add_new_wood_product_name);
        InputWoodProductCode = (EditText) findViewById(R.id.add_new_wood_product_code);
        InputWoodProductDescription = (EditText) findViewById(R.id.add_new_wood_product_description);
        InputWoodProductPrice = (EditText) findViewById(R.id.add_new_wood_product_price);
        InputWoodProductExtraPrice = (EditText) findViewById(R.id.add_new_wood_product_extra_price);
        InputWoodProductBulkPrice = (EditText) findViewById(R.id.add_new_wood_product_bulk_price);



        loadingBar = new ProgressDialog(this);


        InputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });


        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateProductData();
            }
        });

    }



    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            InputImage.setImageURI(ImageUri);
        }
    }


    private void ValidateProductData()
    {
        Name = InputWoodProductName.getText().toString();
        Code = InputWoodProductCode.getText().toString();
        Description = InputWoodProductDescription.getText().toString();
        Price = InputWoodProductPrice.getText().toString();
        ExtraPrice = InputWoodProductExtraPrice.getText().toString();
        BulkPrice = InputWoodProductBulkPrice.getText().toString();




        if (ImageUri == null)
        {
            Toast.makeText(this,getString(R.string.toast_add_product_image) , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Name))
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
        loadingBar.setTitle(getString(R.string.loading_add_product_title));
        loadingBar.setMessage(getString(R.string.loading_add_product_message) );
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;


        final StorageReference filePath = WoodProductsImagesRef.child(ImageUri.getLastPathSegment() +productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AddNewWoodProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }



    private void SaveProductInfoToDatabase()
    {
        HashMap<String, Object> itemMap = new HashMap<>();
        itemMap.put("id", productRandomKey);
        itemMap.put("image", downloadImageUrl);
         itemMap.put("name", Name);
        itemMap.put("code", Code);
        itemMap.put("description", Description);
        itemMap.put("price", Price);
        itemMap.put("extraPrice", ExtraPrice);
        itemMap.put("bulkPrice", BulkPrice);


        WoodProductsRef.child(productRandomKey).updateChildren(itemMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            InputImage.setImageDrawable(getDrawable(R.drawable.ic_baseline_insert_photo_24));
                            InputWoodProductName.setText("");
                            InputWoodProductCode.setText("");
                            InputWoodProductDescription.setText("");
                            InputWoodProductPrice.setText("");
                            InputWoodProductExtraPrice.setText("");
                            InputWoodProductBulkPrice.setText("");


                            Toast.makeText(AddNewWoodProductActivity.this, getString(R.string.toast_add_product_successfully) , Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddNewWoodProductActivity.this, getString(R.string.toast_add_product_fail) + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
