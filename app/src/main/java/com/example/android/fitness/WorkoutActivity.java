package com.example.android.fitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class WorkoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        getSupportActionBar().setTitle("All workouts");
        final ListView list = (ListView)findViewById(R.id.list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v = (TextView)view;
                String exercise = v.getText().toString();
                if(exercise.equals("Cardio"))
                {
                    Intent intent = new Intent(WorkoutActivity.this,ExercisesListActivity.class);
                    intent.putExtra("exercise","exercises");
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(WorkoutActivity.this,ExercisesListActivity.class);
                    intent.putExtra("exercise",exercise);
                    startActivity(intent);
                }
            }
        });
    }
}
