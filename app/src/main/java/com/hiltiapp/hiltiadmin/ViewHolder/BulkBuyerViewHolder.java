package com.hiltiapp.hiltiadmin.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hiltiapp.hiltiadmin.Interface.ItemClickListener;
import com.hiltiapp.hiltiadmin.R;

public class BulkBuyerViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {


    public ItemClickListener listener;
    public TextView buyerName, buyerPhone, buyerEmail , buyerAddress, buyerShop, buyerState;

    public Button activateBtn, deleteBtn;

    public BulkBuyerViewHolder(@NonNull View itemView) {
        super(itemView);
        buyerName = itemView.findViewById(R.id.buyer_name);
        buyerPhone = itemView.findViewById(R.id.buyer_phone_number);
        buyerEmail = itemView.findViewById(R.id.buyer_email);
        buyerAddress = itemView.findViewById(R.id.buyer_address);
        buyerShop = itemView.findViewById(R.id.buyer_shop);
        activateBtn = itemView.findViewById(R.id.activate_buyer_btn);
        deleteBtn = itemView.findViewById(R.id.delete_buyer_btn);


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

