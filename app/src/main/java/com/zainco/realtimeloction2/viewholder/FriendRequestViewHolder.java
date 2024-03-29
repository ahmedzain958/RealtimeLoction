package com.zainco.realtimeloction2.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.zainco.realtimeloction2.R;
import com.zainco.realtimeloction2.listeners.IRecyclerItemClickListener;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder {
    public TextView txt_user_email;
    public ImageView btn_accept,btn_decline;


    public FriendRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_user_email = itemView.findViewById(R.id.txt_user_email);
        btn_accept = itemView.findViewById(R.id.btn_accept);
        btn_decline = itemView.findViewById(R.id.btn_decline);
    }
}
