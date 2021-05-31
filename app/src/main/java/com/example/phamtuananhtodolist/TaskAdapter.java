package com.example.phamtuananhtodolist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }

        TweetViewHolder viewHolder = (TweetViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new TweetViewHolder();
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.text = convertView.findViewById(R.id.text);
            viewHolder.avatar = convertView.findViewById(R.id.avatar);
            convertView.setTag(viewHolder);
        }

        Task task = getItem(position);
        if (task.getImportant().equals("y")) {
            viewHolder.text.setTextColor(Color.parseColor("#FF0000"));
        }

        if (task.getImportant().equals("n")) {
            viewHolder.text.setTextColor(Color.parseColor("#000000"));
        }

        viewHolder.name.setText(task.getName());
        viewHolder.text.setText(task.getText());
        viewHolder.avatar.setImageDrawable(new ColorDrawable(task.getColor()));

        return convertView;
    }
    private class TweetViewHolder{
        public TextView name;
        public TextView text;
        public ImageView avatar;
    }
}

class Task {
    private int color;
    private String name;
    private String text;
    private String important;

    public Task(int color, String name, String text, String important) {
        this.color = color;
        this.name = name;
        this.text = text;
        this.important = important;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getImportant() {
        return important;
    }
}