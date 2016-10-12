package com.yendry.firebase;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by User on 10/11/2016.
 */

public class MessageHolder extends RecyclerView.ViewHolder {
    public ImageView imgMessage;
    public TextView txtMessage;

    public MessageHolder(View itemView) {
        super(itemView);
        imgMessage = (ImageView) itemView.findViewById(R.id.imgMsg);
        txtMessage = (TextView) itemView.findViewById(R.id.txtMsg);


    }
}
