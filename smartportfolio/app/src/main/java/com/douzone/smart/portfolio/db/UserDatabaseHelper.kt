package com.douzone.smart.portfolio.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.douzone.smart.portfolio.data.user

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "userDatabase.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE if not exists user(" +
                "name text primary key," +
                "title text," +
                "contents text," +
                "image text," +
                "url text," +
                "viewType integer);"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE if exists user;"
        db?.execSQL(sql)
        onCreate(db)
    }

    fun insertData(data: user) {
        val query = "INSERT INTO user('name', 'title', 'contents', 'image', 'url', 'viewType') values('${data.name}', '${data.title}', '${data.contents}','${data.image}', '${data.url}', '${data.viewType}');"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun deleteData(name: String) {
        val query = "DELETE user WHERE name = '${name}';"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun updateData(data: user) {
        val query = "UPDATE user set name = '${data.name}', title = '${data.title}', contents = '${data.contents}', image = '${data.image}', url = '${data.url}', viewType = '${data.viewType}'"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun selectData(): ArrayList<user> {
        val db = this.readableDatabase
        val result = ArrayList<user>()

        val cursor = db.rawQuery("SELECT * FROM user", null)
        while(cursor.moveToNext()) {
            val rowData = user(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5))
            result.add(rowData)
        }

        cursor.close()

        return result
    }
}