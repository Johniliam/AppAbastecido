package com.example.abastecido.adapters

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.abastecido.R
import com.example.abastecido.data_class.Articulo
import com.example.abastecido.databinding.OrderItemArticuloBinding
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ModifyInventaryAdapter (val articulo: MutableList<Articulo>):RecyclerView.Adapter<ModifyInventaryAdapter.ArticuloHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ArticuloHolder(layoutInflater.inflate(R.layout.order_item_articulo, parent, false))
    }

    override fun getItemCount(): Int = articulo.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ArticuloHolder, position: Int) {
        holder.render(articulo[position])
    }

    class ArticuloHolder(val view: View):RecyclerView.ViewHolder(view){
        val binding = OrderItemArticuloBinding.bind(view)

        val dataReference = FirebaseDatabase.getInstance().getReference("images")

        @RequiresApi(Build.VERSION_CODES.O)
        fun render(articulo: Articulo){
            binding.tvArticuloName.text = articulo.articuloNombre

            val drawable: Drawable = binding.tvStock.background
            val color : PorterDuffColorFilter

            when(articulo.stock){
                in 0..2 -> color = PorterDuffColorFilter(ContextCompat.getColor(view.context, R.color.red), PorterDuff.Mode.SRC)
                in 3..999 -> color =PorterDuffColorFilter(ContextCompat.getColor(view.context, R.color.green), PorterDuff.Mode.SRC)
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

            view.setOnClickListener {
                if (binding.tvStock.text.toString() != ""){
                    saveDB(articulo.key,binding.tvStock.text.toString().toInt())
                }
            }

        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun saveDB(key: String, stock: Int){
            dataReference.child(key).child("stock").setValue(stock)
            val currentDateTime = LocalDateTime.now()
            val time = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            dataReference.child(key).child("updated_at").setValue(time)
            binding.tvStock.background.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(view.context, R.color.white), PorterDuff.Mode.SRC)
            binding.tvStock.isClickable = false
        }

    }
}
