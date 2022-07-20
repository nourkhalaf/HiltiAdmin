package com.hiltiapp.hiltiadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hiltiapp.hiltiadmin.Model.Product;
import com.hiltiapp.hiltiadmin.ViewHolder.CartViewHolder;

public class UserCartListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListRef;

    private String orderID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cart_list);
        orderID = getIntent().getStringExtra("id");

        recyclerView = findViewById(R.id.order_products_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child(orderID)
                .child("Admin View")
                .child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(cartListRef,Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull Product model)
                    {
                        holder.productName.setText(model.getName());
                        holder.productTotalPrice.setText(model.getTotalPrice());

                         holder.productCode.setText(getString(R.string.product_details_code) +model.getCode());

                        if (!TextUtils.isEmpty(model.getBrand()) && !TextUtils.isEmpty(model.getModel()))
                        {
                        holder.productBrand.setText(model.getBrand());
                        holder.productModel.setText(model.getModel());
                        }
                        if (!TextUtils.isEmpty(model.getCategory()))
                        {
                            holder.productCategory.setText(model.getCategory());

                        }
                        if (!TextUtils.isEmpty(model.getNotes()))
                        {
                            holder.productNotes.setText(model.getNotes());
                        }
                     }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}