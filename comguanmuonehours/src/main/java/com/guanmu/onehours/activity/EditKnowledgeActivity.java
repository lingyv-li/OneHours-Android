package com.guanmu.onehours.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.guanmu.onehours.R;
import com.guanmu.onehours.SharePreferenceUtils;
import com.guanmu.onehours.StorageUtils;
import com.guanmu.onehours.adapter.KnowledgeAdapter;
import com.guanmu.onehours.model.MPoint;
import com.guanmu.onehours.model.MTag;
import com.guanmu.onehours.view.CustomEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.widget.Toolbar;
import me.gujun.android.taggroup.TagGroup;

public class EditKnowledgeActivity extends BaseActivity {

    public static final int REQUEST_ADD = 0;
    public static final int REQUEST_EDIT = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int EDIT_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    private KnowledgeAdapter adapter;
    private ShowcaseView buttonShowcase, tagShowcase, saveShowcase;
    private CustomEditText editText;
    private TagGroup mTagGroup;
    private ToggleButton button;
    private MPoint src; // For edit only
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String GUID = i.getStringExtra("point_id");
        if (TextUtils.isEmpty(GUID)) {
            src = new MPoint(dao);
        } else {
            src = new MPoint(dao, UUID.fromString(GUID));
        }
        switch (src.getType()) {
            case image:
                Intent intent = new Intent(this, EditImageKnowledgeActivity.class);
                intent.putExtra("point_id", src.getGUID().toString());
                startActivityForResult(intent, EDIT_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
            case normal:
                break;
        }
        setContentView(com.guanmu.onehours.R.layout.activity_edit_knowledge);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assert toolbar != null;
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initView();

    }

    private void initView() {
        button = findViewById(R.id.button);
        mTagGroup = findViewById(R.id.tag_group);
        editText = findViewById(R.id.edit_text);

        // Init edit area
        assert editText != null;
        editText.setBoldToggleButton(button);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTagGroup.getInputTagText().isEmpty()) {
                    mTagGroup.submitTag();
                    editText.requestFocus();
                }
            }
        });
        mTagGroup.clearFocus();
        editText.clearFocus();

        // Fill data
        List<String> tagList = new ArrayList<>();
        for (MTag tag :
                src.getAddTagList()) {
            tagList.add(tag.getName());
        }
        mTagGroup.setTags(tagList);
        editText.setText(Html.fromHtml(src.getContent()));

        // Showcase
        if (SharePreferenceUtils.getInstance().getFirstEdit()) {
            showTutor(1);
        } else {
            editText.requestFocus();
        }

        // Set auto complete adapter
        if (adapter == null) {
            adapter = new KnowledgeAdapter(EditKnowledgeActivity.this, KnowledgeAdapter.FilterType.contentFilter, dao);
        }
        editText.setAdapter(adapter);
    }

    private void showTutor(int type) {
        OnShowcaseEventListener showcaseEventListener = new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                if (showcaseView == buttonShowcase) {
                    showTutor(2);
                } else if (showcaseView == tagShowcase) {
                    showTutor(3);
                } else if (showcaseView == saveShowcase) {
                    editText.requestFocus();
                }
            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

            }
        };
        switch (type) {
            case 1:
                buttonShowcase = new ShowcaseView.Builder(this)
                        .withMaterialShowcase()
                        .setTarget(new ViewTarget(button))
                        .setContentTitle(R.string.blank_button_usage_title)
                        .setContentText(R.string.tip_blank_button)
                        .hideOnTouchOutside()
                        .setShowcaseEventListener(showcaseEventListener)
                        .build();
                break;
            case 2:
                tagShowcase = new ShowcaseView.Builder(this)
                        .withMaterialShowcase()
                        .setTarget(new ViewTarget(mTagGroup.getChildAt(0)))
                        .setContentTitle(R.string.tag_usage_title)
                        .setContentText(R.string.tip_tag)
                        .hideOnTouchOutside()
                        .setShowcaseEventListener(showcaseEventListener)
                        .build();
                break;
            case 3:
                saveShowcase = new ShowcaseView.Builder(this)
                        .withMaterialShowcase()
                        .setTarget(new ViewTarget(R.id.action_submit, this))
                        .setContentTitle(R.string.save_usage_title)
                        .setContentText(R.string.tip_save)
                        .hideOnTouchOutside()
                        .setShowcaseEventListener(showcaseEventListener)
                        .build();
        }
    }

    private boolean cleanKnowledge() {

        // Check validity
        String textHTML = editText.getTextHTML();
        if (textHTML.isEmpty()) {
            Toast.makeText(this, R.string.edit_tip_no_content, Toast.LENGTH_SHORT).show();
            return false;
        }

        textHTML = textHTML.substring(0, textHTML.length() - 1);

        textHTML = textHTML.replace("</b><b>", "");
        if (textHTML.matches("(?s)<b>.*</b>") && !textHTML.matches("(?s).*</b>.+<b>.*") && !textHTML.matches("(?s).*</b></br>\n<b>.*")) {
            Toast.makeText(this, R.string.edit_tip_all_blanked, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!textHTML.matches("(?s).*<b>.+</b>.*")) {
            Toast.makeText(this, R.string.edit_tip_no_blanked, Toast.LENGTH_SHORT).show();
            return false;
        }

        textHTML = textHTML.replaceAll("</?p.*?>", "");

        // Set content
        if (src == null) {
            src = new MPoint(dao);
        }

        src.setType(MPoint.Type.normal);
        src.setContent(textHTML);

        // Save tags
        src.setTag();
        for (String tag :
                mTagGroup.getTags()) {
            src.addTag(new MTag(dao, tag, null));
        }

        src.save();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.guanmu.onehours.R.menu.activity_edit_knowledge, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_camera:
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = StorageUtils.getOutputMediaFileUri(this, null);
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                return true;
            case R.id.action_submit:
                mTagGroup.submitTag();
                if (!cleanKnowledge()) {
                    return true;
                }
                setResult(RESULT_OK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        Toast.makeText(this, "拍照成功:" + fileUri.getPath(), Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(this, EditImageKnowledgeActivity.class);
                        i.putExtra(EditImageKnowledgeActivity.IMAGE_BACKGROUND, fileUri);
                        startActivityForResult(i, EDIT_IMAGE_ACTIVITY_REQUEST_CODE);

                        break;
                    case RESULT_CANCELED:
                        break;
                    default:
                        Toast.makeText(this, "拍照失败", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case EDIT_IMAGE_ACTIVITY_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        setResult(RESULT_OK);
                        finish();
                        break;
                    case RESULT_CANCELED:
                        finish();
                }
        }
    }
}