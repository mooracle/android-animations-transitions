package com.teamtreehouse.albumcover;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;


public class AlbumDetailActivity extends Activity {

    static final String EXTRA_ALBUM_ART_RESID = "EXTRA_ALBUM_ART_RESID"; //private field

    ImageView albumArtView;

    ImageButton fab;

    ViewGroup titlePanel;

    ViewGroup trackPanel;

    ViewGroup detailContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        albumArtView = findViewById(R.id.album_art);
        fab = findViewById(R.id.fab);
        titlePanel = findViewById(R.id.title_panel);
        trackPanel = findViewById(R.id.track_panel);
        detailContainer = findViewById(R.id.detail_container);

        populate();
        albumArtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animate();
            }
        });
    }

    private void animate() {

       /* This was commented out to be replaced by xml style animator ser
       ObjectAnimator fabScaleX = ObjectAnimator.ofFloat(fab, "scaleX", 0, 1);

        ObjectAnimator fabScaleY = ObjectAnimator.ofFloat(fab, "scaleY", 0, 1);

        AnimatorSet fabScale = new AnimatorSet();
        fabScale.playTogether(fabScaleX, fabScaleY);*/

       //xml animator is res/animator/scale.xml needs to be inflated:
        Animator fabScale = AnimatorInflater.loadAnimator(this, R.animator.scale);

        //set the Animator target view which is the fab ImageButton:
        fabScale.setTarget(fab);

        int titleStartValue = titlePanel.getTop();

        int titleEndValue = titlePanel.getBottom();

        //set this to be assigned to ObjectAnimator Effect to be choreographed:
        ObjectAnimator animatorTitle = ObjectAnimator.ofInt(titlePanel,"bottom",
                titleStartValue, titleEndValue);

        //set interpolator for animatorTitle: in this case we will use Accelerate Interpolator type:
        animatorTitle.setInterpolator(new AccelerateInterpolator());

        //set the duration of animatorTitle:
        animatorTitle.setDuration(300); // NOTE: this is in milliseconds (1000 ms = 1 s)

        //set animator title delay: NOTE: Maximizing performance thus this must go!
        //animatorTitle.setStartDelay(1000); // in milliseconds

        //set this also to be assigned to Object Animator for choreographing:
        ObjectAnimator animatorTrack = ObjectAnimator.ofInt(trackPanel, "bottom",
                trackPanel.getTop(), trackPanel.getBottom());

        //set interpolator for animatorTrack: in this case we will use Decelerate Interpolator type:
        animatorTrack.setInterpolator(new DecelerateInterpolator());

        //set the duration for animator Track:
        animatorTrack.setDuration(150); //in milliseconds (1000 ms = 1 s)

        //create the set animation by instantiating a new AnimatorSet object:
        AnimatorSet playFirst = new AnimatorSet();
        AnimatorSet set = new AnimatorSet();

        //using the set Object above to choreograph the whole animations from fab to titles:
        //as for why we don't set the playFirst in xml refer to why xml cannot get runtime data for animator
        playFirst.playTogether(fabScale, animatorTitle);
        set.playSequentially(playFirst, animatorTrack);

        //fix the flickering of views by adding these codes:
        titlePanel.setBottom(titleStartValue); //set the bottom to be Top of title panel view
        trackPanel.setBottom(titleStartValue); //set the bottom of track panel as top of above title panel view
        //both panel comes into animation slided from top to bottom thus the initial state before animation is not
        //visible thus the bottom meet the top of the view titlePanel so it will shown sliding from the same spot

        //as for the fab we need initialized the starting point of both scaleX and scaleY as 0:
        fab.setScaleX(0);
        fab.setScaleY(0);

        //start the animation: NOTE we cannot chain it with the code above, we need to separate it:
        set.start();
    }



    private void populate() {
        int albumArtResId = getIntent().getIntExtra(EXTRA_ALBUM_ART_RESID, R.drawable.mean_something_kinder_than_wolves);
        albumArtView.setImageResource(albumArtResId);

        Bitmap albumBitmap = getReducedBitmap(albumArtResId);
        colorizeFromImage(albumBitmap);
    }

    private Bitmap getReducedBitmap(int albumArtResId) {
        // reduce image size in memory to avoid memory errors
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 8;
        return BitmapFactory.decodeResource(getResources(), albumArtResId, options);
    }

    private void colorizeFromImage(Bitmap image) {
        Palette palette = Palette.from(image).generate();

        // set panel colors
        int defaultPanelColor = 0xFF808080;
        int defaultFabColor = 0xFFEEEEEE;
        titlePanel.setBackgroundColor(palette.getDarkVibrantColor(defaultPanelColor));
        trackPanel.setBackgroundColor(palette.getLightMutedColor(defaultPanelColor));

        // set fab colors
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed}
        };

        int[] colors = new int[]{
                palette.getVibrantColor(defaultFabColor),
                palette.getLightVibrantColor(defaultFabColor)
        };
        fab.setBackgroundTintList(new ColorStateList(states, colors));
    }
}
