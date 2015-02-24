package io.github.gatimus.slideshow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SlideshowAdapter extends ArrayAdapter<SlideshowInfo> {

    public static final String NAME_EXTRA = "NAME";
    private List<SlideshowInfo> items;
    private LayoutInflater inflater;

    public SlideshowAdapter(Context context, List<SlideshowInfo> items) {
        super(context, -1, items);
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    View.OnClickListener playButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent playSlideshow = new Intent(getContext(), SlideshowPlayer.class);
            playSlideshow.putExtra(NAME_EXTRA, ((SlideshowInfo) v.getTag()).getName());
            getContext().startActivity(playSlideshow);
        }
    };

    View.OnClickListener editButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent editSlideshow = new Intent(getContext(), SlideshowEditor.class);
            editSlideshow.putExtra(NAME_EXTRA, ((SlideshowInfo) v.getTag()).getName());
            getContext().startActivity(editSlideshow);

        }
    };

    View.OnClickListener deleteButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.dialog_confirm_delete);
            builder.setMessage(R.string.dialog_confirm_delete_message);
            builder.setPositiveButton(R.string.button_ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Slideshow.slideshowList.remove((SlideshowInfo) v.getTag());
                            SlideshowAdapter.this.notifyDataSetChanged();
                        }
                    }
            );
            builder.setNegativeButton(R.string.button_cancel, null);
            builder.show();
        }
    };

    private class LoadThumbnailTask extends AsyncTask<Object, Object, Bitmap> {
        ImageView imageView;

        @Override
        protected Bitmap doInBackground(Object... params) {
            imageView = (ImageView) params[0];
            return SlideshowList.getThumbnail((Uri) params[1], getContext().getContentResolver(), new BitmapFactory.Options());
        } //background

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        } //onPost

    } //LoadThumbnailTask


    private static class ViewHolder {
        TextView nameTextView;
        ImageView imageView;
        Button playButton;
        Button editButton;
        Button deleteButton;
    }

}
