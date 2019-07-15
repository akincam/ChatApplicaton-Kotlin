package database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.messengerapp.User

class LoginUserDatabase(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME,null,1) {
    companion object {
        private val DATABASE_NAME = "LoginUser"
        private val TABLE_CONTACTS = "UserTable"
        private val KEY_EMAIL = "email"
        private val KEY_PASSWORD ="password"
        private val KEY_USERNAME = "username"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_EMAIL + " TEXT,"
                + KEY_PASSWORD + " TEXT,"+
                  KEY_USERNAME + " TEXT"+ ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }

    fun addUser(user: UserInfo):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_EMAIL, user.email)
        contentValues.put(KEY_PASSWORD,user.password )
        contentValues.put(KEY_USERNAME,user.userName)
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        db.close()
        return success
    }

    fun deleteUser(){
        val db = this.writableDatabase
        db.execSQL("delete from "+ TABLE_CONTACTS);
    }

    fun getUser() : UserInfo? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_CONTACTS"
        val result = db.rawQuery(query,null)
        var user : UserInfo? = null;
        if(result.moveToFirst()){
            do {
                user = UserInfo(result.getString(result.getColumnIndex(KEY_EMAIL)).toString(),result.getString(result.getColumnIndex(
                    KEY_PASSWORD)).toString(),result.getString(result.getColumnIndex(KEY_USERNAME)).toString())
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return user
        }
}

class UserInfo(){
    constructor(email : String,password : String,userName : String) : this() {
        this.email = email
        this.password = password
        this.userName = userName
    }
    lateinit var email : String
    lateinit var password : String
    lateinit var userName: String

}