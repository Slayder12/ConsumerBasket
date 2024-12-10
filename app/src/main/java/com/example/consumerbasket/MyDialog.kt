package com.example.consumerbasket

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class MyDialog: DialogFragment() {
    private var removable: Removable? = null
    private var updatable: Updatable? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        removable = context as Removable
        updatable = context as Updatable
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val product = requireArguments().getSerializable("product")
        val builder = android.app.AlertDialog.Builder(
            requireActivity()
        )

        return builder
            .setTitle("Внимание!")
            .setMessage("Выберите действие $product")
            .setPositiveButton("Изменить") { dialog, which ->
                updatable?.update(product as Product)
            }
            .setNeutralButton("Отмена", null)
            .setNegativeButton("Удалить", ){ dialog, which ->
                removable?.remove(product as Product)
            }.create()
    }

}