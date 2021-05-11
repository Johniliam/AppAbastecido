package com.example.abastecido.adapters

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.contentValuesOf
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.abastecido.R
import com.example.abastecido.activities.InventaryActivity
import com.example.abastecido.activities.NewOrderActivity
import com.example.abastecido.data_class.Articulo
import com.example.abastecido.databinding.OrderItemArticuloBinding
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class NewOrderAdapter (private val articulo: MutableList<Articulo>):RecyclerView.Adapter<NewOrderAdapter.ArticuloHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ArticuloHolder(layoutInflater.inflate(R.layout.order_item_articulo, parent, false), activity = NewOrderActivity())
    }

    override fun getItemCount(): Int = articulo.size

    override fun onBindViewHolder(holder: ArticuloHolder, position: Int) {
        holder.render(articulo[position], position)
    }

    class ArticuloHolder(private val view: View, private val activity: NewOrderActivity):RecyclerView.ViewHolder(view){
        val binding = OrderItemArticuloBinding.bind(view)

        val dataReference = FirebaseDatabase.getInstance().getReference("images")

        fun render(articulo: Articulo, pos: Int){
            binding.tvArticuloName.text = articulo.articuloNombre
            
            val drawable: Drawable = binding.tvStock.background
            drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(view.context, R.color.green), PorterDuff.Mode.SRC)
            binding.tvStock.background = drawable

            Picasso.get().load(articulo.imagen).into(binding.ivArticulo)

            view.setOnClickListener {
                if (binding.tvStock.text.toString() != ""){
                    val sum = binding.tvStock.text.toString().toInt() + articulo.stock
                saveDB(articulo.key,sum)
                }
            }

        }

        fun saveDB(key: String, stock: Int){
            dataReference.child(key).child("stock").setValue(stock)
            binding.tvStock.background.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(view.context, R.color.white), PorterDuff.Mode.SRC)
            binding.tvStock.isClickable = false
        }

    }
}
