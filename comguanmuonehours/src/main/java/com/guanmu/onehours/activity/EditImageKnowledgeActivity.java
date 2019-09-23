package com.guanmu.onehours.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.guanmu.onehours.R;
import com.guanmu.onehours.StorageUtils;
import com.guanmu.onehours.model.MPoint;
import com.guanmu.onehours.model.MTag;
import com.guanmu.onehours.view.PaintView;
import com.rarepebble.colorpicker.ColorPickerView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import me.gujun.android.taggroup.TagGroup;

public class EditImageKnowledgeActivity extends BaseActivity implements View.OnClickListener {

    public static final String IMAGE_BACKGROUND = "ImageBackground";
    private PaintView mPaint;
    private TagGroup mTagGroup;
    private MPoint src;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image_knowledge);

        Intent i = getIntent();

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assert toolbar != null;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Init TagGroup
        mTagGroup = findViewById(R.id.tag_group);

        // Init PaintView
        mPaint = findViewById(R.id.paint);

        // Load background/foreground image
        Uri backUri, foreUri = null;
        String GUID = i.getStringExtra("point_id");
        if (TextUtils.isEmpty(GUID)) {
            src = new MPoint(dao);
            backUri = i.getParcelableExtra(IMAGE_BACKGROUND);
        } else {
            src = new MPoint(dao, UUID.fromString(GUID));
            String[] fileNames = src.getMetaContent().split("/");
            backUri = StorageUtils.getOutputMediaFileUri(this, fileNames[0]);
            foreUri = StorageUtils.getOutputMediaFileUri(this, fileNames[1]);
        }

        List tagList = new ArrayList<>();
        for (MTag tag :
                src.getAddTagList()) {
            tagList.add(tag.getName());
        }
        mTagGroup.setTags(tagList);

        try {
            mPaint.setBackground(backUri, foreUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
        }

        // Init Button Bar
        ToggleButton togglePenButton = findViewById(R.id.toggle_pen_button);
        assert togglePenButton != null;
        togglePenButton.setOnClickListener(this);
        Button setPenWidthButton = findViewById(R.id.set_pen_width_button);
        assert setPenWidthButton != null;
        setPenWidthButton.setOnClickListener(this);
        Button setPenColorButton = findViewById(R.id.set_pen_color_button);
        assert setPenColorButton != null;
        setPenColorButton.setOnClickListener(this);
    }

    private boolean cleanKnowledge() {
        // Check validity
        if (!mPaint.cleanKnowledge()) {
            Toast.makeText(this, R.string.edit_tip_no_blanked, Toast.LENGTH_SHORT).show();
            return false;
        }
        Pair<String, String> fileNamePair;
        try {
            fileNamePair = mPaint.savePictures();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, R.string.edit_tip_no_content, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Set content
        if (src == null) {
            src = new MPoint(dao);
        }
        src.setType(MPoint.Type.image);
        src.setMetaContent(fileNamePair.first + "/" + fileNamePair.second);

        // Save tags
        for (String tag :
                mTagGroup.getTags()) {
            MTag tagObject = new MTag(dao, tag, null);
            tagObject.addPoint(src);
        }
        src.setContent("Image Point");
        src.save();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.guanmu.onehours.R.menu.activity_edit_image_knowledge, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_submit:
                mTagGroup.submitTag();
                if (!cleanKnowledge()) {
                    return true;
                }
                setResult(RESULT_OK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("InflateParams")
    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (view.getId()) {
            case R.id.toggle_pen_button:
                mPaint.togglePenState();
                break;
            case R.id.set_pen_width_button:
                final boolean isPaint = mPaint.isPaint();
                LinearLayout widthLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_width_select, null);

                final SeekBar widthSelect = widthLayout.findViewById(R.id.width_seek_bar);
                final TextView widthText = widthLayout.findViewById(R.id.width_text);
                widthSelect.setProgress(isPaint ? mPaint.getPenWidth() : mPaint.getEraserWidth());
                widthSelect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        widthText.setText(String.valueOf(widthSelect.getProgress()));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                widthSelect.setMax(200);
                widthText.setText(String.valueOf(widthSelect.getProgress()));

                builder.setTitle(R.string.set_pen_width).setView(widthLayout)
                        .setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (isPaint) {
                                    mPaint.setPenWidth(widthSelect.getProgress());
                                } else {
                                    mPaint.setEraserWidth(widthSelect.getProgress());
                                }
                            }
                        }).show();
                break;
            case R.id.set_pen_color_button:
                final ColorPickerView colorPicker = new ColorPickerView(this);
                colorPicker.setColor(mPaint.getPenColor());

                builder.setTitle(R.string.set_pen_color).setView(colorPicker)
                        .setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mPaint.setPenColor(colorPicker.getColor());
                            }
                        }).show();
                break;
        }
    }
}
