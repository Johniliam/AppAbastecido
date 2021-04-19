package com.example.abastecido

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.abastecido.databinding.InventaryItemArticuloBinding
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class InventaryAdapter (val articulo: List<Articulo>):RecyclerView.Adapter<InventaryAdapter.ArticuloHolder>() {

    lateinit var articuloFilterList : List<Articulo>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ArticuloHolder(layoutInflater.inflate(R.layout.inventary_item_articulo, parent, false))

    }

    override fun getItemCount(): Int = articulo.size

    override fun onBindViewHolder(holder: ArticuloHolder, position: Int) {
        holder.render(articulo[position])
    }

    class ArticuloHolder(val view: View): RecyclerView.ViewHolder(view){
        val binding = InventaryItemArticuloBinding.bind(view)

        fun render(articulo: Articulo){
            binding.tvArticuloName.text = articulo.articuloNombre

            val drawable: Drawable = binding.tvStock.background
            val color : PorterDuffColorFilter

            when(articulo.stock){
                in 0..2 -> color = PorterDuffColorFilter(ContextCompat.getColor(view.context, R.color.red), PorterDuff.Mode.SRC)
                in 3..99 -> color = PorterDuffColorFilter(ContextCompat.getColor(view.context, R.color.green), PorterDuff.Mode.SRC)
                else -> color = PorterDuffColorFilter(ContextCompat.getColor(view.context, R.color.black), PorterDuff.Mode.SRC)
            }
            drawable.colorFilter = color
            binding.tvStock.background = drawable

            //just 2 digits on the qty
            when(articulo.stock.toString().length){
                1 -> binding.tvStock.hint = "0${articulo.stock}"
                2 -> binding.tvStock.hint = articulo.stock.toString()
                else -> binding.tvStock.hint = "99"
            }

            Picasso.get().load(articulo.imagen).into(binding.ivArticulo)
            view.setOnClickListener { Toast.makeText(view.context, "Has seleccionado a ${articulo.articuloNombre}", Toast.LENGTH_SHORT).show() }
        }
    }

}