package io.github.gatimus.slideshow;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Slideshow extends ListActivity {

    public static final String NAME_EXTRA = "NAME";

    OnClickListener playButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent playSlideshow = new Intent(Slideshow.this, SlideshowPlayer.class);
            playSlideshow.putExtra(NAME_EXTRA, ((SlideshowInfo) v.getTag()).getName());
            startActivity(playSlideshow);
        }
    };

    private OnClickListener editButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent editSlideshow = new Intent(Slideshow.this, SlideshowEditor.class);
            editSlideshow.putExtra(NAME_EXTRA, ((SlideshowInfo) v.getTag()).getName());
            startActivityForResult(editSlideshow, 0);
        }
    };

    private static final int EDIT_ID = 0;
    static List<SlideshowInfo> slideshowList;
    private ListView slideshowListView;
    private SlideshowAdapter slideshowAdapter;

    private OnClickListener deleteButtonListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Slideshow.this);
            builder.setTitle(R.string.dialog_confirm_delete);
            builder.setMessage(R.string.dialog_confirm_delete_message);
            builder.setPositiveButton(R.string.button_ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Slideshow.slideshowList.remove((SlideshowInfo) v.getTag());
                            slideshowAdapter.notifyDataSetChanged();
                        }
                    }
            );
            builder.setNegativeButton(R.string.button_cancel, null);
            builder.show();
        }
    };


    public static SlideshowInfo getSlideshowInfo(String name) {

        for (SlideshowInfo slideshowInfo : slideshowList)
            if (slideshowInfo.getName().equals(name))
                return slideshowInfo;

        return null;
    }


    public static Bitmap getThumbnail(Uri uri, ContentResolver cr, BitmapFactory.Options options) {
        int id = Integer.parseInt(uri.getLastPathSegment());
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, options);
        return bitmap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slideshowListView = getListView();
        slideshowList = new ArrayList<SlideshowInfo>();
        slideshowAdapter = new SlideshowAdapter(this, slideshowList);
        slideshowListView.setAdapter(slideshowAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.welcome_message_title);
        builder.setMessage(R.string.welcome_message);
        builder.setPositiveButton(R.string.button_ok, null);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.slideshow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slideshow_name_edittext, null);
        final EditText nameEditText = (EditText) view.findViewById(R.id.nameEditText);

        AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
        inputDialog.setView(view);
        inputDialog.setTitle(R.string.dialog_set_name_title);

        inputDialog.setPositiveButton(R.string.button_set_slideshow_name,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String name = nameEditText.getText().toString().trim();

                        if (name.length() != 0) {
                            slideshowList.add(new SlideshowInfo(name));
                            Intent editSlideshowIntent = new Intent(Slideshow.this, SlideshowEditor.class);
                            editSlideshowIntent.putExtra(NAME_EXTRA, name);
                            startActivityForResult(editSlideshowIntent, EDIT_ID);
                        } else {
                            Toast message = Toast.makeText(Slideshow.this, R.string.message_name, Toast.LENGTH_SHORT);
                            message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
                            message.show();
                        }
                    }
                }
        );

        inputDialog.setNegativeButton(R.string.button_cancel, null);
        inputDialog.show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        slideshowAdapter.notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView nameTextView;
        ImageView imageView;
        Button playButton;
        Button editButton;
        Button deleteButton;
    }

    private class SlideshowAdapter extends ArrayAdapter<SlideshowInfo> {
        private List<SlideshowInfo> items;
        private LayoutInflater inflater;

        public SlideshowAdapter(Context context, List<SlideshowInfo> items) {
            super(context, -1, items);
            this.items = items;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } //constructor

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.slideshow_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.slideshowImageView);
                viewHolder.playButton = (Button) convertView.findViewById(R.id.playButton);
                viewHolder.editButton = (Button) convertView.findViewById(R.id.editButton);
                viewHolder.deleteButton = (Button) convertView.findViewById(R.id.deleteButton);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();
            SlideshowInfo slideshowInfo = items.get(position);
            viewHolder.nameTextView.setText(slideshowInfo.getName());
            if (slideshowInfo.size() > 0) {
                String firstItem = slideshowInfo.getImageAt(0);
                new LoadThumbnailTask().execute(viewHolder.imageView, Uri.parse(firstItem));
            }
            viewHolder.playButton.setTag(slideshowInfo);
            viewHolder.playButton.setOnClickListener(playButtonListener);
            viewHolder.editButton.setTag(slideshowInfo);
            viewHolder.editButton.setOnClickListener(editButtonListener);
            viewHolder.deleteButton.setTag(slideshowInfo);
            viewHolder.deleteButton.setOnClickListener(deleteButtonListener);
            return convertView;
        } //getView

    } //slideShowAdapter

    private class LoadThumbnailTask extends AsyncTask<Object, Object, Bitmap> {
        ImageView imageView;

        @Override
        protected Bitmap doInBackground(Object... params) {
            imageView = (ImageView) params[0];
            return Slideshow.getThumbnail((Uri) params[1], getContentResolver(), new BitmapFactory.Options());
        } //background

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        } //onPost

    } //LoadThumbnailTask

} //class