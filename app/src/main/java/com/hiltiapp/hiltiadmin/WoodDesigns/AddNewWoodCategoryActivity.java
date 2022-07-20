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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddNewWoodCategoryActivity extends AppCompatActivity {
    private String Name;
    private String categoryRandomKey, saveCurrentDate, saveCurrentTime, downloadImageUrl;
    private Button AddNewCategoryButton;
    private ImageView InputImage;
    private EditText InputCategoryName;

    private static final int GalleryPick = 1;
    private Uri ImageUri;

    private StorageReference WoodCategoriesImagesRef;
    private DatabaseReference WoodCategoriesRef;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_wood_category);


        WoodCategoriesImagesRef = FirebaseStorage.getInstance().getReference().child("Wood Categories Images");
        WoodCategoriesRef = FirebaseDatabase.getInstance().getReference().child("Wood Categories");


        AddNewCategoryButton = (Button) findViewById(R.id.save_new_wood_category_btn);
        InputImage = (ImageView) findViewById(R.id.add_new_wood_category_image);
        InputCategoryName = (EditText) findViewById(R.id.add_new_wood_category_name);


        loadingBar = new ProgressDialog(this);


        InputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });


        AddNewCategoryButton.setOnClickListener(new View.OnClickListener() {
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
        Name = InputCategoryName.getText().toString();

        if (ImageUri == null)
        {
            Toast.makeText(this,getString(R.string.toast_add_product_image) , Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Name))
        {
            Toast.makeText(this,getString(R.string.toast_add_product_name) , Toast.LENGTH_SHORT).show();
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

        categoryRandomKey = saveCurrentDate + saveCurrentTime;


        final StorageReference filePath = WoodCategoriesImagesRef.child(ImageUri.getLastPathSegment() +categoryRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AddNewWoodCategoryActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
        itemMap.put("id", categoryRandomKey);
        itemMap.put("image", downloadImageUrl);
        itemMap.put("name", Name);

        WoodCategoriesRef.child(categoryRandomKey).updateChildren(itemMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            InputImage.setImageDrawable(getDrawable(R.drawable.ic_baseline_insert_photo_24));
                            InputCategoryName.setText("");


                            Toast.makeText(AddNewWoodCategoryActivity.this, getString(R.string.toast_add_product_successfully) , Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddNewWoodCategoryActivity.this, getString(R.string.toast_add_product_fail) + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
