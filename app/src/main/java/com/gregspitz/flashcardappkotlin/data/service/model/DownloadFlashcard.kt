package com.gregspitz.flashcardappkotlin.data.service.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DownloadFlashcard(val id: String = "",
                             val categoryName: String = "",
                             val front: String = "", val back: String = "") : Parcelable
