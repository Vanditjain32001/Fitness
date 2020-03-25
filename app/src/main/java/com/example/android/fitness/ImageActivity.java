package com.example.android.fitness;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageActivity extends AppCompatActivity {

    private ImageView mOnlineImageView;
    private LottieAnimationView loadingIndicator;
    private CardView mCardView;
    private ListView mListView;
    private ImageView mExpandImageView;
    private TextView mTitleText;
    private TextView mTimeText;
    private TextView mTargetMusclesText;
    private TextView mDescriptionText;
    private ArrayAdapter<String> adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference;
    DocumentReference documentReference;
    YouTubePlayerView youTubePlayerView;

    private ArrayList<String> benefits;
    private String imageUrl ;
    private String videoUrl ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        mOnlineImageView = (ImageView)findViewById(R.id.online_image);
        loadingIndicator = (LottieAnimationView)findViewById(R.id.loading_indicator);
        mCardView = (CardView)findViewById(R.id.card_benefits);
        mListView = (ListView)findViewById(R.id.list_view);
        mExpandImageView = (ImageView)findViewById(R.id.expand);
        mTitleText = (TextView)findViewById(R.id.title);
        mTimeText = (TextView) findViewById(R.id.time);
        mTargetMusclesText = (TextView)findViewById(R.id.target_muscles);
        mDescriptionText = (TextView)findViewById(R.id.description);
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        String title,exercise_type;
        Intent intent = getIntent();
        title = intent.getStringExtra("exercise_name");
        exercise_type = intent.getStringExtra("exercise_type");
        collectionReference = db.collection(exercise_type);
        documentReference = collectionReference.document(title);
        mTitleText.setText(title);
        mCardView.setEnabled(false);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            @SuppressWarnings("unchecked")
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    Toast.makeText(ImageActivity.this,"no internet connection",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (documentSnapshot.exists()) {
                        mTimeText.setText(documentSnapshot.getString("time"));
                        mTargetMusclesText.setText(documentSnapshot.getString("target_muscles"));
                        mDescriptionText.setText(documentSnapshot.getString("description"));
                        benefits = (ArrayList<String>) documentSnapshot.get("benefits");
                        mCardView.setEnabled(true);
                        imageUrl = documentSnapshot.getString("imageUrl");
                        RequestOptions options = new RequestOptions()
                                .centerCrop();
                        Glide.with(ImageActivity.this)
                                .load(imageUrl)
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        loadingIndicator.setVisibility(View.GONE);
                                        Toast.makeText(ImageActivity.this,"error in loading image",Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        loadingIndicator.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .apply(options)
                                .into(mOnlineImageView);
                        videoUrl = documentSnapshot.getString("videoUrl");
                        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                                youTubePlayer.loadVideo(videoUrl,0);
                            }
                        });
                    }
                }
            }
        });




        /* Picasso.get().load(imageUrl).fit().centerCrop()
                .placeholder(R.drawable.preloader)
                .error(R.drawable.preloader)
                .into(mOnlineImageView); */

        /*CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(60f);
        circularProgressDrawable.start();
        */
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListView.getVisibility()==View.GONE)
                {
                    mListView.setVisibility(View.VISIBLE);
                    populateListView(benefits);
                    mExpandImageView.setImageResource(R.drawable.expand_less);
                }
                else if(mListView.getVisibility()==View.VISIBLE)
                {
                    mListView.setAdapter(null);
                    mListView.setVisibility(View.GONE);
                    mExpandImageView.setImageResource(R.drawable.expand_more);
                }
            }
        });
    }
    void populateListView(ArrayList<String> benefits)
    {
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,benefits);
        mListView.setAdapter(adapter);
    }
}
