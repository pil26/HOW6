package com.example.viladomar

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private val DATABASE_NAME = "VilaDoMarDatabase" // Nome do banco de dados
        private val DATABASE_VERSION = 1 // Versão do banco de dados
        private val TABLE_CONTACTS = "VilaDoMarTable" // Nome da tabela
        private val KEY_ID = "_id" // Nome da coluna ID
        private val KEY_NAME = "_name" // Nome da coluna nome
        private val KEY_EMAIL = "_email" // Nome da coluna email
        private val KEY_PHONE = "_phone" // Nome da coluna telefone
        private val KEY_ADDRESS = "_address" // Nome da coluna endereço
    }

    // Criação da tabela
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = "CREATE TABLE $TABLE_CONTACTS($KEY_ID INTEGER PRIMARY KEY, $KEY_ADDRESS TEXT, $KEY_NAME TEXT, $KEY_EMAIL TEXT, $KEY_PHONE TEXT);"
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    // Atualização da tabela caso exista
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS;")
        onCreate(db)
    }

    // Inserir um registro no banco de dados
    fun addEmployee(emp:EmpModel) : Long{
        val db = this.writableDatabase
        val contentValue = ContentValues()

        contentValue.put(KEY_ADDRESS, emp.address) // Inserir endereço no ContentValues
        contentValue.put(KEY_NAME, emp.name) // Inserir nome no ContentValues
        contentValue.put(KEY_EMAIL, emp.email) // Inserir email no ContentValues
        contentValue.put(KEY_PHONE, emp.phone) // Inserir telefone no ContentValues

        val success = db.insert(TABLE_CONTACTS, null, contentValue)
        db.close()
        return success
    }

    // Exibir todos os registros no aplicativo
    fun showEmployees(): ArrayList<EmpModel>{
        val db = this.readableDatabase
        val empList = ArrayList<EmpModel>()
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS;"

        var cursor : Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch(e: SQLException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        if(cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL))
                val phone = cursor.getString(cursor.getColumnIndex(KEY_PHONE))
                val address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS))
                empList.add(EmpModel(id, name, email, phone, address))
            }while(cursor.moveToNext())
        }
        cursor.close()
        return empList
    }

    // Excluir um registro do banco de dados
    fun deleteEmployee(emp:EmpModel):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(KEY_ID, emp.id)

        val success = db.delete(TABLE_CONTACTS, "$KEY_ID = ${emp.id}", null)
        db.close()
        return success
    }

    // Editar (atualizar) um registro do banco de dados
    fun updateEmployee(emp: EmpModel):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(KEY_NAME, emp.name)
        contentValues.put(KEY_EMAIL, emp.email)
        contentValues.put(KEY_PHONE, emp.phone)
        contentValues.put(KEY_ADDRESS, emp.address)

        val success = db.update(TABLE_CONTACTS, contentValues, "$KEY_ID = ${emp.id}", null)
        db.close()
        return success
    }
}
