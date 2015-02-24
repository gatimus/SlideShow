package io.github.gatimus.slideshow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static io.github.gatimus.slideshow.SlideshowList.OnFragmentInteractionListener;


public class MainActivity extends ActionBarActivity implements OnFragmentInteractionListener {

    public static final String NAME_EXTRA = "NAME";
    private static final int EDIT_ID = 0;
    private SlideshowList slideshowList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        slideshowList = SlideshowList.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.fragment, slideshowList).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.slideshow_menu, menu);
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
                            Intent editSlideshowIntent = new Intent(getApplicationContext(), SlideshowEditor.class);
                            editSlideshowIntent.putExtra(NAME_EXTRA, name);
                            startActivityForResult(editSlideshowIntent, EDIT_ID);
                        } else {
                            Toast message = Toast.makeText(getApplicationContext(), R.string.message_name, Toast.LENGTH_SHORT);
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
    public void onFragmentInteraction(String id) {

    }

}
