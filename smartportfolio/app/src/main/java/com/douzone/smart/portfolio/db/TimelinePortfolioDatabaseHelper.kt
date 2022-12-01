package com.douzone.smart.portfolio.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.douzone.smart.portfolio.data.Timeline

class TimelinePortfolioDatabaseHelper(context: Context): SQLiteOpenHelper(context, "userDatabase.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE if not exists TimelinePortfolio(" +
                "id primary key autoincrement," +
                "name text," +
                "title text," +
                "contents text," +
                "date text," +
                "image text," +
                "url text," +
                "circleColor integer);"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE if exists TimelinePortfolio;"
        db?.execSQL(sql)
        onCreate(db)
    }

    fun insertData(data: Timeline) {
        val query = "INSERT INTO TimelinePortfolio('name', 'title', 'contents', 'date', 'image', 'url', 'circleColor') " +
                "values('${data.name}', '${data.title}', '${data.contents}', '${data.date}', '${data.image}', '${data.url}', '${data.circleColor}');"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun deletePortfolio(id: Int) {
        val query = "DELETE TimelinePortfolio WHERE id = '${id}';"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun deleteData(name: String) {
        val query = "DELETE TimelinePortfolio WHERE name = '${name}';"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun updateData(data: Timeline) {
        val query = "UPDATE TimelinePortfolio set name = '${data.name}', title = '${data.title}', contents = '${data.contents}', date = '${data.date}', image = '${data.image}', url = '${data.url}', circleColor = '${data.circleColor}'"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun selectData(name: String): ArrayList<Timeline> {
        val db = this.readableDatabase
        val result = ArrayList<Timeline>()

        val cursor = db.rawQuery("SELECT * FROM TimelinePortfolio WHERE name = '${name}'", null)
        while(cursor.moveToNext()) {
            val rowData = Timeline(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7))
            result.add(rowData)
        }
        cursor.close()

        return result
    }
}