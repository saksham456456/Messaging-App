package com.synq.app.data.local
import androidx.room.Database
import androidx.room.RoomDatabase
import com.synq.app.data.local.dao.ChatDao
import com.synq.app.data.local.dao.MessageDao
import com.synq.app.data.local.entity.ChatEntity
import com.synq.app.data.local.entity.MessageEntity
@Database(entities = [ChatEntity::class, MessageEntity::class], version = 1, exportSchema = false) abstract class SynqDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}
