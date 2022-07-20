package com.hiltiapp.hiltiadmin.ui.bulkOrders;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hiltiapp.hiltiadmin.BulkCartListActivity;
import com.hiltiapp.hiltiadmin.Model.Order;
import com.hiltiapp.hiltiadmin.R;
import com.hiltiapp.hiltiadmin.UserCartListActivity;
import com.hiltiapp.hiltiadmin.ViewHolder.OrderViewHolder;
import com.hiltiapp.hiltiadmin.ui.orders.NotificationsViewModel;

public class BulkOrdersFragment extends Fragment {


    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bulk_orders, container, false);


        ordersRef = FirebaseDatabase.getInstance().getReference().child("Bulk Orders");


        ordersList = root.findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Order> options=
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(ordersRef,  Order.class)
                        .build();


        FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<Order,  OrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull  OrderViewHolder holder, final int position, @NonNull final Order model)
                    {
                        holder.userName.setText(getString(R.string.order_name)+ model.getName());
                        holder.userPhoneNumber.setText(getString(R.string.order_phone)+ model.getPhone());
                        holder.userTotalPrice.setText(getString(R.string.le)+ model.getTotalAmount());
                        holder.userDateTime.setText(getString(R.string.order_at)+ model.getDate()+"  "+ model.getTime());
                        holder.userShippingAddress.setText(getString(R.string.order_address)+ model.getAddress()+", "+model.getCity());

                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {

                                String uID = getRef(position).getKey();

                                Intent intent = new Intent(getActivity(), BulkCartListActivity.class);
                                intent.putExtra("id",model.getId());
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                getString(R.string.confirm),
                                                getString(R.string.cancel)
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle( getString(R.string.alert_remove_order) );

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i)
                                    {
                                        if(i == 0)
                                        {
                                            String uID = getRef(position).getKey();
                                            RemoveOrder(model.getId());

                                        }
                                        else
                                        {

                                        }

                                    }
                                });

                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
                        return new OrderViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();

    }
    private void RemoveOrder(String id) {

        ordersRef.child(id).removeValue();


        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Bulk Cart List");

        cartListRef.child(id)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                             Toast.makeText(getActivity(), getString(R.string.toast_remove_order),Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

}