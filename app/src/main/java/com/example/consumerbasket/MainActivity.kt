package com.example.consumerbasket

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), Removable, Updatable {

    private val dataBase = DBHelper(this)

    private var product: Product? = null
    private var productList: MutableList<Product> = mutableListOf()
    private var adapter: ListAdapter? = null
    private lateinit var productLiveData: ProductViewModel

    private lateinit var toolbar: Toolbar
    private lateinit var nameTV: EditText
    private lateinit var weightET: EditText
    private lateinit var priceET: EditText
    private lateinit var listViewLV: ListView
    private lateinit var checkAmountTV: TextView


    private lateinit var saveDataBTN: Button
    private lateinit var updateDataBTN:Button
    private lateinit var deleteDataBTN:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        productLiveData = ViewModelProvider(this)[ProductViewModel::class.java]
        adapter =  ListAdapter(this@MainActivity, productList)
        listViewLV.adapter = adapter

        readingDatabase()
        productLifeData()

        saveDataBTN.setOnClickListener{
            saveRecord()
        }

        listViewLV.setOnItemClickListener { _, _, position, _ ->
            product = adapter!!.getItem(position)
            val dialog = MyDialog()
            val args = Bundle()
            args.putSerializable("product", product)
            dialog.arguments = args
            dialog.show(supportFragmentManager, "custom")
        }

    }

    override fun onResume() {
        super.onResume()

        updateDataBTN.setOnClickListener{
            updateRecord()
        }

        deleteDataBTN.setOnClickListener{
            deleteRecord()
        }
    }

    private fun init() {
        toolbar = findViewById(R.id.toolbar)
        title = ""
        setSupportActionBar(toolbar)

        listViewLV = findViewById(R.id.listViewLV)
        nameTV = findViewById(R.id.nameET)
        weightET = findViewById(R.id.weightET)
        priceET = findViewById(R.id.priceET)

        checkAmountTV = findViewById(R.id.checkAmountTV)

        saveDataBTN = findViewById(R.id.saveDataBTN)
        updateDataBTN = findViewById(R.id.updateDataBTN)
        deleteDataBTN = findViewById(R.id.deleteDataBTN)

    }

    private fun saveRecord() {
        val product = Product(
            null,
            nameTV.text.toString(),
            weightET.text.toString().toDoubleOrNull(),
            priceET.text.toString().toIntOrNull()
        )

        if (!InputProductValidation(this, product).isValidate()) return

        clearEditText()

        dataBase.addData(product)
        Toast.makeText(
            this,
            getString(R.string.add_product_text, product.name), Toast.LENGTH_SHORT
        ).show()
        readingDatabase()
    }

    private fun updateRecord() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.apdate_dialog, null)
        dialogBuilder.setView(dialogView)

        val editID = dialogView.findViewById<EditText>(R.id.updateIdItemET)
        val editName = dialogView.findViewById<EditText>(R.id.updateNameET)
        val editWeight = dialogView.findViewById<EditText>(R.id.updateWeightET)
        val editPrice = dialogView.findViewById<EditText>(R.id.updatePriceET)

        dialogBuilder.setTitle(getString(R.string.update))
        dialogBuilder.setMessage(getString(R.string.input_data))
        dialogBuilder.setPositiveButton(getString(R.string.update_data)) { _, _ ->

            val product = product(editID, editName, editWeight, editPrice)

            val data = dataBase.readData()

            if (CheckProductId(this, product.productId).checkId(data) &&
                InputProductValidation(this, product).isValidate()) {
                dataBase.updateData(product)
                readingDatabase()
                Toast.makeText(
                    this,
                    getString(R.string.data_updated), Toast.LENGTH_SHORT
                ).show()
            }

        }
        dialogBuilder.setNegativeButton(getString(R.string.cancellation)) { dialog, whitch ->

        }
        dialogBuilder.create().show()
    }


    private fun deleteRecord() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.delete_dialog, null)
        dialogBuilder.setView(dialogView)

        val chooseDeleteId = dialogView.findViewById<EditText>(R.id.deleteItemIdET)

        dialogBuilder.setTitle(getString(R.string.delete_record))
        dialogBuilder.setMessage(getString(R.string.enter_the_identifier))
        dialogBuilder.setPositiveButton(getString(R.string.delete)) { _, _ ->
            val deleteId = chooseDeleteId.text.toString().toIntOrNull()

            val data = dataBase.readData()
            if (CheckProductId(this, deleteId).checkId(data)) {
                val product = Product(deleteId, "", null, null)
                dataBase.deleteData(product)
                readingDatabase()
                Toast.makeText(
                    this,
                    getString(R.string.record_deleted), Toast.LENGTH_SHORT
                ).show()
            }

        }
        dialogBuilder.setNegativeButton(getString(R.string.cancellation)) {dialog, whitch ->

        }
        dialogBuilder.create().show()

    }

    private fun product(
        editID: EditText,
        editName: EditText,
        editWeight: EditText,
        editPrice: EditText
    ): Product {
        val updateId = editID.text.toString()
        val updateName = editName.text.toString()
        val updateWeight = editWeight.text.toString()
        val updatePrice = editPrice.text.toString()

        val product = createProduct(updateId, updateName, updateWeight, updatePrice)
        return product
    }

    private fun createProduct(
        updateId: String,
        updateName: String,
        updateWeight: String,
        updatePrice: String
    ): Product {
        val product = Product(
            updateId.toIntOrNull(),
            updateName,
            updateWeight.toDoubleOrNull(),
            updatePrice.toIntOrNull(),
        )
        return product
    }

    private fun productLifeData() {
        productLiveData.productLiveData.observe(this, Observer { persons ->
            adapter?.clear()
            adapter?.addAll(persons)
            adapter?.notifyDataSetChanged()
        })
    }

    @SuppressLint("StringFormatMatches")
    private fun readingDatabase() {
        val currentList = dataBase.readData()
        productLiveData.productLiveData.value = currentList
        val checkAmount = currentList.sumOf { it.price ?: 0 }
        checkAmountTV.text = getString(R.string.total_amount_text_tv, checkAmount)
        dataBase.close()
    }

    private fun clearEditText() {
        nameTV.text.clear()
        weightET.text.clear()
        priceET.text.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.exitMenu) {
            Toast.makeText(this, getString(R.string.exit_programm_text), Toast.LENGTH_SHORT).show()
            //dataBase.removeAll()
            finishAffinity()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun remove(product: Product?) {
        dataBase.deleteData(product!!)
        readingDatabase()

        Toast.makeText(
            this,
            getString(R.string.record_deleted), Toast.LENGTH_SHORT
        ).show()
    }

    @SuppressLint("StringFormatMatches")
    override fun update(product: Product) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.apdate_dialog, null)
        dialogBuilder.setView(dialogView)

        val tableRow = dialogView.findViewById<TableRow>(R.id.inputIdTR)
        tableRow.visibility = View.GONE

        val productId = product.productId
        val editName = dialogView.findViewById<EditText>(R.id.updateNameET)
        val editWeight = dialogView.findViewById<EditText>(R.id.updateWeightET)
        val editPrice = dialogView.findViewById<EditText>(R.id.updatePriceET)

        dialogBuilder.setTitle(getString(R.string.update_id, product.productId))
        dialogBuilder.setMessage(getString(R.string.input_data))
        dialogBuilder.setPositiveButton(getString(R.string.update_data)) { _, _ ->

            val updateName = editName.text.toString()
            val updateWeight = editWeight.text.toString()
            val updatePrice = editPrice.text.toString()

            val product = createProduct(productId.toString(), updateName, updateWeight, updatePrice)

            if (InputProductValidation(this, product).isValidate()) {
                dataBase.updateData(product)
                readingDatabase()
                Toast.makeText(
                    this,
                    getString(R.string.data_updated), Toast.LENGTH_SHORT
                ).show()
            }

        }
        dialogBuilder.setNegativeButton(getString(R.string.cancellation)) { dialog, whitch ->

        }
        dialogBuilder.create().show()
    }

}


