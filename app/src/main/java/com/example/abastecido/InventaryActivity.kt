package com.example.abastecido

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abastecido.databinding.ActivityInventaryBinding
import java.util.*

class InventaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventaryBinding
    private val articuloFiltered = mutableListOf<Articulo>()

    //borrar al integrar api
    val articulosPrueba = listOf(
        Articulo("Aceite", 4, "https://image.freepik.com/foto-gratis/aceite-cocina-botella-plastico-blanco_35712-553.jpg"),
        Articulo("Pan", 7, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Zanahoria", 8, "https://img.freepik.com/foto-gratis/zanahoria-entera-aislada_88281-1988.jpg?size=626&ext=jpg"),
        Articulo("Pan", 2, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Aceite", 7, "https://image.freepik.com/foto-gratis/aceite-cocina-botella-plastico-blanco_35712-553.jpg"),
        Articulo("Pan", 7, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Zanahoria", 99, "https://img.freepik.com/foto-gratis/zanahoria-entera-aislada_88281-1988.jpg?size=626&ext=jpg"),
        Articulo("Aceite", 102, "https://image.freepik.com/foto-gratis/aceite-cocina-botella-plastico-blanco_35712-553.jpg"),
        Articulo("Pan", 12, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Zanahoria", 9, "https://img.freepik.com/foto-gratis/zanahoria-entera-aislada_88281-1988.jpg?size=626&ext=jpg"),
        Articulo("Pan", 10, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Aceite", 1, "https://image.freepik.com/foto-gratis/aceite-cocina-botella-plastico-blanco_35712-553.jpg"),
        Articulo("Pan", 12, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Zanahoria", 3, "https://img.freepik.com/foto-gratis/zanahoria-entera-aislada_88281-1988.jpg?size=626&ext=jpg")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        title = getString(R.string.inv)
        initRecycler()

        binding.fbNewOrder.setOnClickListener {
            goToNewOrder()
        }
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
        articuloFiltered.addAll(articulosPrueba)
        binding.rvStorageList.layoutManager = LinearLayoutManager(this)
        val adapter = InventaryAdapter(articuloFiltered)
        binding.rvStorageList.adapter = adapter
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
                        articulosPrueba.forEach{
                            if (it.articuloNombre.toLowerCase(Locale.getDefault()).contains(search.toLowerCase(Locale.getDefault()))) {
                                articuloFiltered.add(it)
                                Log.d("Añadido", "El articulo añadido fue: ${it.articuloNombre}")
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

    private fun closeKeyboard(view: View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }

}