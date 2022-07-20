package com.hiltiapp.hiltiadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.hiltiapp.hiltiadmin.Model.Product;
import com.hiltiapp.hiltiadmin.Model.SliderImage;
import com.hiltiapp.hiltiadmin.Screens.AddNewScreenActivity;
import com.hiltiapp.hiltiadmin.Screens.ScreenBrandsActivity;
import com.hiltiapp.hiltiadmin.Screens.ScreensProductsActivity;
import com.hiltiapp.hiltiadmin.Screens.UpdateScreenProductActivity;
import com.hiltiapp.hiltiadmin.ViewHolder.ProductViewHolder;
import com.hiltiapp.hiltiadmin.ViewHolder.SliderViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SliderImagesActivity extends AppCompatActivity {

    private StorageReference SliderImagesRef;
    private DatabaseReference SlidersRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private String ImageRandomKey, saveCurrentDate, saveCurrentTime, downloadImageUrl;
    private Button AddImageBtn;
    private ImageView InputImage;

    private static final int GalleryPick = 1;
    private Uri ImageUri;


    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_images);
        SliderImagesRef = FirebaseStorage.getInstance().getReference().child("Slider Images");

        SlidersRef = FirebaseDatabase.getInstance().getReference().child("Slider Images");


        AddImageBtn = (Button) findViewById(R.id.save_image_btn);
        InputImage = (ImageView) findViewById(R.id.add_slider_image);


        loadingBar = new ProgressDialog(this);




        InputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });


        AddImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateProductData();
            }
        });



        recyclerView = findViewById(R.id.slider_images_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<SliderImage> options =
                new FirebaseRecyclerOptions.Builder<SliderImage>()
                        .setQuery(SlidersRef, SliderImage.class)
                        .build();

        FirebaseRecyclerAdapter<SliderImage, SliderViewHolder> adapter =
                new FirebaseRecyclerAdapter<SliderImage, SliderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@androidx.annotation.NonNull SliderViewHolder holder, int position, @androidx.annotation.NonNull SliderImage model) {
                        Picasso.get().load(model.getImage()).into(holder.sliderImage);
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                deleteImage(model.getId());
                                 return true;
                            }
                        });



                    }


                    @androidx.annotation.NonNull
                    @Override
                    public SliderViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item_layout, parent, false);
                        SliderViewHolder holder = new SliderViewHolder(view);
                        return holder;
                    }
                };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteImage(String imageId) {
        CharSequence options[] = new CharSequence[]
                {
                        getString(R.string.ok),
                        getString(R.string.cancel)
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(SliderImagesActivity.this);
        builder.setTitle(getString(R.string.alert_delete_title));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i)
            {
                if(i == 0)
                {
                    SlidersRef.child(imageId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Toast.makeText(SliderImagesActivity.this,getString(R.string.toast_delete),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if(i == 1)
                {

                }

            }
        });
        builder.show();

    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }


    private void ValidateProductData()
    {

        if (ImageUri == null)
        {
            Toast.makeText(this,getString(R.string.toast_add_image) , Toast.LENGTH_SHORT).show();
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

        ImageRandomKey = saveCurrentDate + saveCurrentTime;


        final StorageReference filePath = SliderImagesRef.child(ImageUri.getLastPathSegment() +ImageRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(SliderImagesActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
        itemMap.put("image", downloadImageUrl);
        itemMap.put("id", ImageRandomKey);



        SlidersRef.child(ImageRandomKey).updateChildren(itemMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            InputImage.setImageDrawable(getDrawable(R.drawable.ic_baseline_insert_photo_24));
                            Toast.makeText(SliderImagesActivity.this, getString(R.string.toast_add_image_successfully) , Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(SliderImagesActivity.this, getString(R.string.toast_add_product_fail) + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

}