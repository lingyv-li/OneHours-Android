package com.guanmu.onehours.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.widget.Toolbar
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.guanmu.onehours.R
import com.guanmu.onehours.SharePreferenceUtils
import com.guanmu.onehours.StorageUtils
import com.guanmu.onehours.adapter.KnowledgeAdapter
import com.guanmu.onehours.model.MPoint
import com.guanmu.onehours.model.MTag
import com.guanmu.onehours.view.CustomEditText
import me.gujun.android.taggroup.TagGroup
import java.util.*

class EditKnowledgeActivity : BaseActivity() {
    private var adapter: KnowledgeAdapter? = null
    private var buttonShowcase: ShowcaseView? = null
    private var tagShowcase: ShowcaseView? = null
    private var saveShowcase: ShowcaseView? = null
    private var editText: CustomEditText? = null
    private var mTagGroup: TagGroup? = null
    private lateinit var button: ToggleButton
    private var src: MPoint? = null // For edit only
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val i = intent
        val GUID = i.getStringExtra("point_id")
        if (TextUtils.isEmpty(GUID)) {
            src = MPoint(dao)
        } else {
            src = MPoint(dao, UUID.fromString(GUID))
        }
        when (src!!.type) {
            MPoint.Type.image -> {
                val intent = Intent(this, EditImageKnowledgeActivity::class.java)
                intent.putExtra("point_id", src!!.guid.toString())
                startActivityForResult(intent, EDIT_IMAGE_ACTIVITY_REQUEST_CODE)
            }
            MPoint.Type.normal -> {
            }
        }
        setContentView(com.guanmu.onehours.R.layout.activity_edit_knowledge)

        // Set up toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        assert(toolbar != null)
        toolbar!!.setNavigationOnClickListener { finish() }

        initView()

    }

    private fun initView() {
        button = findViewById(R.id.button)
        mTagGroup = findViewById(R.id.tag_group)
        editText = findViewById(R.id.edit_text)

        // Init edit area
        assert(editText != null)
        editText!!.setBoldToggleButton(button)
        editText!!.setOnClickListener {
            if (!mTagGroup!!.inputTagText.isEmpty()) {
                mTagGroup!!.submitTag()
                editText!!.requestFocus()
            }
        }
        mTagGroup!!.clearFocus()
        editText!!.clearFocus()

        // Fill data
        val tagList = ArrayList<String>()
        for (tag in src!!.getAddTagList()) {
            tagList.add(tag.name)
        }
        mTagGroup!!.setTags(tagList)
        editText!!.setText(Html.fromHtml(src!!.content))

        // Showcase
        if (SharePreferenceUtils.instance.firstEdit) {
            showTutor(1)
        } else {
            editText!!.requestFocus()
        }

        // Set auto complete adapter
        if (adapter == null) {
            adapter = KnowledgeAdapter(this@EditKnowledgeActivity, KnowledgeAdapter.FilterType.ContentFilter, dao)
        }
        editText!!.setAdapter(adapter)
    }

    private fun showTutor(type: Int) {
        val showcaseEventListener = object : OnShowcaseEventListener {
            override fun onShowcaseViewHide(showcaseView: ShowcaseView) {

            }

            override fun onShowcaseViewDidHide(showcaseView: ShowcaseView) {
                if (showcaseView === buttonShowcase) {
                    showTutor(2)
                } else if (showcaseView === tagShowcase) {
                    showTutor(3)
                } else if (showcaseView === saveShowcase) {
                    editText!!.requestFocus()
                }
            }

            override fun onShowcaseViewShow(showcaseView: ShowcaseView) {

            }

            override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent) {

            }
        }
        when (type) {
            1 -> buttonShowcase = ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setTarget(ViewTarget(button))
                    .setContentTitle(R.string.blank_button_usage_title)
                    .setContentText(R.string.tip_blank_button)
                    .hideOnTouchOutside()
                    .setShowcaseEventListener(showcaseEventListener)
                    .build()
            2 -> tagShowcase = ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setTarget(ViewTarget(mTagGroup!!.getChildAt(0)))
                    .setContentTitle(R.string.tag_usage_title)
                    .setContentText(R.string.tip_tag)
                    .hideOnTouchOutside()
                    .setShowcaseEventListener(showcaseEventListener)
                    .build()
            3 -> saveShowcase = ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setTarget(ViewTarget(R.id.action_submit, this))
                    .setContentTitle(R.string.save_usage_title)
                    .setContentText(R.string.tip_save)
                    .hideOnTouchOutside()
                    .setShowcaseEventListener(showcaseEventListener)
                    .build()
        }
    }

    private fun cleanKnowledge(): Boolean {

        // Check validity
        var textHTML = editText!!.textHTML
        if (textHTML.isEmpty()) {
            Toast.makeText(this, R.string.edit_tip_no_content, Toast.LENGTH_SHORT).show()
            return false
        }

        textHTML = textHTML.substring(0, textHTML.length - 1)

        textHTML = textHTML.replace("</b><b>", "")
        if (textHTML.matches("(?s)<b>.*</b>".toRegex()) && !textHTML.matches("(?s).*</b>.+<b>.*".toRegex()) && !textHTML.matches("(?s).*</b></br>\n<b>.*".toRegex())) {
            Toast.makeText(this, R.string.edit_tip_all_blanked, Toast.LENGTH_SHORT).show()
            return false
        }
        if (!textHTML.matches("(?s).*<b>.+</b>.*".toRegex())) {
            Toast.makeText(this, R.string.edit_tip_no_blanked, Toast.LENGTH_SHORT).show()
            return false
        }

        textHTML = textHTML.replace("</?p.*?>".toRegex(), "")

        // Set content
        if (src == null) {
            src = MPoint(dao)
        }

        src!!.type = MPoint.Type.normal
        src!!.content = textHTML

        // Save tags
        src!!.setTag()
        for (tag in mTagGroup!!.tags) {
            src!!.addTag(MTag(dao, tag, ""))
        }

        src!!.save()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(com.guanmu.onehours.R.menu.activity_edit_knowledge, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        when (item.itemId) {
            R.id.action_camera -> {
                val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                fileUri = StorageUtils.getOutputMediaFileUri(this, null)
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)

                startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
                return true
            }
            R.id.action_submit -> {
                mTagGroup!!.submitTag()
                if (!cleanKnowledge()) {
                    return true
                }
                setResult(RESULT_OK)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE -> when (resultCode) {
                RESULT_OK -> {
                    Toast.makeText(this, "拍照成功:" + fileUri!!.path!!, Toast.LENGTH_SHORT).show()

                    val i = Intent(this, EditImageKnowledgeActivity::class.java)
                    i.putExtra(EditImageKnowledgeActivity.IMAGE_BACKGROUND, fileUri)
                    startActivityForResult(i, EDIT_IMAGE_ACTIVITY_REQUEST_CODE)
                }
                RESULT_CANCELED -> {
                }
                else -> Toast.makeText(this, "拍照失败", Toast.LENGTH_SHORT).show()
            }
            EDIT_IMAGE_ACTIVITY_REQUEST_CODE -> when (resultCode) {
                RESULT_OK -> {
                    setResult(RESULT_OK)
                    finish()
                }
                RESULT_CANCELED -> finish()
            }
        }
    }

    companion object {

        val REQUEST_ADD = 0
        val REQUEST_EDIT = 1
        private val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100
        private val EDIT_IMAGE_ACTIVITY_REQUEST_CODE = 200
    }
}