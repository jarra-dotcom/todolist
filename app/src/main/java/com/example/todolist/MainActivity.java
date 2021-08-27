package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

//This class takes care of populating the list view with task list from the database.
//It queries data from the database and initialise an array list of Task objects which
//is then passed on to the ToDoListItemAdapter custom adapter to initialise and display
//to-do list.
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    //Ref - https://www.youtube.com/watch?v=E6vE8fqQPTE

    private static String DB_URL ="http://192.168.0.41/todolist/getTasks.php";
    private static String UPDATE_TASK_URL ="http://192.168.0.41/todolist/updateTask.php";
    ListView listView;
    BufferedInputStream inputStream;
    String lineItem = null;
    String result = null;
    private ArrayList<Task> toDoListArray;
    private static final String  SUCCESS = "SUCCESS";
    private static final String  FAILED = "FAILED";
    private static final String TASK_COMPLETED_YES = "1";
    private static final String TASK_COMPLETED_NO = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.lvTask);

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        toDoListArray = fetchTaskFromDB();
        ToDoListItemAdapter toDoListItemAdapter = new ToDoListItemAdapter(this, R.layout.layout,toDoListArray);

        //initialise bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //Listener to handle event when the To Do List item is clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Task toDoListTask=(Task)o;
                PopupMenu updateStatusPopupMenu = new PopupMenu(MainActivity.this, view);
                updateStatusPopupMenu.getMenuInflater().inflate(R.menu.update_status_menu, updateStatusPopupMenu.getMenu());
                MenuCompat.setGroupDividerEnabled(updateStatusPopupMenu.getMenu(), true);
                updateStatusPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Retrieve task ID and update database when "Yes"is selected"
                        String taskID = toDoListTask.getTaskID();
                        String selectedMenu = ""; //default selection so database is not updated
                        switch(item.getItemId()){
                            case R.id.status_yes:
                                selectedMenu = TASK_COMPLETED_YES;
                                break;
                            default:
                                selectedMenu = TASK_COMPLETED_NO;
                        }
                        if(!selectedMenu.equals("")) {
                            HashMap postData = new HashMap();
                            postData.put("TASK_ID", taskID);
                            postData.put("TASK_STATUS", selectedMenu);

                            //Posting data to the server is handle by this method
                            PostResponseAsyncTask task = new PostResponseAsyncTask(MainActivity.this,postData, new AsyncResponse() {
                                //Response from the database is handle by this method
                                @Override
                                public void processFinish(String postResponse) {
                                    //Notify user as to whether the update was successful or not
                                    if(postResponse.equals(SUCCESS)) {
                                        toDoListTask.setStatus(TASK_COMPLETED_YES);
                                        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                                        Toast.makeText(MainActivity.this, "Task Updated!! ", Toast.LENGTH_SHORT).show();

                                    }else{
                                        toDoListTask.setStatus(TASK_COMPLETED_NO);
                                        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                                        Toast.makeText(MainActivity.this, "Task Updated!!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                            task.execute(UPDATE_TASK_URL);
                        }

                        return false;
                    }
                });
                updateStatusPopupMenu.show();
            }
        });

        listView.setAdapter(toDoListItemAdapter);
    }

    //Handle even when "Create New Task" menu is clicked in the bottom navigation
    //Ref - https://www.youtube.com/watch?v=bgIUdb-7Rqo
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.create_new_task:
                //open a create new task fragment
                Intent intent = new Intent(this, CreateNewTask.class);
                startActivity(intent);
                return true;
        }

        return false;
    }

    //This method fetches data from the database, initialise Task object and returns array list of task objects
    private ArrayList<Task> fetchTaskFromDB() {
        ArrayList<Task> taskList= new ArrayList<Task>();
        try {
            //Open HTTP connection
            URL scriptURL = new URL(DB_URL);
            HttpURLConnection conn = (HttpURLConnection)scriptURL.openConnection();
            conn.setRequestMethod("GET");
            inputStream = new BufferedInputStream(conn.getInputStream());

        }catch(Exception e){
            e.printStackTrace();
        }

        //Content
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sBuilder = new StringBuilder();
            while((lineItem = bReader.readLine()) != null){
                sBuilder.append(lineItem + "\n");
            }
            inputStream.close();
            result = sBuilder.toString();
        }catch (Exception e){
            e.printStackTrace();
        }

        //JSON
        try{
            JSONArray ja = new JSONArray(result);
            for(int i = 0; i < ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);
                Task task = new Task(jo.getString("taskID"),jo.getString("taskDescription"), jo.getString("startDate"), jo.getString("endDate"), jo.getString("status"));
                taskList.add(task);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return taskList;
    }
}