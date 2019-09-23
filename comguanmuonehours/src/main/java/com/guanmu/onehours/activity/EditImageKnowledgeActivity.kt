package com.guanmu.onehours.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.util.component1
import androidx.core.util.component2
import com.guanmu.onehours.R
import com.guanmu.onehours.StorageUtils
import com.guanmu.onehours.model.MPoint
import com.guanmu.onehours.model.MTag
import com.guanmu.onehours.view.PaintView
import com.rarepebble.colorpicker.ColorPickerView
import me.gujun.android.taggroup.TagGroup
import java.io.FileNotFoundException
import java.util.*

class EditImageKnowledgeActivity : BaseActivity(), View.OnClickListener {
    private var mPaint: PaintView? = null
    private var mTagGroup: TagGroup? = null
    private var src: MPoint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image_knowledge)

        val i = intent

        // Set up toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        assert(toolbar != null)
        toolbar!!.setNavigationOnClickListener { finish() }

        // Init TagGroup
        mTagGroup = findViewById(R.id.tag_group)

        // Init PaintView
        mPaint = findViewById(R.id.paint)

        // Load background/foreground image

        val guid = i.getStringExtra("point_id")
        val (backUri: Uri, foreUri: Uri?) =
                if (TextUtils.isEmpty(guid)) {
                    src = MPoint(dao)
                    Pair<Uri, Uri?>(i.getParcelableExtra(IMAGE_BACKGROUND), null)
                } else {
                    src = MPoint(dao, UUID.fromString(guid))
                    val fileNames = src!!.metaContent!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    Pair(
                            StorageUtils.getOutputMediaFileUri(this, fileNames[0])!!,
                            StorageUtils.getOutputMediaFileUri(this, fileNames[1]))
                }

        val tagList = src!!.getAddTagList().map { it.name }
        mTagGroup!!.setTags(tagList)

        try {
            mPaint!!.setBackground(backUri, foreUri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            finish()
        }

        // Init Button Bar
        val togglePenButton = findViewById<ToggleButton>(R.id.toggle_pen_button)!!
        togglePenButton.setOnClickListener(this)
        val setPenWidthButton = findViewById<Button>(R.id.set_pen_width_button)!!
        setPenWidthButton.setOnClickListener(this)
        val setPenColorButton = findViewById<Button>(R.id.set_pen_color_button)!!
        setPenColorButton.setOnClickListener(this)
    }

    private fun cleanKnowledge(): Boolean {
        // Check validity
        if (!mPaint!!.cleanKnowledge()) {
            Toast.makeText(this, R.string.edit_tip_no_blanked, Toast.LENGTH_SHORT).show()
            return false
        }
        val fileNamePair: Pair<String, String>
        try {
            fileNamePair = mPaint!!.savePictures()
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, R.string.edit_tip_no_content, Toast.LENGTH_SHORT).show()
            return false
        }

        // Set content
        if (src == null) {
            src = MPoint(dao)
        }
        src!!.type = MPoint.Type.image
        src!!.metaContent = fileNamePair.first + "/" + fileNamePair.second

        // Save tags
        for (tag in mTagGroup!!.tags) {
            val tagObject = MTag(dao, tag, "")
            tagObject.addPoint(src!!)
        }
        src!!.content = "Image Point"
        src!!.save()
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_edit_image_knowledge, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_submit -> {
                mTagGroup!!.submitTag()
                if (!cleanKnowledge()) {
                    return true
                }
                setResult(RESULT_OK)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InflateParams")
    override fun onClick(view: View) {
        val builder = AlertDialog.Builder(this)
        when (view.id) {
            R.id.toggle_pen_button -> mPaint!!.togglePenState()
            R.id.set_pen_width_button -> {
                val isPaint = mPaint!!.isPaint
                val widthLayout = layoutInflater.inflate(R.layout.layout_width_select, null) as LinearLayout

                val widthSelect = widthLayout.findViewById<SeekBar>(R.id.width_seek_bar)
                val widthText = widthLayout.findViewById<TextView>(R.id.width_text)
                widthSelect.progress = if (isPaint) mPaint!!.penWidth else mPaint!!.eraserWidth
                widthSelect.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                        widthText.text = widthSelect.progress.toString()
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {

                    }
                })
                widthSelect.max = 200
                widthText.text = widthSelect.progress.toString()

                builder.setTitle(R.string.set_pen_width).setView(widthLayout)
                        .setPositiveButton(R.string.complete) { _, _ ->
                            if (isPaint) {
                                mPaint!!.penWidth = widthSelect.progress
                            } else {
                                mPaint!!.eraserWidth = widthSelect.progress
                            }
                        }.show()
            }
            R.id.set_pen_color_button -> {
                val colorPicker = ColorPickerView(this)
                colorPicker.color = mPaint!!.penColor

                builder.setTitle(R.string.set_pen_color).setView(colorPicker)
                        .setPositiveButton(R.string.complete) { _, _ ->
                            mPaint!!.penColor = colorPicker.color
                        }.show()
            }
        }
    }

    companion object {

        val IMAGE_BACKGROUND = "ImageBackground"
    }
}
