package io.github.gatimus.slideshow;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SlideshowPlayer extends Activity {
    private static final String TAG = "SLIDESHOW";


    private static final String MEDIA_TIME = "MEDIA_TIME";
    private static final String IMAGE_INDEX = "IMAGE_INDEX";
    private static final String SLIDESHOW_NAME = "SLIDESHOW_NAME";

    private static final int DURATION = 5000;
    private ImageView imageView;
    private String slideshowName;
    private SlideshowInfo slideshow;
    private BitmapFactory.Options options;
    private Handler handler;
    private int nextItemIndex;
    private int mediaTime;
    private MediaPlayer mediaPlayer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slideshow_player);

        imageView = (ImageView) findViewById(R.id.imageView);

        if (savedInstanceState == null) {

            slideshowName = getIntent().getStringExtra(Slideshow.NAME_EXTRA);
            mediaTime = 0;
            nextItemIndex = 0;
        } else {

            mediaTime = savedInstanceState.getInt(MEDIA_TIME);


            nextItemIndex = savedInstanceState.getInt(IMAGE_INDEX);


            slideshowName = savedInstanceState.getString(SLIDESHOW_NAME);
        }


        slideshow = SlideshowList.getSlideshowInfo(slideshowName);


        options = new BitmapFactory.Options();
        options.inSampleSize = 4;


        if (slideshow.getMusicPath() != null) {

            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(
                        this, Uri.parse(slideshow.getMusicPath()));
                mediaPlayer.prepare();
                mediaPlayer.setLooping(true);
                mediaPlayer.seekTo(mediaTime);
            } catch (Exception e) {
                Log.v(TAG, e.toString());
            }
        }

        handler = new Handler();
    }


    @Override
    protected void onStart() {
        super.onStart();
        handler.post(updateSlideshow);
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mediaPlayer != null)
            mediaPlayer.pause();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mediaPlayer != null)
            mediaPlayer.start();
    }


    @Override
    protected void onStop() {
        super.onStop();


        handler.removeCallbacks(updateSlideshow);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null)
            mediaPlayer.release();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        if (mediaPlayer != null)
            outState.putInt(MEDIA_TIME, mediaPlayer.getCurrentPosition());


        outState.putInt(IMAGE_INDEX, nextItemIndex - 1);
        outState.putString(SLIDESHOW_NAME, slideshowName);
    }


    private Runnable updateSlideshow = new Runnable() {
        @Override
        public void run() {
            if (nextItemIndex >= slideshow.size()) {

                if (mediaPlayer != null && mediaPlayer.isPlaying())
                    mediaPlayer.reset();
                finish();
            } else {
                String item = slideshow.getImageAt(nextItemIndex);
                new LoadImageTask().execute(Uri.parse(item));
                ++nextItemIndex;
            }
        }


        public Bitmap getBitmap(Uri uri, ContentResolver cr, BitmapFactory.Options options) {
            Bitmap bitmap = null;


            try {
                InputStream input = cr.openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(input, null, options);
            } catch (FileNotFoundException e) {
                Log.v(TAG, e.toString());
            }

            return bitmap;
        }


        class LoadImageTask extends AsyncTask<Uri, Object, Bitmap> {

            @Override
            protected Bitmap doInBackground(Uri... params) {
                return getBitmap(params[0], getContentResolver(), options);

            }


            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                BitmapDrawable next = new BitmapDrawable(result);
                next.setGravity(android.view.Gravity.CENTER);
                Drawable previous = imageView.getDrawable();


                if (previous instanceof TransitionDrawable)
                    previous = ((TransitionDrawable) previous).getDrawable(1);

                if (previous == null)
                    imageView.setImageDrawable(next);
                else {
                    Drawable[] drawables = {previous, next};
                    TransitionDrawable transition =
                            new TransitionDrawable(drawables);
                    imageView.setImageDrawable(transition);
                    transition.startTransition(1000);
                }

                handler.postDelayed(updateSlideshow, DURATION);
            }
        }
    };
}