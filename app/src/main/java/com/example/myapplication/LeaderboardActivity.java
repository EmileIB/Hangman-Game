package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import de.codecrafters.tableview.TableDataAdapter;
import de.codecrafters.tableview.TableHeaderAdapter;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.colorizers.TableDataRowColorizer;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;

public class LeaderboardActivity extends AppCompatActivity {

    private String URL = "https://drmelhemibrahim.000webhostapp.com/getScores.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        String[][] myData = new String[][] {};

        final SimpleTableDataAdapter dataAdapter = new SimpleTableDataAdapter(this, myData);
        final SimpleTableHeaderAdapter headerAdapter = new SimpleTableHeaderAdapter(this, "Username", "Score");
        TableView<String[]> table = findViewById(R.id.table);
        table.setDataAdapter(dataAdapter);
        table.setHeaderAdapter(headerAdapter);

        table.setHeaderBackgroundColor(getResources().getColor(R.color.design_default_color_primary));
        table.setHeaderElevation(10);
        dataAdapter.setTextColor(Color.BLACK);
        dataAdapter.setTextSize(22);
        headerAdapter.setTextSize(27);
        dataAdapter.setGravity(Gravity.CENTER);
        headerAdapter.setGravity(Gravity.CENTER);
        headerAdapter.setTextColor(Color.WHITE);
        dataAdapter.setPaddings(5,13,5,13);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++){
                    try {
                        JSONObject row = response.getJSONObject(i);
                        String username = row.getString("username");
                        int score = row.getInt("score");
                        String strScore = Integer.toString(score);
                        Log.i("USERNAME", username);
                        dataAdapter.getData().add(new String[]{username, strScore});
                    } catch (Exception ex) {
                        Toast.makeText(LeaderboardActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
                dataAdapter.notifyDataSetChanged();
            }
        }, null);
        queue.add(request);
    }
}