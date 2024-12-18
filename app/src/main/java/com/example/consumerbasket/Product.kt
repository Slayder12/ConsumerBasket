package com.example.consumerbasket

import android.content.Context
import android.widget.Toast
import java.io.Serializable

class Product(
    var productId: Int?,
    var name: String,
    var weight: Double?,
    var price: Int?
) : Serializable{
    override fun toString(): String {
        return "(id продукта: №$productId)"
    }
}

class InputProductValidation(private val context: Context, private val person: Product) {
    fun isValidate(): Boolean {

        if (person.name.isEmpty() && person.weight == null && person.price == null) {
            Toast.makeText(context,
                "Введите все поля", Toast.LENGTH_SHORT).show()
            return false
        }

        if (person.name.isEmpty()) {
            Toast.makeText(context, "Введите имя", Toast.LENGTH_SHORT).show()
            return false
        }
        if (person.name.length !in 2..171) {
            Toast.makeText(context,
                "Название должно быть от 2 до 171 символов", Toast.LENGTH_SHORT).show()
            return false
        }

        if (person.weight == null) {
            Toast.makeText(context,
                "Введите вес", Toast.LENGTH_SHORT).show()
            return false
        }

        if (person.weight!! < 0.001 || person.weight!! > 100.0){
            Toast.makeText(context, "Вес должен быть от 0.001 до 100 кг.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (person.price == null) {
            Toast.makeText(context,
                "Введите цену", Toast.LENGTH_SHORT).show()
            return false
        }
        if (person.price !in 1..500000) {
            Toast.makeText(context, "Цена должна быть от 1 до 500000 руб.", Toast.LENGTH_SHORT).show()
            return false
        }
        
        return true
    }

}

class CheckProductId(private val context: Context, private val id: Int?){

    fun checkId(productList: MutableList<Product>): Boolean {
        if (id == null) {
            Toast.makeText(
                context,
                "Введите id", Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (productList.any { it.productId == id }){
            return true
        }
        Toast.makeText(context, "Такого id не существует", Toast.LENGTH_SHORT).show()
        return false
    }

}