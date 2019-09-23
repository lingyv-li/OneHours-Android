package com.guanmu.onehours

import android.os.Parcel
import android.text.ParcelableSpan
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance

class AgsUnderlineSpan : CharacterStyle(), UpdateAppearance, ParcelableSpan {

    override fun getSpanTypeId(): Int {
        return 6
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {}

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = true
    }
}
