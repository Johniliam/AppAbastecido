package com.example.abastecido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.abastecido.databinding.ItemArticuloBinding
import com.squareup.picasso.Picasso

class ArticuloAdapter (val articulo: List<String>):RecyclerView.Adapter<ArticuloAdapter.ArticuloHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ArticuloHolder(layoutInflater.inflate(R.layout.item_articulo, parent, false))
    }

    override fun getItemCount(): Int = articulo.size

    override fun onBindViewHolder(holder: ArticuloHolder, position: Int) {
        //holder.render(articulo[position])
        val item = articulo[position]
        holder.render(item)
    }

    class ArticuloHolder(val view: View):RecyclerView.ViewHolder(view){
        val binding = ItemArticuloBinding.bind(view)

        fun render(image: String){

            //binding.tvArticuloName.text = articulo.articuloNombre
            //when(articulo.stock){
            //    in 0..2 -> binding.llStockColor.setBackgroundColor(ContextCompat.getColor(view.context, R.color.red))
            //    in 3..6 -> binding.llStockColor.setBackgroundColor(ContextCompat.getColor(view.context, R.color.yellow))
            //    in 7..99 -> binding.llStockColor.setBackgroundColor(ContextCompat.getColor(view.context, R.color.green))
            //    else -> binding.llStockColor.setBackgroundColor(ContextCompat.getColor(view.context, R.color.black))
            //}
            //binding.tvStock.text = articulo.stock.toString()
            Picasso.get().load(image).into(binding.ivArticulo)
            //view.setOnClickListener { Toast.makeText(view.context, "Has seleccionado a ${articulo.articuloNombre}", Toast.LENGTH_SHORT).show() }
        }
    }
}
