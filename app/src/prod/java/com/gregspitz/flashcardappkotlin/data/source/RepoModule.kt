package com.gregspitz.flashcardappkotlin.data.source

import android.app.Application
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.gregspitz.flashcardappkotlin.InitialData
import com.gregspitz.flashcardappkotlin.data.source.local.FlashcardDao
import com.gregspitz.flashcardappkotlin.data.source.local.FlashcardDatabase
import com.gregspitz.flashcardappkotlin.data.source.local.FlashcardLocalDataSource
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executors
import javax.inject.Singleton

/**
 * Dagger module for prod FlashcardRepository
 */
@Module
class RepoModule {

    @Provides @Singleton
    fun provideFlashcardDatabase(application: Application) : FlashcardDatabase {
        return Room.databaseBuilder(application,
                FlashcardDatabase::class.java, "flashcard.db")
                .addCallback(object: RoomDatabase.Callback() {

                    /*
                     * Add initial data to the database
                     */
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Executors.newSingleThreadExecutor().execute({
                            for (flashcard in InitialData.flashcards) {
                                val contentValues = ContentValues()
                                contentValues.put("id", flashcard.id)
                                contentValues.put("front", flashcard.front)
                                contentValues.put("back", flashcard.back)
                                db.insert("flashcard", SQLiteDatabase.CONFLICT_REPLACE,
                                        contentValues)
                            }
                        })

                    }
                })
                .build()
    }

    @Provides @Singleton
    fun provideFlashcardDao(flashcardDatabase: FlashcardDatabase) : FlashcardDao {
        return flashcardDatabase.flashcardDao()
    }

    @Provides @Singleton
    fun provideFlashcardLocalDataSource(flashcardDao: FlashcardDao) : FlashcardDataSource {
        return FlashcardLocalDataSource(flashcardDao)
    }

    @Provides @Singleton
    fun provideFlashcardRepository(flashcardDataSource: FlashcardDataSource) : FlashcardRepository {
        return FlashcardRepository(flashcardDataSource)
    }
}
