package com.example.abastecido

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abastecido.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArticuloAdapter

    //test api
    private val productsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchBar.setOnQueryTextListener(this)
        initRecycler()

    }

    //RecyclerView Initializer
    private fun initRecycler(){
        adapter = ArticuloAdapter(productsList)
        binding.rvStorageList.layoutManager = LinearLayoutManager(this)
        binding.rvStorageList.adapter = adapter
    }

    private fun getRetrofit():Retrofit{
        return Retrofit.Builder()
                .baseUrl("https://dog.ceo/api/breed/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                //.baseUrl("https://super.walmart.com.mx/api/assembler/v2/page/")
                //.addConverterFactory(GsonConverterFactory.create())
                //.build()
    }

    //hilo secundario
    private fun searchByName(query:String){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getJSONObject("$query/images")
            val puppies = call.body()

            //val call = getRetrofit().create(APIService::class.java).getJSONObject("search?Ntt=$query&Nrpp=10&offSet=0&storeId=0000009999&profileId=NA") //Nrpp = articles that search
            //val objeto = call.body()
            //parse information
            //val ja = objeto?.root
            //Log.d("ObjetoJSON",objeto.toString())
            //val jo = ja?.getJSONObject("ResultsList")
            //val je = jo?.getJSONObject("content")
            //val arrayJSON = je?.getJSONObject("0")
            //var listItems = mutableListOf<Articulo>()
//
            //for (i in 0 until arrayJSON?.length()!!) {
//
            //    val id = arrayJSON.getString("id").toString()
            //    val price = arrayJSON.getInt("skuPrice")
//
            //    if (id != null || price != null){
            //        listItems.add(Articulo(query, price, "https://res.cloudinary.com/walmart-labs/image/upload/w_960,dpr_auto,f_auto,q_auto:best/gr/images/product-images/img_large/${id}L.jpg"))
            //    }
            //    else{
            //        print("Item Null")
            //    }
//
            //}
//
            runOnUiThread {
                if (call.isSuccessful){
                    //show Recycler view
                    val images = puppies?.root ?: emptyList()
                    productsList.clear()
                    productsList.addAll(images)
                    adapter.notifyDataSetChanged()
                }else{
                    //show error
                    showError()
                }
            }

        }
    }

    private fun showError(){
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()){
            searchByName(query.toLowerCase())
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }
}