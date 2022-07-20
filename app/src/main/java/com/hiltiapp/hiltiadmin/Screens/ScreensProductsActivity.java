package com.hiltiapp.hiltiadmin.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ScreensProductsActivity extends AppCompatActivity {

    private DatabaseReference ScreensRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private Button addNewScreenBtn,brandBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screens_products);
        ScreensRef = FirebaseDatabase.getInstance().getReference().child("Screen Products");


        addNewScreenBtn = findViewById(R.id.add_screen_btn);
        addNewScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScreensProductsActivity.this,AddNewScreenActivity.class);
                startActivity(intent);
            }
        });

        brandBtn = findViewById(R.id.add_brand_btn);
        brandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScreensProductsActivity.this,ScreenBrandsActivity.class);
                 startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.products_list);
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
                        .setQuery(ScreensRef, Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@androidx.annotation.NonNull ProductViewHolder holder, int position, @androidx.annotation.NonNull Product model) {
                        holder.productName.setText(model.getName());
                        Picasso.get().load(model.getImage()).into(holder.productImage);
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                deleteProduct(model.getId());
                                return true;
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ScreensProductsActivity.this, UpdateScreenProductActivity.class);
                                intent.putExtra("screenId",model.getId());
                                startActivity(intent);

                            }
                        });


                    }


                    @androidx.annotation.NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteProduct(String productId) {
        CharSequence options[] = new CharSequence[]
                {
                        getString(R.string.ok),
                        getString(R.string.cancel)
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(ScreensProductsActivity.this);
        builder.setTitle(getString(R.string.alert_delete_title));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i)
            {
                if(i == 0)
                {
                    ScreensRef.child(productId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Toast.makeText(ScreensProductsActivity.this,getString(R.string.toast_delete),Toast.LENGTH_SHORT).show();
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