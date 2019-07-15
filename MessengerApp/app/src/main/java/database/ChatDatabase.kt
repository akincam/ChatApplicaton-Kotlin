package database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import co.intentservice.chatui.models.ChatMessage
import java.lang.reflect.Type

class ChatDatabase(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME,null,1) {

    companion object {
        private val DATABASE_NAME = "Chat"
        private val KEY_MESSAGE = "message"
        private val KEY_TIMESTAMPT = "timestampt"
        private val KEY_TYPE = "type"
        private val KEY_SENDER = "sender"

    }

    override fun onCreate(p0: SQLiteDatabase?) {
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun createTable(tableName: String) {
        var db = this.writableDatabase

        db.execSQL("CREATE TABLE IF NOT EXISTS  $tableName(message TEXT, timestampt TEXT, type TEXT, sender TEXT);")
    }

    fun getMessages(tableName: String): ArrayList<ChatMessage> {
        val list: ArrayList<ChatMessage> = ArrayList<ChatMessage>()
        val db = this.readableDatabase
        val query = "SELECT * FROM ${tableName}"
        val result = db.rawQuery(query, null)
        var user: ChatMessage
        if (result.moveToFirst()) {
            do {
                if (result.getString(result.getColumnIndex(KEY_TYPE)).toString().equals("SENT")) {
                    user = ChatMessage(
                        result.getString(result.getColumnIndex(KEY_MESSAGE)).toString(),
                        result.getLong(result.getColumnIndex(KEY_TIMESTAMPT)), ChatMessage.Type.SENT
                    )
                } else {
                    user = ChatMessage(
                        result.getString(result.getColumnIndex(KEY_MESSAGE)).toString(),
                        result.getLong(result.getColumnIndex(KEY_TIMESTAMPT)), ChatMessage.Type.RECEIVED
                    )
                }
                list.add(user)

            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun addMessage(chat: ChatMessage,tableName: String) : Boolean{
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(KEY_MESSAGE, chat.message)
            contentValues.put(KEY_TIMESTAMPT, chat.timestamp)
            if(chat.type == ChatMessage.Type.SENT) {
                contentValues.put(KEY_TYPE, "SENT")
            }else {
                contentValues.put(KEY_TYPE, "RECEIVED")
            }
            val success = db.insert(tableName, null, contentValues)
            db.close()
            return true
        }
    }



