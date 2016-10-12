package com.yendry.firebase;

import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 10/11/2016.
 */

public class ConverseView extends RecyclerView.Adapter<MessageHolder> {

    Context context;
    List<Message> msgList;
    public ConverseView(Context c,ArrayList<Message> messageList){
        this.context=c;
        this.msgList = messageList;
    }
    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_view, parent, false);
        MessageHolder holder = new MessageHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {

        //Log.d("onBind", msgList.get(position).getMessage());
        //https://firebasestorage.googleapis.com/v0/b/fir-82de7
        String message = msgList.get(position).getMessage();
        String sender = msgList.get(position).getSender();

        if(sender.equals(Chat_Room.user_name))
            holder.txtMessage.setGravity(Gravity.RIGHT);
        if(message.contains("https://firebasestorage.googleapis.com/v0/b/fir-82de7")){
            holder.txtMessage.setText(sender+":");
            Log.d("Camera",message);
            Picasso.with(context).load(message).placeholder(R.drawable.ic_launcher).fit().centerCrop().into(holder.imgMessage);
        } else {
            holder.txtMessage.setText(sender+":"+message);
            holder.imgMessage.setVisibility(View.GONE);

        }

    }


    @Override
    public int getItemCount() {
        return msgList.size();
    }
}
