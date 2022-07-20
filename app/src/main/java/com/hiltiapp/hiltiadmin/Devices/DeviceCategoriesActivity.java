package com.hiltiapp.hiltiadmin.Devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hiltiapp.hiltiadmin.Model.Product;
import com.hiltiapp.hiltiadmin.R;
import com.hiltiapp.hiltiadmin.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class DeviceCategoriesActivity extends AppCompatActivity {

    private DatabaseReference  DeviceCategoriesRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private Button addNewCategoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_categories);

        DeviceCategoriesRef = FirebaseDatabase.getInstance().getReference().child("Device Categories");


        addNewCategoryBtn = findViewById(R.id.add_category_btn);
        addNewCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceCategoriesActivity.this, AddNewDeviceCategoryActivity.class);
                startActivity(intent);
            }
        });
        recyclerView = findViewById(R.id.devices_categories_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(DeviceCategoriesRef, Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
                        holder.productName.setText(model.getName());
                        Picasso.get().load(model.getImage()).into(holder.productImage);
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                deleteCategory(model.getId());
                                return true;
                            }
                        });
                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Intent intent = new Intent(DeviceCategoriesActivity.this, DeviceProductsActivity.class);
                               intent.putExtra("deviceCategoryId",model.getId());
                               intent.putExtra("deviceCategoryName",model.getName());
                               startActivity(intent);
                           }
                       });




                    }


                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteCategory(String categoryId) {
        CharSequence options[] = new CharSequence[]
                {
                        getString(R.string.ok),
                        getString(R.string.cancel)
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceCategoriesActivity.this);
        builder.setTitle(getString(R.string.alert_delete_title));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i)
            {
                if(i == 0)
                {
                   DeviceCategoriesRef.child(categoryId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Toast.makeText(DeviceCategoriesActivity.this,getString(R.string.toast_delete),Toast.LENGTH_SHORT).show();
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


}