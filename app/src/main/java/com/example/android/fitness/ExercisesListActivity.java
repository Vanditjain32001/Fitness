package com.example.android.fitness;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ExercisesListActivity extends AppCompatActivity {

    private ListView listView;
    private ExerciseAdaptor adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("exercises");


    private ArrayList<Exercise> exercises ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_list);

        exercises = new ArrayList<Exercise>();

        listView = (ListView)findViewById(R.id.list_view);


        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(ExercisesListActivity.this, "error", Toast.LENGTH_SHORT).show();
                } else if (queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(ExercisesListActivity.this,"no notes",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots)
                    {
                        exercises.add(new Exercise(documentSnapshot.getId(),
                                documentSnapshot.getString("time"),
                                documentSnapshot.getString("target_muscles"),
                                documentSnapshot.getString("imageUrl")));
                    }
                    adapter = new ExerciseAdaptor(ExercisesListActivity.this,0,exercises);
                    listView.setAdapter(adapter);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Exercise currentExercise = adapter.getItem(position);
                Intent intent = new Intent(ExercisesListActivity.this,ImageActivity.class);
                intent.putExtra("exercise_name",currentExercise.getTitle());
                intent.putExtra("exercise_type","exercises");
                startActivity(intent);
            }
        });
    }
}
