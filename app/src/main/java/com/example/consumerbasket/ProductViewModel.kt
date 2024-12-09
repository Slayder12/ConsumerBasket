package com.example.consumerbasket

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProductViewModel: ViewModel() {
    val productLiveData: MutableLiveData<MutableList<Product>> = MutableLiveData(mutableListOf())
}