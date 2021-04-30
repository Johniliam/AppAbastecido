package com.example.abastecido.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abastecido.R
import com.example.abastecido.adapters.InventaryAdapter
import com.example.abastecido.data_class.Articulo
import com.example.abastecido.databinding.ActivityInventaryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


enum class ProviderType{
    BASIC
}

class InventaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventaryBinding
    private val articuloFiltered = mutableListOf<Articulo>()

    val imageRef = FirebaseStorage.getInstance().reference

    //borrar al integrar api
    val articulosDB = mutableListOf<Articulo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        title = getString(R.string.inv)

        binding.fbNewOrder.setOnClickListener {
            goToNewOrder()
        }

        initRecycler()
        getListFiles()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.edit -> goToEdit()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToEdit(){
        val intent = Intent(this, ModifyInventaryActivity::class.java)
        startActivity(intent)
    }

    private fun goToNewOrder(){
        val intent = Intent(this, NewOrderActivity::class.java)
        startActivity(intent)
    }

    //RecyclerView Initializer
    //cambiar al integrar api
    private fun initRecycler(){
        binding.rvStorageList.layoutManager = LinearLayoutManager(this)
        val adapter = InventaryAdapter(articuloFiltered)
        binding.rvStorageList.adapter = adapter
    }

    private fun getListFiles() = CoroutineScope(Dispatchers.IO).launch {
        try {

            val ref = FirebaseDatabase.getInstance().reference.child("images")
            ref.addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (dsp in dataSnapshot.children){
                                val key = dsp.key
                                val name = dsp.child("articuloNombre").value.toString()
                                val image = dsp.child("imagen").value.toString()
                                val stock = dsp.child("stock").value.toString().toInt()
                                articulosDB.add(Articulo(name,stock,image))
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            //handle databaseError
                        }
                    })
            withContext(Dispatchers.Main){
                articuloFiltered.clear()
                articuloFiltered.addAll(articulosDB)

                binding.rvStorageList.adapter?.notifyDataSetChanged()

            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@InventaryActivity, getString(R.string.imagesdbError), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.inventary_menu, menu)
        val menuItem = menu!!.findItem(R.id.search)

        if(menuItem != null){
            val searchView = menuItem.actionView as SearchView

            searchView.queryHint = "Buscar..."

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    closeKeyboard(binding.rvStorageList)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(newText!!.isNotEmpty()){

                        articuloFiltered.clear()
                        val search = newText.toLowerCase(Locale.getDefault())
                        articulosDB.forEach{
                            if (it.articuloNombre.toLowerCase(Locale.getDefault()).contains(search.toLowerCase(Locale.getDefault()))) {
                                articuloFiltered.add(it)
                                Log.d("Añadido", "El articulo añadido fue: ${it.articuloNombre}")
                            }
                        }
                        binding.rvStorageList.adapter!!.notifyDataSetChanged()
                    }
                    else{
                        articuloFiltered.clear()
                        articuloFiltered.addAll(articulosDB)
                        binding.rvStorageList.adapter!!.notifyDataSetChanged()
                    }
                    return true
                }
            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun closeKeyboard(view: View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }

}