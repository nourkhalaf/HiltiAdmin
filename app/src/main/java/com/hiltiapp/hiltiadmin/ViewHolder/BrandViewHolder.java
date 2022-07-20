package com.hiltiapp.hiltiadmin.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hiltiapp.hiltiadmin.Interface.ItemClickListener;
import com.hiltiapp.hiltiadmin.R;

public class BrandViewHolder extends RecyclerView.ViewHolder implements android.view.View.OnClickListener {


    public ItemClickListener listener;
    public TextView brandName;


    public BrandViewHolder(@NonNull View itemView) {
        super(itemView);
        brandName = itemView.findViewById(R.id.brand_name);
    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(android.view.View view)
    {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
