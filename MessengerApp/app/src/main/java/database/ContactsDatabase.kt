package database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.example.messengerapp.User
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import com.google.android.gms.common.util.IOUtils.toByteArray
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.sql.Blob
import android.database.sqlite.SQLiteException




class ContactsDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null,1) {
    companion object {
        private val DATABASE_NAME = "CONCTACTS"
        private val TABLE_CONTACTS = "ContactsTable"
        private val KEY_USERNAME ="username"
        private val KEY_IMAGE ="imageurl"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + ContactsDatabase.TABLE_CONTACTS + "("
                + " TEXT,"+ ContactsDatabase.KEY_USERNAME + " TEXT,"
                + ContactsDatabase.KEY_IMAGE + " BLOB" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }
    fun insertContacs(user : ContactInfoDb) : Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_USERNAME,user.username)
        contentValues.put(KEY_IMAGE,user.image)
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        db.close()
        return success
    }

    fun getContacts() : ArrayList<ContactInfoDb>{
        val list : ArrayList<ContactInfoDb> = ArrayList<ContactInfoDb>()
        val db = this.readableDatabase
        val query = "SELECT * FROM ${TABLE_CONTACTS}"
        val cursor = db.rawQuery(query,null)
        if(cursor!=null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    list.add(ContactInfoDb(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE))))
                } while (cursor.moveToNext());
                cursor.close();
            }

        }
        db.close()
        return list
    }
    fun getContactsCount() :Int {
        val countQuery : String = "SELECT * FROM " + TABLE_CONTACTS;
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null);
        val count = cursor.getCount()
        cursor.close()
        return count
    }
    fun checkIsDataAlreadyInDBorNot(value : String): Boolean {
        var cursor: Cursor? = null
        val db = this.readableDatabase
        cursor = db.rawQuery("select * from " + TABLE_CONTACTS + " WHERE " + KEY_USERNAME + "='" + value + "'", null)
        val count = cursor.count
        return count>0
    }
}

class ContactInfoDb{
    var username : String
    var image : ByteArray
    constructor(username : String , image : ByteArray){
        this.username = username
        this. image = image
    }
}