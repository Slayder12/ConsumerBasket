package com.example.consumerbasket

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_NAME = "PRODUCT_DATABASE"
        private val DATABASE_VERSION = 1

        val TABLE_NAME = "product_table"
        val KEY_ID = "Id"
        val KEY_NAME = "name"
        val KEY_WEIGHT = "weight"
        val KEY_PRICE = "price"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("  +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_NAME + " TEXT, " +
                KEY_WEIGHT + " TEXT, " +
                KEY_PRICE + " TEXT" + ")")
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    fun addData(product: Product){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, product.productId)
        contentValues.put(KEY_NAME, product.name)
        contentValues.put(KEY_WEIGHT, product.weight.toString())
        contentValues.put(KEY_PRICE, product.price.toString())

        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    @SuppressLint("Range", "Recycle")
    fun readData(): MutableList<Product> {
        val productList: MutableList<Product> = mutableListOf()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return productList
        }
        var productId: Int
        var productName: String
        var productWeight: String
        var productPrice: String
        if (cursor.moveToFirst()){
            do {
                productId = cursor.getInt(cursor.getColumnIndex("Id"))
                productName = cursor.getString(cursor.getColumnIndex("name"))
                productWeight = cursor.getString(cursor.getColumnIndex("weight"))
                productPrice = cursor.getString(cursor.getColumnIndex("price"))

                val product = Product(productId, productName, productWeight.toDouble(), productPrice.toInt())
                productList.add(product)

            } while (cursor.moveToNext())
        }
        return productList

    }

    fun updateData(product: Product){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, product.productId)
        contentValues.put(KEY_NAME, product.name)
        contentValues.put(KEY_WEIGHT, product.weight.toString())
        contentValues.put(KEY_PRICE, product.price.toString())

        db.update(TABLE_NAME, contentValues,"id=" + product.productId, null)
        db.close()
    }

    fun deleteData(product: Product){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, product.productId)
        db.delete(TABLE_NAME,"id=" + product.productId, null)
        db.close()
    }

    fun removeAll(){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
    }


}