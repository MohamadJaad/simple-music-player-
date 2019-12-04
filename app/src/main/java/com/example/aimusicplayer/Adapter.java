package com.example.aimusicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Adapter extends ArrayAdapter {
    Context context;
    String[] titles;

    public Adapter(Context context, String[] titles) {
        super(context, R.layout.list_layout,titles);
        this.context = context;
        this.titles = titles;
    }



    private final class ViewHolder {
        TextView textView;
        ImageView imageView;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();
        View result ;


            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_layout, parent, false);
            viewHolder.textView = convertView.findViewById(R.id.textSongTitle);
            viewHolder.imageView = convertView.findViewById(R.id.imageView2);
            convertView.setTag(viewHolder);
           result = convertView;


        viewHolder.textView.setText(titles[position]);
        viewHolder.imageView.setImageResource(R.drawable.musicplayer);

        return result;
    }
}