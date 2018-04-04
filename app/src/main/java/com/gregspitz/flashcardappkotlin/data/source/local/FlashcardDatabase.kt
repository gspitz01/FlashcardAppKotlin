package com.gregspitz.flashcardappkotlin.data.source.local

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.gregspitz.flashcardappkotlin.InitialData
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import java.util.concurrent.Executors

/**
 * Room database for flashcards
 */
@Database(entities = arrayOf(Flashcard::class), version = 1)
abstract class FlashcardDatabase : RoomDatabase() {

    abstract fun flashcardDao(): FlashcardDao

    companion object {
        @Volatile private var instance: FlashcardDatabase? = null

        fun getInstance(context: Context): FlashcardDatabase? {
            if (instance == null) {
                synchronized(FlashcardDatabase::class) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(context.applicationContext,
                                FlashcardDatabase::class.java, "flashcard.db")
                                .addCallback(object: Callback() {
                                    override fun onCreate(db: SupportSQLiteDatabase) {
                                        super.onCreate(db)
                                        Executors.newSingleThreadExecutor().execute(Runnable {
                                            getInstance(context)!!.flashcardDao()
                                                    .insertFlashcards(InitialData.flashcards)
                                        })
                                    }
                                })
                                .build()
                    }
                }
            }

            return instance
        }

        fun detroyInstance() {
            instance = null
        }
    }
}
