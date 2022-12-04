package com.douzone.smart.portfolio.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.douzone.smart.portfolio.data.User

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "userDatabase.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE if not exists user(" +
                "name text primary key,"+
                "viewType integer);"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE if exists user;"
        db?.execSQL(sql)
        onCreate(db)
    }

    fun insertData(data: User) {
        val query = "INSERT INTO user('name', 'viewType') values('${data.name}', '${data.viewType}');"
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

    fun updateData(data: User) {
        val query = "UPDATE user set name = '${data.name}', viewType = '${data.viewType}'"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun selectData(): ArrayList<User> {
        val db = this.readableDatabase
        val result = ArrayList<User>()

        val cursor = db.rawQuery("SELECT * FROM user", null)
        while(cursor.moveToNext()) {
            val rowData = User(cursor.getString(0), cursor.getInt(1))
            result.add(rowData)
        }

        cursor.close()

        return result
    }
}