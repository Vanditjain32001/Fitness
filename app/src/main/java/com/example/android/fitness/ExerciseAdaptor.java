package com.example.android.fitness;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class ExerciseAdaptor extends ArrayAdapter<Exercise> {
    public ExerciseAdaptor(@NonNull Context context, int resource, @NonNull List<Exercise> objects) {
        super(context, 0, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null)
        {
            listItemView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_item_exercises, parent, false);
        }
        Exercise currentWorkOut = getItem(position);

        final ImageView currentImageView = (ImageView)listItemView.findViewById(R.id.thumbnail_image);
        TextView currentTime = (TextView)listItemView.findViewById(R.id.time);
        TextView currentTargetMuscle = (TextView)listItemView.findViewById(R.id.target_muscles);
        TextView currentTitle = (TextView)listItemView.findViewById(R.id.title);
        final LottieAnimationView loadingIndicator = (LottieAnimationView)listItemView.findViewById(R.id.loading_indicator);

        RequestOptions options = new RequestOptions()
                .centerCrop();
        Glide.with(currentImageView.getContext())
                .load(currentWorkOut.getImageUrl())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        loadingIndicator.setVisibility(View.GONE);
                        Toast.makeText(currentImageView.getContext(),"error in loading image",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loadingIndicator.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(options)
                .into(currentImageView);
        currentTitle.setText(currentWorkOut.getTitle());
        currentTime.setText("Time Required: "+currentWorkOut.getTime());
        currentTargetMuscle.setText(currentWorkOut.getTargetMuscles());
        currentTargetMuscle.setSelected(true);
        return listItemView;


    }
}
