package com.hiltiapp.hiltiadmin.ui.users;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hiltiapp.hiltiadmin.Model.BulkBuyer;
import com.hiltiapp.hiltiadmin.Model.Order;
import com.hiltiapp.hiltiadmin.R;
import com.hiltiapp.hiltiadmin.UserCartListActivity;
import com.hiltiapp.hiltiadmin.ViewHolder.BulkBuyerViewHolder;
import com.hiltiapp.hiltiadmin.ViewHolder.OrderViewHolder;
import com.hiltiapp.hiltiadmin.ui.orders.NotificationsViewModel;

import java.util.HashMap;

public class UsersFragment extends Fragment {

    private RecyclerView usersList;
    private DatabaseReference usersRef;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_users, container, false);


        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        usersList = root.findViewById(R.id.users_list);
        usersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<BulkBuyer> options=
                new FirebaseRecyclerOptions.Builder<BulkBuyer>()
                        .setQuery(usersRef.orderByChild("state").equalTo("notActive"),  BulkBuyer.class)
                        .build();


        FirebaseRecyclerAdapter<BulkBuyer, BulkBuyerViewHolder> adapter =
                new FirebaseRecyclerAdapter<BulkBuyer,  BulkBuyerViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull  BulkBuyerViewHolder holder, final int position, @NonNull final BulkBuyer model)
                    {

                        holder.buyerName.setText(model.getName());
                        holder.buyerPhone.setText(model.getPhone());
                        holder.buyerEmail.setText(model.getEmail());
                        holder.buyerAddress.setText(model.getAddress());
                        holder.buyerShop.setText(model.getShopName());
                        holder.activateBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {

                                 ActivateAccount(model.getPhone());

                            }
                        });

                        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {

                                DeleteAccount(model.getPhone());

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public BulkBuyerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulk_buyer_layout, parent, false);
                        return new BulkBuyerViewHolder(view);
                    }
                };

        usersList.setAdapter(adapter);
        adapter.startListening();

    }
    private void ActivateAccount(String phone) {


        HashMap<String, Object> itemMap = new HashMap<>();
        itemMap.put("state", "active");

        usersRef.child(phone)
                .updateChildren(itemMap)
                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {

                             Toast.makeText(getActivity() ,getString(R.string.toast_activate_account),Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

    private void DeleteAccount(String phone) {

        usersRef.child(phone)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {

                            Toast.makeText(getActivity() ,getString(R.string.toast_delete),Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }
}