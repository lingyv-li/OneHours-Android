package com.guanmu.onehours.view

import android.content.Context
import android.graphics.Typeface
import android.text.*
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.widget.AutoCompleteTextView
import android.widget.ToggleButton
import com.guanmu.onehours.AgsUnderlineSpan

class CustomEditText : AutoCompleteTextView {

    // Optional styling button references
    private var boldToggle: ToggleButton? = null
    private var italicsToggle: ToggleButton? = null
    private var underlineToggle: ToggleButton? = null
    private val blockToggle: ToggleButton? = null

    // Html image getter that handles the loading of inline images
    private var imageGetter: Html.ImageGetter? = null

    private var isDeleteCharaters = false

    // Color
    private var currentColor = -1

    // Get and set Spanned, styled text
    var spannedText: Spanned
        get() = this.text
        set(text) = this.setText(text)

    // Get and set simple text as simple strings
    var stringText: String
        get() = this.text.toString()
        set(text) = this.setText(text)

    // Get and set styled HTML text
    var textHTML: String
        get() = Html.toHtml(this.text)
        set(text) = this.setText(Html.fromHtml(text, imageGetter, null))
    //    private EventBack eventBack;
    //
    //

    //    // interface
    //    public interface EventBack {
    //        public void close();
    //
    //        public void show();
    //    }
    //
    //    public EventBack getEventBack() {
    //        return eventBack;
    //    }
    //
    //    public void setEventBack(EventBack eventBack) {
    //        this.eventBack = eventBack;
    //    }
    //
    //    // XXX Triet H.M. Pham
    //    @Override
    //    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
    //        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
    //            eventBack.close();
    //        }
    //        else
    //        {
    //            eventBack.show();
    //        }
    //        return super.dispatchKeyEvent(event);
    //    }

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initialize()
    }

    override fun performFiltering(text: CharSequence, keyCode: Int) {
        if (lineCount == 1) {
            super.performFiltering(text, keyCode)
        }
    }

    private fun initialize() {
        // Add a default imageGetter
        imageGetter = Html.ImageGetter { null }

        // Add TextWatcher that reacts to text changes and applies the selected
        // styles
        this.addTextChangedListener(DWTextWatcher())
    }

    /**
     * When the user selects a section of the text, this method is used to
     * toggle the defined style on it. If the selected text already has the
     * style applied, we remove it, otherwise we apply it.
     *
     * @param style The styles that should be toggled on the selected text.
     */
    private fun toggleStyle(style: Int) {
        var selectionStart = this.selectionStart
        var selectionEnd = this.selectionEnd

        // Reverse if the case is what's noted above
        if (selectionStart > selectionEnd) {
            val temp = selectionEnd
            selectionEnd = selectionStart
            selectionStart = temp
        }

        if (selectionEnd > selectionStart) {
            when (style) {
                STYLE_BOLD -> boldButtonClick(selectionStart, selectionEnd)
                STYLE_ITALIC -> italicButtonClick(selectionStart, selectionEnd)
                STYLE_UNDERLINED -> underlineButtonClick(selectionStart, selectionEnd)
            }
        }
    }

    private fun underlineButtonClick(selectionStart: Int, selectionEnd: Int) {
        var exists = false
        val str = this.text
        val underSpan = str.getSpans(selectionStart, selectionEnd, AgsUnderlineSpan::class.java)

        // If the selected text-part already has UNDERLINE style on it, then we need to disable it
        var underlineStart = -1
        var underlineEnd = -1
        for (styleSpan in underSpan) {
            if (str.getSpanStart(styleSpan) < selectionStart) {
                underlineStart = str.getSpanStart(styleSpan)
            }
            if (str.getSpanEnd(styleSpan) > selectionEnd) {
                underlineEnd = str.getSpanEnd(styleSpan)
            }
            str.removeSpan(styleSpan)
            exists = true
        }
        if (underlineStart > -1) {
            str.setSpan(AgsUnderlineSpan(), underlineStart, selectionStart,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (underlineEnd > -1) {
            str.setSpan(AgsUnderlineSpan(), selectionEnd, underlineEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Else we set UNDERLINE style on it
        if (!exists) {
            str.setSpan(AgsUnderlineSpan(), selectionStart, selectionEnd,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        } else {
            underlineToggle!!.isChecked = false
        }

        this.setSelection(selectionStart, selectionEnd)
    }

    private fun italicButtonClick(selectionStart: Int, selectionEnd: Int) {
        handleStyleSpannable(selectionStart, selectionEnd, Typeface.ITALIC)
    }

    private fun boldButtonClick(selectionStart: Int, selectionEnd: Int) {
        handleStyleSpannable(selectionStart, selectionEnd, Typeface.BOLD)
    }

    private fun handleStyleSpannable(selectionStart: Int, selectionEnd: Int, type: Int) {
        var exists = false
        val str = this.text
        val styleSpans = str.getSpans(selectionStart, selectionEnd, StyleSpan::class.java)

        // If the selected text-part already has BOLD style on it,
        // then
        // we need to disable it
        var styleStart = -1
        var styleEnd = -1
        for (styleSpan in styleSpans) {
            if (styleSpan.style == type) {
                if (str.getSpanStart(styleSpan) < selectionStart) {
                    styleStart = str.getSpanStart(styleSpan)
                }
                if (str.getSpanEnd(styleSpan) > selectionEnd) {
                    styleEnd = str.getSpanEnd(styleSpan)
                }
                str.removeSpan(styleSpan)
                exists = true
            }
        }
        if (styleStart > -1) {
            str.setSpan(StyleSpan(type), styleStart, selectionStart,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (styleEnd > -1) {
            str.setSpan(StyleSpan(type), selectionEnd, styleEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Else we set BOLD style on it
        if (!exists) {
            str.setSpan(StyleSpan(type), selectionStart, selectionEnd,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        } else {
            when (type) {
                //            case android.graphics.Typeface.ITALIC:
                //                italicsToggle.setChecked(false);
                //                break;
                Typeface.BOLD -> boldToggle!!.isChecked = false
            }
        }
        this.setSelection(selectionStart, selectionEnd)
    }

    public override fun onSelectionChanged(selStart: Int, selEnd: Int) {

        var boldExists = false
        var italicsExists = false
        var underlinedExists = false

        // If the user only placed the cursor around
        if (selStart > 0 && selStart == selEnd) {
            val styleSpans = this.text.getSpans(selStart - 1, selStart, CharacterStyle::class.java)

            for (styleSpan in styleSpans) {
                if (styleSpan is StyleSpan) {
                    if (styleSpan.style == Typeface.BOLD) {
                        boldExists = true
                    } else if (styleSpan.style == Typeface.ITALIC) {
                        italicsExists = true
                    } else if (styleSpan.style == Typeface.BOLD_ITALIC) {
                        italicsExists = true
                        boldExists = true
                    }
                } else if (styleSpan is AgsUnderlineSpan) {
                    underlinedExists = true
                }
            }
        } else if (!TextUtils.isEmpty(this@CustomEditText.text)) {
            val styleSpans = this.text.getSpans(selStart, selEnd,
                    CharacterStyle::class.java)

            for (styleSpan in styleSpans) {
                if (styleSpan is StyleSpan) {
                    if (styleSpan.style == Typeface.BOLD) {
                        if (this.text.getSpanStart(styleSpan) <= selStart && this.text.getSpanEnd(styleSpan) >= selEnd) {
                            boldExists = true
                        }
                    } else if (styleSpan.style == Typeface.ITALIC) {
                        if (this.text.getSpanStart(styleSpan) <= selStart && this.text.getSpanEnd(styleSpan) >= selEnd) {
                            italicsExists = true
                        }
                    } else if (styleSpan.style == Typeface.BOLD_ITALIC) {
                        if (this.text.getSpanStart(styleSpan) <= selStart && this.text.getSpanEnd(styleSpan) >= selEnd) {
                            italicsExists = true
                            boldExists = true
                        }
                    }
                } else if (styleSpan is AgsUnderlineSpan) {
                    if (this.text.getSpanStart(styleSpan) <= selStart && this.text.getSpanEnd(styleSpan) >= selEnd) {
                        underlinedExists = true
                    }
                }
            }
        }// Else if the user selected multiple characters

        // Display the format settings
        if (boldToggle != null) {
            if (boldExists)
                boldToggle!!.isChecked = true
            else
                boldToggle!!.isChecked = false
        }

        if (italicsToggle != null) {
            if (italicsExists)
                italicsToggle!!.isChecked = true
            else
                italicsToggle!!.isChecked = false
        }

        if (underlineToggle != null) {
            if (underlinedExists)
                underlineToggle!!.isChecked = true
            else
                underlineToggle!!.isChecked = false
        }
    }

    // Set the default image getter that handles the loading of inline images
    fun setImageGetter(imageGetter: Html.ImageGetter) {
        this.imageGetter = imageGetter
    }


    // Style toggle button setters
    fun setBoldToggleButton(button: ToggleButton) {
        boldToggle = button

        boldToggle!!.setOnClickListener { toggleStyle(STYLE_BOLD) }
    }

    fun setItalicsToggleButton(button: ToggleButton) {
        italicsToggle = button

        italicsToggle!!.setOnClickListener { toggleStyle(STYLE_ITALIC) }
    }

    fun setUnderlineToggleButton(button: ToggleButton) {
        underlineToggle = button

        underlineToggle!!.setOnClickListener { toggleStyle(STYLE_UNDERLINED) }
    }

    fun setColor(color: Int, selectionStart: Int, selectionEnd: Int) {
        var selectionStart = selectionStart
        var selectionEnd = selectionEnd
        currentColor = color

        // Reverse if the case is what's noted above
        if (selectionStart > selectionEnd) {
            val temp = selectionEnd
            selectionEnd = selectionStart
            selectionStart = temp
        }

        // The selectionEnd is only greater then the selectionStart position
        // when the user selected a section of the text. Otherwise, the 2
        // variables
        // should be equal (the cursor position).
        if (selectionEnd > selectionStart) {
            val spannable = this.text
            val appliedStyles = spannable.getSpans(selectionStart, selectionEnd,
                    ForegroundColorSpan::class.java)
            if (appliedStyles != null && appliedStyles.size > 0) {
                var colorStart = -1
                var colorEnd = -1
                var beforeColor = 0
                var afterColor = 0
                for (foregroundColorSpan in appliedStyles) {
                    if (spannable.getSpanStart(foregroundColorSpan) < selectionStart) {
                        colorStart = spannable.getSpanStart(foregroundColorSpan)
                        beforeColor = foregroundColorSpan.foregroundColor
                    }
                    if (spannable.getSpanEnd(foregroundColorSpan) > selectionEnd) {
                        colorEnd = spannable.getSpanEnd(foregroundColorSpan)
                        afterColor = foregroundColorSpan.foregroundColor
                    }
                    spannable.removeSpan(foregroundColorSpan)
                }

                //
                if (colorStart > -1) {
                    spannable.setSpan(ForegroundColorSpan(beforeColor), colorStart,
                            selectionStart,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (colorEnd > -1) {
                    spannable.setSpan(ForegroundColorSpan(afterColor), selectionEnd, colorEnd,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                spannable.setSpan(ForegroundColorSpan(color), selectionStart, selectionEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                spannable.setSpan(ForegroundColorSpan(color), selectionStart, selectionEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            this.setSelection(selectionStart, selectionEnd)
        }
    }

    private inner class DWTextWatcher : TextWatcher {
        private var beforeChangeTextLength = 0
        private var appendTextLength = 0

        /**
         * After text Change
         */
        override fun afterTextChanged(editable: Editable) {
            // Add style as the user types if a toggle button is enabled
            var position = Selection.getSelectionStart(this@CustomEditText.text)
            appendTextLength = Math.abs(position - beforeChangeTextLength)

            //XXX: Fixed bold error when text size not change
            if (appendTextLength == 0 || isDeleteCharaters) {
                return
            }

            if (position < 0) {
                position = 0
            }

            if (position > 0) {
                val appliedStyles = editable.getSpans(position - 1, position, CharacterStyle::class.java)

                var currentBoldSpan: StyleSpan? = null
                var currentItalicSpan: StyleSpan? = null
                var currentAgsUnderlineSpan: AgsUnderlineSpan? = null
                var currentForegroundColorSpan: ForegroundColorSpan? = null

                // Look for possible styles already applied to the entered text
                for (appliedStyle in appliedStyles) {
                    if (appliedStyle is StyleSpan) {
                        if (appliedStyle.style == Typeface.BOLD) {
                            // Bold style found
                            currentBoldSpan = appliedStyle
                        } else if (appliedStyle.style == Typeface.ITALIC) {
                            // Italic style found
                            currentItalicSpan = appliedStyle
                        }
                    } else if (appliedStyle is AgsUnderlineSpan) {
                        // Underlined style found
                        currentAgsUnderlineSpan = appliedStyle
                    } else if (appliedStyle is ForegroundColorSpan) {
                        if (currentForegroundColorSpan == null) {
                            currentForegroundColorSpan = appliedStyle
                        }
                    }
                }

                handleInsertBoldCharacter(editable, position, currentBoldSpan)
                handleInsertItalicCharacter(editable, position, currentItalicSpan)
                handleInsertUnderlineCharacter(editable, position, currentAgsUnderlineSpan)
                handleInsertColorCharacter(editable, position, currentForegroundColorSpan)
            }

        }

        private fun handleInsertColorCharacter(editable: Editable, position: Int, currentForegroundColorSpan: ForegroundColorSpan?) {
            // Handle color
            if (currentForegroundColorSpan != null) {
                if (currentForegroundColorSpan.foregroundColor != currentColor) {
                    val colorStart = editable.getSpanStart(currentForegroundColorSpan)
                    val colorEnd = editable.getSpanEnd(currentForegroundColorSpan)

                    if (position == colorEnd) {
                        val nextSpan = getNextForegroundColorSpan(editable, position)
                        if (nextSpan != null) {
                            if (currentColor == nextSpan.foregroundColor) {
                                val colorEndNextSpan = editable.getSpanEnd(nextSpan)
                                editable.removeSpan(currentForegroundColorSpan)
                                editable.removeSpan(nextSpan)
                                // set before span
                                editable.setSpan(ForegroundColorSpan(currentForegroundColorSpan.foregroundColor), colorStart,
                                        colorEnd - appendTextLength,
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                                // set after span
                                editable.setSpan(ForegroundColorSpan(nextSpan.foregroundColor),
                                        position - appendTextLength,
                                        colorEndNextSpan,
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                                return
                            }
                        }

                    }
                    editable.removeSpan(currentForegroundColorSpan)
                    if (position - appendTextLength < colorEnd && colorStart != colorEnd) {
                        // Cursor in the text's middle with different color
                        val oldColor = currentForegroundColorSpan.foregroundColor

                        if (colorStart < position - appendTextLength) {
                            // Before inserting text
                            editable.setSpan(ForegroundColorSpan(oldColor), colorStart,
                                    position - appendTextLength,
                                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                        }

                        // At inserting
                        editable.setSpan(ForegroundColorSpan(currentColor), position - appendTextLength,
                                position,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

                        if (position < colorEnd) {
                            // After inserting
                            editable.setSpan(ForegroundColorSpan(oldColor), position, colorEnd,
                                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                        }
                    } else {
                        // Cursor in the end
                        editable.setSpan(ForegroundColorSpan(currentColor), position - appendTextLength,
                                colorEnd,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                    }
                }
            } else if (currentColor != -1) {
                val nextSpan = getNextForegroundColorSpan(editable, position)
                if (nextSpan != null) {
                    val colorEndNextSpan = editable.getSpanEnd(nextSpan)
                    if (currentColor == nextSpan.foregroundColor) {
                        editable.removeSpan(nextSpan)
                        // set before span
                        editable.setSpan(ForegroundColorSpan(nextSpan.foregroundColor),
                                position - appendTextLength,
                                colorEndNextSpan,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

                        return
                    }
                }
                editable.setSpan(ForegroundColorSpan(currentColor),
                        position - appendTextLength, position,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            }
        }

        private fun getNextForegroundColorSpan(editable: Editable, position: Int): ForegroundColorSpan? {
            val nextSpans = editable.getSpans(position, position + 1, ForegroundColorSpan::class.java)
            return if (nextSpans.size > 0) {
                nextSpans[0]
            } else null
        }

        private fun handleInsertUnderlineCharacter(editable: Editable, position: Int,
                                                   currentAgsUnderlineSpan: AgsUnderlineSpan?) {
            // Handle the underlined style toggle button if it's present
            if (underlineToggle != null && underlineToggle!!.isChecked
                    && currentAgsUnderlineSpan == null) {
                editable.setSpan(AgsUnderlineSpan(), position - appendTextLength, position,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            } else if (underlineToggle != null && !underlineToggle!!.isChecked
                    && currentAgsUnderlineSpan != null) {
                val underLineStart = editable.getSpanStart(currentAgsUnderlineSpan)
                val underLineEnd = editable.getSpanEnd(currentAgsUnderlineSpan)

                editable.removeSpan(currentAgsUnderlineSpan)
                if (underLineStart <= position - appendTextLength) {
                    editable.setSpan(AgsUnderlineSpan(), underLineStart, position - appendTextLength,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                // We need to split the span
                if (underLineEnd > position) {
                    editable.setSpan(AgsUnderlineSpan(), position, underLineEnd,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }

        private fun handleInsertItalicCharacter(editable: Editable, position: Int,
                                                currentItalicSpan: StyleSpan?) {
            // Handle the italics style toggle button if it's present
            if (italicsToggle != null && italicsToggle!!.isChecked && currentItalicSpan == null) {
                editable.setSpan(StyleSpan(Typeface.ITALIC), position - appendTextLength,
                        position,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            } else if (italicsToggle != null && !italicsToggle!!.isChecked
                    && currentItalicSpan != null) {
                val italicStart = editable.getSpanStart(currentItalicSpan)
                val italicEnd = editable.getSpanEnd(currentItalicSpan)

                editable.removeSpan(currentItalicSpan)
                if (italicStart <= position - appendTextLength) {
                    editable.setSpan(StyleSpan(Typeface.ITALIC),
                            italicStart, position - appendTextLength,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                // Split the span
                if (italicEnd > position) {
                    editable.setSpan(StyleSpan(Typeface.ITALIC), position,
                            italicEnd,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }

        private fun handleInsertBoldCharacter(editable: Editable, position: Int,
                                              currentBoldSpan: StyleSpan?) {
            // Handle the bold style toggle button if it's present
            if (boldToggle != null) {
                if (boldToggle!!.isChecked && currentBoldSpan == null) {
                    // The user switched the bold style button on and the
                    // character doesn't have any bold
                    // style applied, so we start a new bold style span. The
                    // span is inclusive,
                    // so any new characters entered right after this one
                    // will automatically get this style.
                    editable.setSpan(StyleSpan(Typeface.BOLD),
                            position - appendTextLength, position,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                } else if (!boldToggle!!.isChecked && currentBoldSpan != null) {
                    // The user switched the bold style button off and the
                    // character has bold style applied.
                    // We need to remove the old bold style span, and define
                    // a new one that end 1 position right
                    // before the newly entered character.
                    val boldStart = editable.getSpanStart(currentBoldSpan)
                    val boldEnd = editable.getSpanEnd(currentBoldSpan)

                    editable.removeSpan(currentBoldSpan)
                    if (boldStart <= position - appendTextLength) {
                        editable.setSpan(StyleSpan(Typeface.BOLD),
                                boldStart, position - appendTextLength,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }

                    // The old bold style span end after the current cursor
                    // position, so we need to define a
                    // second newly created style span too, which begins
                    // after the newly entered character and
                    // ends at the old span's ending position. So we split
                    // the span.
                    if (boldEnd > position) {
                        editable.setSpan(StyleSpan(Typeface.BOLD),
                                position, boldEnd,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
        }

        /**
         * Before Text Change
         */
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            var position = Selection.getSelectionStart(this@CustomEditText.text)
            if (position < 0) {
                position = 0
            }

            beforeChangeTextLength = position
            if (count - after == 1 || s.length == 0 && position > 0) { // Delete character
                val editable = this@CustomEditText.text

                removeForegroundColorSpan(position, editable)
                removeAgsUnderlineSpan(position, editable)
                removeStyleSpan(position, editable, Typeface.ITALIC)
                removeStyleSpan(position, editable, Typeface.BOLD)
            }
        }

        private fun removeForegroundColorSpan(position: Int, editable: Editable) {
            val previousColorSpan = getPreviousForegroundColorSpan(editable, position, ForegroundColorSpan::class.java) as ForegroundColorSpan?
            val appliedStyles = editable.getSpans(position - 1, position, ForegroundColorSpan::class.java)

            if (appliedStyles.size > 0 && appliedStyles[0] != null && previousColorSpan != null
                    && previousColorSpan.foregroundColor != appliedStyles[0].foregroundColor) {
                val colorSpan = appliedStyles[0]
                val colorStart = editable.getSpanStart(colorSpan)
                val colorEnd = editable.getSpanEnd(colorSpan)

                editable.removeSpan(colorSpan)
                if (colorStart < position - 1) {
                    editable.setSpan(AgsUnderlineSpan(), colorStart, position - 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                // We need to split the span
                if (colorEnd > position) {
                    editable.setSpan(AgsUnderlineSpan(), position, colorEnd,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }

        private fun removeAgsUnderlineSpan(position: Int, editable: Editable) {
            val previousColorSpan = getPreviousForegroundColorSpan(editable, position, AgsUnderlineSpan::class.java) as AgsUnderlineSpan?
            val appliedStyles = editable.getSpans(position - 1, position, AgsUnderlineSpan::class.java)

            if (appliedStyles.size > 0 && previousColorSpan == null) {
                val colorSpan = appliedStyles[0]
                val underLineStart = editable.getSpanStart(colorSpan)
                val underLineEnd = editable.getSpanEnd(colorSpan)

                editable.removeSpan(colorSpan)
                if (underLineStart < position - 1) {
                    editable.setSpan(AgsUnderlineSpan(), underLineStart, position - 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                // We need to split the span
                if (underLineEnd > position) {
                    editable.setSpan(AgsUnderlineSpan(), position, underLineEnd,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }

        private fun removeStyleSpan(position: Int, editable: Editable, type: Int) {
            val previousColorSpan = getPreviousForegroundColorSpan(editable, position, StyleSpan::class.java) as StyleSpan?
            val appliedStyles = editable.getSpans(position - 1, position, StyleSpan::class.java)

            var styleSpan: StyleSpan? = null
            for (span in appliedStyles) {
                if (span.style == type) {
                    styleSpan = span
                }
            }

            if (styleSpan != null && previousColorSpan == null) {
                val styleStart = editable.getSpanStart(styleSpan)
                val styleEnd = editable.getSpanEnd(styleSpan)

                editable.removeSpan(styleSpan)
                if (styleStart < position - 1) {
                    editable.setSpan(StyleSpan(type), styleStart, position - 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                // We need to split the span
                if (styleEnd > position) {
                    editable.setSpan(StyleSpan(type), position, styleEnd,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }

        private fun <T> getPreviousForegroundColorSpan(editable: Editable, position: Int, clss: Class<T>): T? {
            if (position - 2 >= 0) {
                val nextSpans = editable.getSpans<T>(position - 2, position - 1, clss)
                if (nextSpans.isNotEmpty()) {
                    return nextSpans[0]
                }
            }
            return null
        }

        /**
         * On Text Change
         */
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            isDeleteCharaters = count == 0
            // Remove all span when EditText is empty
            if (this@CustomEditText.text.toString().isEmpty()) {
                val appliedStyles = this@CustomEditText.text.getSpans(0,
                        this@CustomEditText.text.length, CharacterStyle::class.java)
                for (characterStyle in appliedStyles) {
                    this@CustomEditText.text.removeSpan(characterStyle)
                }
            }
        }
    }

    companion object {

        // Log tag
        val TAG = "DroidWriter"

        // Style constants
        private val STYLE_BOLD = 0
        private val STYLE_ITALIC = 1
        private val STYLE_UNDERLINED = 2
    }
}
