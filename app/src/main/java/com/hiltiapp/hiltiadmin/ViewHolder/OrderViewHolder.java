package com.hiltiapp.hiltiadmin.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hiltiapp.hiltiadmin.Interface.ItemClickListener;
import com.hiltiapp.hiltiadmin.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public ItemClickListener listener;


    public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress, userShippingLocation;
    public Button showOrdersBtn;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        userName = itemView.findViewById(R.id.order_user_name);
        userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
        userTotalPrice = itemView.findViewById(R.id.order_total_price);
        userDateTime = itemView.findViewById(R.id.order_date_time);
        userShippingLocation = itemView.findViewById(R.id.order_shipping_location);
        userShippingAddress = itemView.findViewById(R.id.order_address_city);
        showOrdersBtn = itemView.findViewById(R.id.show_all_products_btn);
    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view)
    {
        listener.onClick(view, getAdapterPosition(), false);
    }
}

