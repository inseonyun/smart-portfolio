package com.douzone.smart.portfolio.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.douzone.smart.portfolio.data.Messenger
import com.douzone.smart.portfolio.data.Portfolio

class MessengerPortfolioDatabaseHelper(context: Context): SQLiteOpenHelper(context, "userDatabase.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE if not exists MessengerPortfolio(" +
                "id primary key autoincrement," +
                "name text," +
                "title text," +
                "contents text," +
                "image text," +
                "url text);"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE if exists MessengerPortfolio;"
        db?.execSQL(sql)
        onCreate(db)
    }

    fun insertData(data: Messenger) {
        val query = "INSERT INTO MessengerPortfolio('name', 'title', 'contents', 'image', 'url') values('${data.name}', '${data.title}', '${data.contents}','${data.image}', '${data.url}');"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun deletePortfolio(id: Int) {
        val query = "DELETE FROM MessengerPortfolio WHERE id = '${id}';"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun deleteData(name: String) {
        val query = "DELETE FROM MessengerPortfolio WHERE name = '${name}';"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun updateData(data: Messenger) {
        val query = "UPDATE FROM MessengerPortfolio set name = '${data.name}', title = '${data.title}', contents = '${data.contents}', image = '${data.image}', url = '${data.url}'"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun selectData(name: String): ArrayList<Messenger> {
        val db = this.readableDatabase
        val result = ArrayList<Messenger>()

        val cursor = db.rawQuery("SELECT * FROM MessengerPortfolio WHERE name = '${name}'", null)
        while(cursor.moveToNext()) {
            val rowData = Messenger(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5))
            result.add(rowData)
        }
        cursor.close()

        return result
    }
}