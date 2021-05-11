package com.example.abastecido.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abastecido.R
import com.example.abastecido.adapters.NewOrderAdapter
import com.example.abastecido.data_class.Articulo
import com.example.abastecido.databinding.ActivityNewOrderBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class NewOrderActivity() : AppCompatActivity(){

    override fun onBackPressed() {
        Log.d("CDA", "onBackPressed Called")
        showCancelAlert()
    }

    private lateinit var binding: ActivityNewOrderBinding

    private lateinit var adapter: NewOrderAdapter

    private val articuloFiltered = mutableListOf<Articulo>()
    val articulosDB = mutableListOf<Articulo>()

    val dataReference = FirebaseDatabase.getInstance().getReference("images")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.new_order)

        setSupportActionBar(binding.toolbar)

        initRecycler()
        getListFiles()
    }

    //RecyclerView Initializer
    fun initRecycler(){
        binding.rvStorageList.layoutManager = LinearLayoutManager(this)
        adapter = NewOrderAdapter(articuloFiltered)
        binding.rvStorageList.adapter = adapter
    }

    private fun getListFiles() = CoroutineScope(Dispatchers.IO).launch {
        val ref = FirebaseDatabase.getInstance().reference.child("images")
        ref.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (dsp in dataSnapshot.children){
                            val key = dsp.key.toString()
                            val name = dsp.child("articuloNombre").value.toString()
                            val image = dsp.child("imagen").value.toString()
                            val stock = dsp.child("stock").value.toString()
                            if (stock != ""){
                                articulosDB.add(Articulo(key,name.replace("_", "\n"),stock.toInt(),image))
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        //handle databaseError
                        Toast.makeText(this@NewOrderActivity, "Error in data download", Toast.LENGTH_SHORT).show()
                    }
                })
        delay(1000)
        runOnUiThread {
            articuloFiltered.addAll(articulosDB)

            binding.rvStorageList.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.neworder_menu, menu)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.finishOrder -> showConfirmAlert()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun closeKeyboard(view: View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }

    private fun showConfirmAlert(){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(getString(R.string.save_order))
                .setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener{
                    dialog, id -> dialog.cancel()
                })
                .setPositiveButton(getString(R.string.acept), DialogInterface.OnClickListener{
                    //save order in firebase
                    dialog, id -> run {

                    Toast.makeText(this, getString(R.string.order_saved), Toast.LENGTH_SHORT).show()
                    goToInventary()

                }
                })
        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.finalizar_orden))
        alert.show()
    }

    private fun showCancelAlert(){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("¿Desea cancelar la nueva orden?\nTodos los cambios se perderán")
                .setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener{
                    dialog, id -> dialog.cancel()
                })
                .setPositiveButton(getString(R.string.acept), DialogInterface.OnClickListener{
                    dialog, id -> run {
                    undoDB()
                    goToInventary()
                }
                })
        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.finalizar_orden))
        alert.show()
    }

    private fun undoDB(){

        for (info in articulosDB){

            dataReference.child(info.key).child("stock").setValue(info.stock)

        }
    }

    private fun goToInventary(){
        val intent = Intent(this, InventaryActivity::class.java)
        startActivity(intent)
    }

}