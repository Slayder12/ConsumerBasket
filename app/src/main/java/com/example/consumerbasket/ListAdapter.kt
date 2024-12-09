package com.example.consumerbasket;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ListAdapter(context: Context, productList: MutableList<Product>) :
    ArrayAdapter<Product>(context, R.layout.list_item, productList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val product = getItem(position)
        if (view == null) {
            view = LayoutInflater.from(context)
                .inflate(R.layout.list_item, parent, false)
        }

        val idItemTV = view?.findViewById<TextView>(R.id.idItemTV)
        val nameItemTV = view?.findViewById<TextView>(R.id.nameItemTV)
        val weightItemTV = view?.findViewById<TextView>(R.id.weightItemTV)
        val priceItemTV = view?.findViewById<TextView>(R.id.priceItemTV)

        idItemTV?.text = product?.productId.toString()
        nameItemTV?.text = product?.name
        weightItemTV?.text = product?.weight.toString()
        priceItemTV?.text = product?.price.toString()

        return view!!
    }
}