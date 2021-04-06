package com.example.abastecido

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abastecido.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //borrar al integrar api
    val articulosPrueba = listOf(
        Articulo("Aceite", 0, "https://image.freepik.com/foto-gratis/aceite-cocina-botella-plastico-blanco_35712-553.jpg"),
        Articulo("Pan", 1, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Zanahoria", 2, "https://img.freepik.com/foto-gratis/zanahoria-entera-aislada_88281-1988.jpg?size=626&ext=jpg"),
        Articulo("Pan", 3, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Aceite", 4, "https://image.freepik.com/foto-gratis/aceite-cocina-botella-plastico-blanco_35712-553.jpg"),
        Articulo("Pan", 5, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Zanahoria", 6, "https://img.freepik.com/foto-gratis/zanahoria-entera-aislada_88281-1988.jpg?size=626&ext=jpg"),
        Articulo("Aceite", 7, "https://image.freepik.com/foto-gratis/aceite-cocina-botella-plastico-blanco_35712-553.jpg"),
        Articulo("Pan", 8, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Zanahoria", 9, "https://img.freepik.com/foto-gratis/zanahoria-entera-aislada_88281-1988.jpg?size=626&ext=jpg"),
        Articulo("Pan", 10, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Aceite", 11, "https://image.freepik.com/foto-gratis/aceite-cocina-botella-plastico-blanco_35712-553.jpg"),
        Articulo("Pan", 12, "https://zainduzaitez.com/wp-content/uploads/2013/11/pan_de_molde2.jpg"),
        Articulo("Zanahoria", 13, "https://img.freepik.com/foto-gratis/zanahoria-entera-aislada_88281-1988.jpg?size=626&ext=jpg")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()

    }

    //RecyclerView Initializer
    //cambiar al integrar api
    fun initRecycler(){
        binding.rvStorageList.layoutManager = LinearLayoutManager(this)
        val adapter = ArticuloAdapter(articulosPrueba)
        binding.rvStorageList.adapter = adapter
    }
}