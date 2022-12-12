package com.douzone.smart.portfolio.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.douzone.smart.portfolio.data.Card

class CardPortfolioDatabaseHelper(context: Context): SQLiteOpenHelper(context, "CardPortfolio.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE if not exists CardPortfolio(" +
                "id integer primary key autoincrement," +
                "name text," +
                "title text," +
                "contents text," +
                "image blob," +
                "defaultImage integer," +
                "url text);"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE if exists CardPortfolio;"
        db?.execSQL(sql)
        onCreate(db)
    }

    fun insertData(data: Card) {
        val db = this.writableDatabase
        val query = "INSERT INTO CardPortfolio('name', 'title', 'contents', 'image', 'defaultImage', 'url') values('${data.name}', '${data.title}', '${data.contents}', ? , '${data.defaultImage}', '${data.url}');"

        val p = db.compileStatement(query)
        p.bindBlob(1, data.image)
        p.execute()
        p.close()
        db.close()
    }

    fun deletePortfolio(id: Int) {
        val query = "DELETE FROM CardPortfolio WHERE id = '${id}';"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun deleteData(name: String) {
        val query = "DELETE FROM CardPortfolio WHERE name = '${name}';"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun updateData(data: Card) {
        val db = this.writableDatabase
        val query = "UPDATE CardPortfolio set name = '${data.name}', title = '${data.title}', contents = '${data.contents}', image = ?, defaultImage = '${data.defaultImage}', url = '${data.url}' WHERE id = '${data.id}'"
        val p = db.compileStatement(query)
        p.bindBlob(1, data.image)
        p.execute()
        p.close()
        db.close()
    }

    fun selectData(name: String): ArrayList<Card> {
        val db = this.readableDatabase
        val result = ArrayList<Card>()

        val cursor = db.rawQuery("SELECT * FROM CardPortfolio WHERE name = '${name}'", null)
        while(cursor.moveToNext()) {
            val rowData = Card(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getBlob(4), cursor.getInt(5), cursor.getString(6))
            result.add(rowData)
        }
        cursor.close()

        return result
    }
}