package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import java.util.Calendar;
import java.util.HashMap;

//Class create new task by posing data to server where a php scripts handles inserting new task record to the database
public class CreateNewTask extends AppCompatActivity {

    //Ref - https://www.youtube.com/watch?v=qCoidM98zNk
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private EditText taskDescription;
    private TextView info;
    private static final String CREATE_NEW_TASK_URL ="http://192.168.0.41/todolist/createNewTask.php";
    private static final String TASK_DESCRIPTION = "TASK_DESCRIPTION";
    private static final String TASK_START_DATE = "TASK_START_DATE";
    private static final String TASK_DUE_DATE = "TASK_DUE_DATE";
    private static final String TASK_STATUS = "TASK_STATUS";
    private static final String  SUCCESS = "SUCCESS";
    private String dueDateDatabaseDateFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_task);
        initialiseDatePicker();
        dateButton = findViewById(R.id.btn_date_picker);
        taskDescription = findViewById(R.id.et_task_description);
        info = findViewById(R.id.tv_info);
        dateButton.setText(getTodaysDate());
        //Set default due date to today's date - This will change when user select a date
        dueDateDatabaseDateFormat = getTodaysDateDatabaseFormat();
    }

    //Returns today's date in format DD-MM-YY
    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return day + "-" + month + "-" + year;
    }

    //Returns today's date in the format YYYY-MM-DD
    private String getTodaysDateDatabaseFormat() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return year + "-" + month + "-" + day;
    }

    //Initialise date picker dialog and and add listener to it
    private void initialiseDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener =  new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = day + "-" + month + "-" + year;
                //Database date format YYYY-MM-DD
                dueDateDatabaseDateFormat = year + "-" + month  + "-" + day;
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener,year, month, day);
    }

    //Show date picker dialog
    public void openDatePicker(View view) {
        datePickerDialog.show();
    }


    //Post new task details to the server
    public void createNewTask(View view) {
        //Post data to the server script
        //Validate all fields are completed before posting data to server
        String description = taskDescription.getText().toString();
        String startDate = getTodaysDateDatabaseFormat();
        String dueDate = dueDateDatabaseDateFormat;
        String status = "0";
        if (description != null && !description.isEmpty()){
            HashMap postData = new HashMap();
            postData.put(TASK_DESCRIPTION, description);
            postData.put(TASK_START_DATE, startDate);
            postData.put(TASK_DUE_DATE, dueDate);
            postData.put(TASK_STATUS, status);

            PostResponseAsyncTask task = new PostResponseAsyncTask(CreateNewTask.this, postData, new AsyncResponse() {
                @Override
                public void processFinish(String postResponse) {
                    if (postResponse.equals(SUCCESS)) {
                        //Task created successfully - return to main activity
                        //Close this activity and return to main activity
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                        CreateNewTask.this.finish();


                    } else {
                        //Display error message to user
                        info.setText("Could not create new task!!");
                    }
                }
            });

            task.execute(CREATE_NEW_TASK_URL);
        }
    }

    public void cancelTask(View view) {
        //cancel and return to main activity
        this.finish();
    }
}