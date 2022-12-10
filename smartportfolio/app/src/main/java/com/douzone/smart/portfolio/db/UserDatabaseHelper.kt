package com.douzone.smart.portfolio.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import com.douzone.smart.portfolio.data.User

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "userDatabase.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE if not exists user(" +
                "name text primary key,"+
                "profileImage blob,"+
                "viewType integer);"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE if exists user;"
        db?.execSQL(sql)
        onCreate(db)
    }

    fun insertData(data: User) {
        val db = this.writableDatabase
        val query = "INSERT INTO user('name', 'profileImage', 'viewType') values(?, ?, ${data.viewType});"
        val p = db.compileStatement(query)
        p.bindString(1, data.name)
        p.bindBlob(2, data.profileImage)
        p.execute()
        p.close()
        db.close()
    }

    fun deleteData(name: String) {
        val query = "DELETE FROM user WHERE name = '${name}';"
        val db = this.writableDatabase
        db.execSQL(query)
        db.close()
    }

    fun updateData(data: User) {
        val db = this.writableDatabase
        val query = "UPDATE user set name = '${data.name}', profileImage = ?, viewType = '${data.viewType}' WHERE name = '${data.name}'"
        val p = db.compileStatement(query)
        p.bindBlob(1, data.profileImage)
        p.execute()
        p.close()
        db.close()
    }

    fun checkUser(name: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM user WHERE name = '$name'", null)
        while(cursor.moveToNext()) {
            return true
        }
        return false
    }

    fun selecetUser(name: String): User? {
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM user WHERE name = '$name'", null)
        while(cursor.moveToNext()) {
            return User(cursor.getString(0), cursor.getBlob(1), cursor.getInt(2))
        }
        return null
    }

    fun selectData(): ArrayList<User> {
        val db = this.readableDatabase
        val result = ArrayList<User>()

        val cursor = db.rawQuery("SELECT * FROM user", null)
        while(cursor.moveToNext()) {
            val rowData = User(cursor.getString(0), cursor.getBlob(1), cursor.getInt(2))
            result.add(rowData)
        }

        cursor.close()

        return result
    }
}