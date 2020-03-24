package com.example.android.fitness;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

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

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {

    private ImageView mImageView;
    private LottieAnimationView lottieAnimationView;
    private CardView mCardView;
    //private TextView mTextView;
    private ListView mListView;
    private ImageView mExpandImageView;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        mImageView = (ImageView)findViewById(R.id.equipment_image);
        lottieAnimationView= (LottieAnimationView)findViewById(R.id.image_loader);
        mCardView = (CardView)findViewById(R.id.card_benefits);
        //mTextView = (TextView)findViewById(R.id.benefits);
        mListView = (ListView)findViewById(R.id.list_view);
        mExpandImageView = (ImageView)findViewById(R.id.expand);

        String imageUri = "https://firebasestorage.googleapis.com/v0/b/gym-management-5647c.appspot.com/o/exercises%2FElliptical.jpg?alt=media&token=d8a39916-7136-4fed-9b27-ff652e3706e8";

       /* Picasso.get().load(imageUri).fit().centerCrop()
                .placeholder(R.drawable.preloader)
                .error(R.drawable.preloader)
                .into(mImageView); */


        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        /*CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(60f);
        circularProgressDrawable.start();
        */

        RequestOptions options = new RequestOptions()
                .centerCrop();
        Glide.with(this)
                .load(imageUri)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        lottieAnimationView.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(options)
                .into(mImageView);

        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListView.getVisibility()==View.GONE)
                {
                    mListView.setVisibility(View.VISIBLE);
                    populateListView();
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
    void populateListView()
    {
        String []benefits = new String[5];
        benefits[0]="1. Can Aid Weight Loss";
        benefits[1]="2. Low-Impact Exercise";
        benefits[2] = "3. Tones The Entire Body";
        benefits[3] = "4. Boosts Cardiovascular Health";
        benefits[4]= "5. Improves Cardio Stamina";

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,benefits);

        mListView.setAdapter(adapter);
    }
}
