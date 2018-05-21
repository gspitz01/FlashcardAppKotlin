/*
 * Copyright (C) 2018 Greg Spitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import com.gregspitz.flashcardappkotlin.data.source.remote.FlashcardRemoteDataSource
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
    fun provideFlashcardLocalDataSource(flashcardDao: FlashcardDao) : FlashcardLocalDataSource {
        return FlashcardLocalDataSource(flashcardDao)
    }

    @Provides @Singleton
    fun provideFlashcardRemoteDataSource() : FlashcardRemoteDataSource {
        return FlashcardRemoteDataSource()
    }

    @Provides @Singleton
    fun provideFlashcardRepository(flashcardLocalDataSource: FlashcardLocalDataSource,
                                   flashcardRemoteDataSource: FlashcardRemoteDataSource)
            : FlashcardRepository {
        return FlashcardRepository(flashcardLocalDataSource, flashcardRemoteDataSource)
    }
}
