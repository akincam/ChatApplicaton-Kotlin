package database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.messengerapp.User

class LastContactsDatabase(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME,null,1) {
    companion object {
        private val DATABASE_NAME = "Conversation"
        private val TABLE_CONTACTS = "LastContacts"
        private val KEY_USERNAME ="username"
        private val KEY_IMAGE ="imageurl"
    }
    override fun onCreate(p0: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_USERNAME + " TEXT,"
                + KEY_IMAGE + " TEXT"+ ")")
        p0?.execSQL(CREATE_CONTACTS_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }
    fun addContacts(user: User):Long {
        val db = this.writableDatabase
        val query = "select * from $TABLE_CONTACTS where $KEY_USERNAME = '${user.username}'"
        val c = db.rawQuery(query, arrayOf<String>())
        var success : Long = 3
        if (c.count == 0){
            val contentValues = ContentValues()
            contentValues.put(KEY_USERNAME, user.username)
            contentValues.put(KEY_IMAGE, user.profileImageUrl)
            success = db.insert(TABLE_CONTACTS, null, contentValues)
         }else{
            val query = "delete  from $TABLE_CONTACTS where $KEY_USERNAME = '${user.username}'"
            val c = db.execSQL(query, arrayOf<String>())
            val contentValues = ContentValues()
            contentValues.put(KEY_USERNAME, user.username)
            contentValues.put(KEY_IMAGE, user.profileImageUrl)
            success = db.insert(TABLE_CONTACTS, null, contentValues)
        }
        db.close()
        return success
    }
    fun getContacts() : ArrayList<User> {
        val list : ArrayList<User> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM ${TABLE_CONTACTS}"
        val result = db.rawQuery(query,null)
        var user : User? = null;
        if(result.moveToFirst()){
            do {
                user = User("",result.getString(result.getColumnIndex(
                    KEY_USERNAME
                )).toString(),result.getString(result.getColumnIndex(KEY_IMAGE)).toString())
                list.add(user)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }
}