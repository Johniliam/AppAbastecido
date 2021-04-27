package com.example.abastecido

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.abastecido.data_class.Articulo
import com.example.abastecido.databinding.ActivityTestBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class test : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    private var imageReference: StorageReference? = null
    private var fileRef: StorageReference? = null

    val dataReference = FirebaseDatabase.getInstance().getReference("images")

    private val tag = "StorageActivity"

    lateinit var filepath : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btLoad.setOnClickListener {
            startFileChooser()
            Toast.makeText(this, "Imagen Cargada", Toast.LENGTH_SHORT).show()
        }

        binding.btUpload.setOnClickListener {
            uploadFile()
            Toast.makeText(this, "Imagen Subida", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showError(){
        Toast.makeText(this, "Error al descargar la imagen", Toast.LENGTH_SHORT).show()
    }

    private fun startFileChooser(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Chose Picture"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 111 && resultCode == Activity.RESULT_OK && data != null){
            filepath = data.data!!
            try {
                filepath.let{
                    if(Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, filepath)
                        binding.ivDownload.setImageBitmap(bitmap)
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, filepath)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        binding.ivDownload.setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadFile() {

        if (filepath != null) {
            val fileName = binding.edtFileName.text.toString()

            if (!validateInputFileName(fileName)) {
                return
            }

            binding.tvFileLoad.visibility = View.VISIBLE

            fileRef = imageReference!!.child(fileName + "." + getFileExtension(filepath!!))
            fileRef!!.putFile(filepath!!)
                .addOnSuccessListener { taskSnapshot ->
                    val name = taskSnapshot.metadata!!.name
                    val url = taskSnapshot.storage.downloadUrl.toString()

                    Log.e(tag, "Uri: ${taskSnapshot.storage.downloadUrl}")
                    Log.e(tag, "Name: ${taskSnapshot.metadata!!.name}")
                    binding.tvFileLoad.text = "${taskSnapshot.metadata!!.path} - ${taskSnapshot.metadata!!.sizeBytes / 1024} KBs"

                    writeNewImageInfoToDB(name!!, url)

                    Toast.makeText(this, "File Uploaded ", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    // progress percentage
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount

                    // percentage in progress
                    val intProgress = progress.toInt()
                    binding.tvFileLoad.text = "Uploaded $intProgress%..."
                }
                .addOnPausedListener { System.out.println("Upload is paused!") }

        } else {
            Toast.makeText(this, "No File!", Toast.LENGTH_LONG).show()
        }
    }

    private fun writeNewImageInfoToDB(name: String, url: String) {
        val info = Articulo(name, 0, url)

        val key = dataReference!!.push().key
        if (key != null) {
            dataReference!!.child(key).setValue(info)
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun validateInputFileName(fileName: String): Boolean {
        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(this, "Enter file name!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

}