package com.example.abastecido.adapters

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.abastecido.R
import com.example.abastecido.data_class.Articulo
import com.example.abastecido.databinding.OrderItemArticuloBinding
import com.squareup.picasso.Picasso

class NewOrderAdapter (private val articulo: MutableList<Articulo>):RecyclerView.Adapter<NewOrderAdapter.ArticuloHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ArticuloHolder(layoutInflater.inflate(R.layout.order_item_articulo, parent, false))
    }

    override fun getItemCount(): Int = articulo.size

    override fun onBindViewHolder(holder: ArticuloHolder, position: Int) {
        holder.render(articulo[position])
    }

    class ArticuloHolder(private val view: View):RecyclerView.ViewHolder(view){
        val binding = OrderItemArticuloBinding.bind(view)

        fun render(articulo: Articulo){
            binding.tvArticuloName.text = articulo.articuloNombre

            val drawable: Drawable = binding.tvStock.background
            drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(view.context, R.color.green), PorterDuff.Mode.SRC)
            binding.tvStock.background = drawable

            binding.tvStock.hint = "00"

            Picasso.get().load(articulo.imagen).into(binding.ivArticulo)
            view.setOnClickListener { Toast.makeText(view.context, "Has seleccionado a ${articulo.articuloNombre}", Toast.LENGTH_SHORT).show() }

        }

    }
}
