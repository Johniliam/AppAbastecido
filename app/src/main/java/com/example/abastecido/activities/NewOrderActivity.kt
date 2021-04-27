package com.example.abastecido.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.hardware.input.InputManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abastecido.R
import com.example.abastecido.adapters.NewOrderAdapter
import com.example.abastecido.data_class.Articulo
import com.example.abastecido.databinding.ActivityNewOrderBinding
import java.util.*

class NewOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewOrderBinding
    private val articuloFiltered = mutableListOf<Articulo>()

    //borrar al integrar api
    val articulosPrueba = mutableListOf(
        Articulo("Aceite", 10, "https://image.freepik.com/foto-gratis/aceite-cocina-botella-plastico-blanco_35712-553.jpg"),
        Articulo("Pan", 1, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Zanahoria", 12, "https://img.freepik.com/foto-gratis/zanahoria-entera-aislada_88281-1988.jpg?size=626&ext=jpg"),
        Articulo("Pan", 13, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Aceite", 14, "https://image.freepik.com/foto-gratis/aceite-cocina-botella-plastico-blanco_35712-553.jpg"),
        Articulo("Pan", 5, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Zanahoria", 16, "https://img.freepik.com/foto-gratis/zanahoria-entera-aislada_88281-1988.jpg?size=626&ext=jpg"),
        Articulo("Aceite", 7, "https://image.freepik.com/foto-gratis/aceite-cocina-botella-plastico-blanco_35712-553.jpg"),
        Articulo("Pan", 8, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Zanahoria", 9, "https://img.freepik.com/foto-gratis/zanahoria-entera-aislada_88281-1988.jpg?size=626&ext=jpg"),
        Articulo("Pan", 10, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Aceite", 11, "https://image.freepik.com/foto-gratis/aceite-cocina-botella-plastico-blanco_35712-553.jpg"),
        Articulo("Pan", 2, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Zanahoria", 13, "https://img.freepik.com/foto-gratis/zanahoria-entera-aislada_88281-1988.jpg?size=626&ext=jpg")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        initRecycler()
        title = getString(R.string.new_order)


    }

    //RecyclerView Initializer
    fun initRecycler(){
        articuloFiltered.addAll(articulosPrueba)
        binding.rvStorageList.layoutManager = LinearLayoutManager(this)
        val adapter = NewOrderAdapter(articuloFiltered)
        binding.rvStorageList.adapter = adapter
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
                        articulosPrueba.forEach{
                            if (it.articuloNombre.toLowerCase(Locale.getDefault()).contains(search.toLowerCase(Locale.getDefault()))) {
                                articuloFiltered.add(it)
                            }
                        }
                        binding.rvStorageList.adapter!!.notifyDataSetChanged()
                    }
                    else{
                        articuloFiltered.clear()
                        articuloFiltered.addAll(articulosPrueba)
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
                .setPositiveButton(getString(R.string.finalizate), DialogInterface.OnClickListener{
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

    private fun goToInventary(){
        val intent = Intent(this, InventaryActivity::class.java)
        startActivity(intent)
    }
    
}