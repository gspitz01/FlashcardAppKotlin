package com.gregspitz.flashcardappkotlin.data.service.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DownloadCategory(val name: String = "", val count: Int = 0, val id: String = "")
    : Parcelable
