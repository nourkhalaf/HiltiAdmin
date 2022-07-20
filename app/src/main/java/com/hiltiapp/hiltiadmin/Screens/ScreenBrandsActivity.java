package com.hiltiapp.hiltiadmin.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.hiltiapp.hiltiadmin.Model.Brand;
import com.hiltiapp.hiltiadmin.R;
import com.hiltiapp.hiltiadmin.ViewHolder.BrandViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ScreenBrandsActivity extends AppCompatActivity {

    private DatabaseReference BrandsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

     private EditText inputBrandName;
    private Button addBrand;
    private ProgressDialog loadingBar;
    private String brandName, saveCurrentDate, saveCurrentTime, brandRandomKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_brands);
        loadingBar = new ProgressDialog(this);


        BrandsRef = FirebaseDatabase.getInstance().getReference().child("Screen Brands");


        inputBrandName = findViewById(R.id.input_brand_name);
        addBrand = findViewById(R.id.add_brand_btn);




        addBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddBrand();

            }
        });

        recyclerView = findViewById(R.id.brands_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }


    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Brand> options =
                new FirebaseRecyclerOptions.Builder<Brand>()
                        .setQuery(BrandsRef, Brand.class)
                        .build();

        FirebaseRecyclerAdapter<Brand, BrandViewHolder> adapter =
                new FirebaseRecyclerAdapter<Brand, BrandViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@androidx.annotation.NonNull BrandViewHolder holder, int position, @androidx.annotation.NonNull Brand model) {
                        holder.brandName.setText(model.getName());
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                deleteBrand(model.getId());
                                return true;
                            }
                        });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ScreenBrandsActivity.this, ScreenModelsActivity.class );
                                intent.putExtra("Brand",model.getName());
                                startActivity(intent);

                            }
                        });


                    }


                    @androidx.annotation.NonNull
                    @Override
                    public BrandViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType)
                    {
                        android.view.View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brand_layout, parent, false);
                        BrandViewHolder holder = new BrandViewHolder(view);
                        return holder;
                    }
                };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }



    private void deleteBrand(String brandId) {
        CharSequence options[] = new CharSequence[]
                {
                        getString(R.string.ok),
                        getString(R.string.cancel)
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(ScreenBrandsActivity.this);
        builder.setTitle(getString(R.string.alert_delete_title));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i)
            {
                if(i == 0)
                {
                    BrandsRef.child(brandId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Toast.makeText(ScreenBrandsActivity.this,getString(R.string.toast_delete),Toast.LENGTH_SHORT).show();
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




    private void AddBrand()
    {
        brandName = inputBrandName.getText().toString();


       if (TextUtils.isEmpty(brandName))
        {
            Toast.makeText(this, getString(R.string.toast_brand_name), Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle(getString(R.string.loading_add_brand));
            loadingBar.setMessage(getString(R.string.loading_add_brand_message));
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calendar.getTime());

            brandRandomKey = saveCurrentDate + saveCurrentTime;

            SaveToDatabase();

        }
    }


    private void SaveToDatabase()
    {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("id", brandRandomKey);
        productMap.put("name", brandName);





        BrandsRef.child(brandRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {


                            loadingBar.dismiss();
                            inputBrandName.setText("");

                            Toast.makeText(ScreenBrandsActivity.this,getString(R.string.toast_brand_added), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(ScreenBrandsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}