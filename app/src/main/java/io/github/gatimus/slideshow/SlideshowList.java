package io.github.gatimus.slideshow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class SlideshowList extends Fragment implements AbsListView.OnItemClickListener {

    static List<SlideshowInfo> slideshowList;
    private AbsListView slideshowListView;
    private SlideshowAdapter slideshowAdapter;

    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;


    public static SlideshowList newInstance() {
        return new SlideshowList();
    }

    public SlideshowList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.welcome_message_title);
        builder.setMessage(R.string.welcome_message);
        builder.setPositiveButton(R.string.button_ok, null);
        builder.show();

        slideshowList = new ArrayList<SlideshowInfo>();
        slideshowAdapter = new SlideshowAdapter(getActivity(), slideshowList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        slideshowListView = (AbsListView) view.findViewById(android.R.id.list);
        slideshowListView.setAdapter(slideshowAdapter);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        slideshowAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }

    public void add(SlideshowInfo slideshowInfo) {
        slideshowList.add(slideshowInfo);
    }

    public static SlideshowInfo getSlideshowInfo(String name) {

        for (SlideshowInfo slideshowInfo : slideshowList)
            if (slideshowInfo.getName().equals(name))
                return slideshowInfo;

        return null;
    }

    public static Bitmap getThumbnail(Uri uri, ContentResolver cr, BitmapFactory.Options options) {
        String stringID = uri.getLastPathSegment().substring(uri.toString().lastIndexOf(":") + 1);
        int id = Integer.parseInt(stringID);
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, options);

        return bitmap;
    }


}
