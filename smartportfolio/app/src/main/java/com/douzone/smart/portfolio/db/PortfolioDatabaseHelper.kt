package com.douzone.smart.portfolio.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.douzone.smart.portfolio.data.Portfolio
import com.douzone.smart.portfolio.data.User

class PortfolioDatabaseHelper(context: Context): SQLiteOpenHelper(context, "CardPortfolio.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE if not exists portfolio(" +
                "id integer primary key autoincrement," +
                "name text," +
                "title text," +
                "contents text," +
                "image text," +
                "url text);"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE if exists portfolio;"
        db?.execSQL(sql)
        onCreate(db)
    }

    fun insertData(data: Portfolio) {
        val query = "INSERT INTO portfolio('name', 'title', 'contents', 'image', 'url') values('${data.name}', '${data.title}', '${data.contents}','${data.image}', '${data.url}');"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun deletePortfolio(id: Int) {
        val query = "DELETE FROM portfolio WHERE id = '${id}';"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun deleteData(name: String) {
        val query = "DELETE FROM portfolio WHERE name = '${name}';"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun updateData(data: Portfolio) {
        val query = "UPDATE portfolio set name = '${data.name}', title = '${data.title}', contents = '${data.contents}', image = '${data.image}', url = '${data.url}'"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun selectData(name: String): ArrayList<Portfolio> {
        val db = this.readableDatabase
        val result = ArrayList<Portfolio>()

        val cursor = db.rawQuery("SELECT * FROM portfolio WHERE name = '${name}'", null)
        while(cursor.moveToNext()) {
            val rowData = Portfolio(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5))
            result.add(rowData)
        }
        cursor.close()

        return result
    }
}