package com.example.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

//Custom adapter for displaying to-do list
public class ToDoListItemAdapter extends ArrayAdapter<Task> {

    private ArrayList<Task> allTasks;
    private Context mContext;
    private int mResource;

    public ToDoListItemAdapter(Context context, int resource, ArrayList<Task> tasks) {
        super(context, resource, tasks);
        mContext= context;
        mResource = resource;
        //allTasks = new ArrayList<Task>(tasks);
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get task details
        String taskID = getItem(position).getTaskID();
        String description = getItem(position).getDescription();
        String startDate = getItem(position).getStartDate();
        String endDate = getItem(position).getEndDate();
        String status = getItem(position).getStatus();

        Task task = new Task(taskID,description,startDate,endDate,status);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView tvDescription = (TextView) convertView.findViewById(R.id.description);
        TextView tvStartDate = (TextView) convertView.findViewById(R.id.startDate);
        TextView tvEndDate = (TextView) convertView.findViewById(R.id.endDate);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.status);

        //Set task details
        tvDescription.setText(description);
        tvStartDate.setText(startDate);
        tvEndDate.setText(endDate);
        //Set completion status to "Completed" if status = 1 and "In Progress" is status = 0
        if(status.equals("1")) {
            tvStatus.setText("Completed");
        }else{
            tvStatus.setText("In Progress");
        }
        return convertView;
    }
}
