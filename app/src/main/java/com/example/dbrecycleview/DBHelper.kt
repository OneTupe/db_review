package com.example.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Todo(val id: Int, val title: String, val isDone: Boolean)

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "tododb"
        const val TABLE_NAME = "todos"
        const val KEY_ID = "id"
        const val KEY_TITLE = "title"
        const val KEY_IS_DONE = "is_done"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_NAME (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_TITLE TEXT NOT NULL,
                $KEY_IS_DONE INTEGER NOT NULL
            )""")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getAll(): List<Todo> {
        val result = mutableListOf<Todo>()
        val database = this.writableDatabase
        val cursor: Cursor = database.query(
            TABLE_NAME, null, null, null,
            null, null, null
        )
        if (cursor.moveToFirst()) {
            val idIndex: Int = cursor.getColumnIndex(KEY_ID)
            val titleIndex: Int = cursor.getColumnIndex(KEY_TITLE)
            val isDoneIndex: Int = cursor.getColumnIndex(KEY_IS_DONE)
            do {
                val todo = Todo(
                    cursor.getInt(idIndex),
                    cursor.getString(titleIndex),
                    cursor.getInt(isDoneIndex) == 1
                )
                result.add(todo)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    fun add(title: String, isDone: Boolean = false) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, title)
        contentValues.put(KEY_IS_DONE, if (isDone) 1 else 0)
        database.insert(TABLE_NAME, null, contentValues)
        close()
    }

    fun update(id: Int, title: String, isDone: Boolean) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, title)
        contentValues.put(KEY_IS_DONE, if (isDone) 1 else 0)
        database.update(TABLE_NAME, contentValues, "$KEY_ID = ?", arrayOf(id.toString()))
        close()
    }

    fun remove(id: Int) {
        val database = this.writableDatabase
        database.delete(TABLE_NAME, "$KEY_ID = ?", arrayOf(id.toString()))
        close()
    }

    fun remove() {
        val database = this.writableDatabase
        database.delete(TABLE_NAME, null, null)
        close()
    }
}