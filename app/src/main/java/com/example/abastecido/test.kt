package com.example.abastecido

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.abastecido.data_class.Articulo
import com.example.abastecido.databinding.ActivityTestBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class test : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    private var imageReference = FirebaseStorage.getInstance().reference.child("images")
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
        Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
    }

    private fun reloadActivity(){
        finish()
        startActivity(intent)
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

        val fileName = binding.edtFileName.text.toString()

        if (!validateInputFileName(fileName)) {
            return
        }

        binding.tvFileLoad.visibility = View.VISIBLE

        fileRef = imageReference!!.child(fileName + "." + getFileExtension(filepath))
        fileRef!!.putFile(filepath)
            .addOnSuccessListener { taskSnapshot ->
                val name = taskSnapshot.metadata!!.name?.substringBefore(".")
                taskSnapshot.storage.downloadUrl.addOnSuccessListener(OnSuccessListener<Uri?> {
                    if (it != null) {
                        val uri = it

                        Log.e(tag, "Url: $uri")
                        Log.e(tag, "Name: ${taskSnapshot.metadata!!.name}")
                        val meta = "${taskSnapshot.metadata!!.path} - ${taskSnapshot.metadata!!.sizeBytes / 1024} KBs"
                        binding.tvFileLoad.text = meta

                        writeNewImageInfoToDB(name!!, uri.toString())

                        Toast.makeText(this, "File Uploaded ", Toast.LENGTH_LONG).show()

                        reloadActivity()

                    }else{
                        showError()
                        reloadActivity()
                    }
                })
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            } //progress bar
            .addOnProgressListener { taskSnapshot ->
                // progress percentage
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount

                // percentage in progress
                val intProgress = progress.toInt()
                val prog = "Uploaded $intProgress%..."
                binding.tvFileLoad.text = prog
            }
            .addOnPausedListener { System.out.println("Upload is paused!") }

    }

    private fun writeNewImageInfoToDB(name: String, url: String) {
        val key = dataReference.push().key.toString()

        val info = Articulo(key, name, 0, url,"")

        dataReference.child(key).setValue(info)
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