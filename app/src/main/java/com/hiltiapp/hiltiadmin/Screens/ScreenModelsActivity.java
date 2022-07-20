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
import android.widget.ImageView;
import android.widget.TextView;
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
import com.hiltiapp.hiltiadmin.Model.Model;
import com.hiltiapp.hiltiadmin.Skins.SkinsProductsActivity;
import com.hiltiapp.hiltiadmin.R;
import com.hiltiapp.hiltiadmin.ViewHolder.BrandViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ScreenModelsActivity extends AppCompatActivity {
    private DatabaseReference ModelsRef;
     private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private EditText inputModelName;
    private Button addModel;
    private ProgressDialog loadingBar;
    private String brand,modelName, saveCurrentDate, saveCurrentTime, modelRandomKey;
    private TextView modelTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_models);

        loadingBar = new ProgressDialog(this);

        brand = getIntent().getStringExtra("Brand");

        ModelsRef = FirebaseDatabase.getInstance().getReference().child("Screen Models").child(brand);


         inputModelName = findViewById(R.id.input_model_name);
        addModel = findViewById(R.id.add_model_btn);

        modelTitle = findViewById(R.id.model_title);
        modelTitle.setText(brand);



        addModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddBrand();

            }
        });

        recyclerView = findViewById(R.id.models_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(ModelsRef, Model.class)
                        .build();

        FirebaseRecyclerAdapter<Model, BrandViewHolder> adapter =
                new FirebaseRecyclerAdapter<Model, BrandViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@androidx.annotation.NonNull BrandViewHolder holder, int position, @androidx.annotation.NonNull Model model) {
                        holder.brandName.setText(model.getName());
                         holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                deleteBrand(model.getId());
                                return true;
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



    private void deleteBrand(String modelId) {
        CharSequence options[] = new CharSequence[]
                {
                        getString(R.string.ok),
                        getString(R.string.cancel)
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(ScreenModelsActivity.this);
        builder.setTitle(getString(R.string.alert_delete_title));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i)
            {
                if(i == 0)
                {
                    ModelsRef.child(modelId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Toast.makeText(ScreenModelsActivity.this,getString(R.string.toast_delete),Toast.LENGTH_SHORT).show();
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
        modelName = inputModelName.getText().toString();


         if (TextUtils.isEmpty(modelName))
        {
            Toast.makeText(this, getString(R.string.toast_brand_name), Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle(getString(R.string.loading_add_model));
            loadingBar.setMessage(getString(R.string.loading_add_model_message));
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calendar.getTime());

            modelRandomKey = saveCurrentDate + saveCurrentTime;


            SaveToDatabase();
        }
    }


    private void SaveToDatabase()
    {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("id", modelRandomKey);
        productMap.put("name", modelName);





        ModelsRef.child(modelRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {


                            loadingBar.dismiss();
                            inputModelName.setText("");

                            Toast.makeText(ScreenModelsActivity.this,getString(R.string.toast_brand_added), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(ScreenModelsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
