package com.example.consumerbasket

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {



    private val db = DBHelper(this)

    private var productData: MutableList<Product> = mutableListOf()
    private var adapter: ListAdapter? = null
    private lateinit var productLiveData: ProductViewModel

    private lateinit var toolbar: Toolbar
    private lateinit var nameTV: EditText
    private lateinit var weightET: EditText
    private lateinit var priceET: EditText
    private lateinit var listViewLV: ListView
    private lateinit var checkAmountTV: TextView

    private lateinit var saveDataBTN: Button
    //private lateinit var updateDataBTN:Button
    //private lateinit var deleteDataBTN:Button

    @SuppressLint("StringFormatMatches")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        productLiveData = ViewModelProvider(this)[ProductViewModel::class.java]
        adapter =  ListAdapter(this@MainActivity, productData)
        listViewLV.adapter = adapter

        personLifeData()

        saveDataBTN.setOnClickListener{
            val product = Product(
                null,
                nameTV.text.toString(),
                weightET.text.toString().toDoubleOrNull(),
                priceET.text.toString().toIntOrNull()
            )

            if (!InputProductValidation(this, product).isValidate()) return@setOnClickListener

            clearEditText()

            db.addData(product)
            Toast.makeText(this,
                getString(R.string.add_product_text, product.name), Toast.LENGTH_SHORT).show()

            val currentList = db.readData()
            db.close()
            productLiveData.productLiveData.value = currentList

            val checkAmount = currentList.sumOf { it.price ?:0 }
            checkAmountTV.text = getString(R.string.total_amount_text_tv, checkAmount)

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
        //updateDataBTN = findViewById(R.id.updateDataBTN)
        //deleteDataBTN = findViewById(R.id.deleteDataBTN)
    }

    private fun personLifeData() {
        productLiveData.productLiveData.observe(this, Observer { persons ->
            adapter?.clear()
            adapter?.addAll(persons)
            adapter?.notifyDataSetChanged()
        })
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
            //db.removeAll()
            finishAffinity()
        }
        return super.onOptionsItemSelected(item)
    }

}