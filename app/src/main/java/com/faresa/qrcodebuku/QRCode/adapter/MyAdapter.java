package com.faresa.qrcodebuku.QRCode.adapter;

import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.faresa.qrcodebuku.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    List<ListItem>listItems;
    Context context;

    public MyAdapter(List<ListItem>listItems,Context context){
        this.listItems = listItems;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent,false);
        return new ViewHolder(v);
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView_Id;
        public TextView textViewCode;
        public TextView textViewType;

        public ViewHolder(View itemView) {
            super(itemView);
            textView_Id = itemView.findViewById(R.id.textView_Id);
            textViewCode = itemView.findViewById(R.id.textViewCode);
            textViewType = itemView.findViewById(R.id.textViewType);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListItem listItem = listItems.get(position);
        holder.textView_Id.setText(String.valueOf(listItem.get_id()));
        holder.textViewCode.setText(listItem.getCode());
        holder.textViewType.setText(listItem.getType());
        Linkify.addLinks(holder.textViewCode,Linkify.ALL);

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}
