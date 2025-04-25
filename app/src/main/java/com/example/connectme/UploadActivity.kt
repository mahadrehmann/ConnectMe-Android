package com.example.connectme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class UploadActivity : AppCompatActivity() {

    private lateinit var ivSelectedImage: ImageView
    private lateinit var gridViewImages: GridView
    private lateinit var tvNext: TextView

    private val imageUriList = mutableListOf<Uri>()
    private var selectedImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                loadGalleryImages()
            } else {
                Toast.makeText(this, "Permission denied to access gallery", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            loadGalleryImages()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        ivSelectedImage = findViewById(R.id.ivSelectedImage)
        gridViewImages = findViewById(R.id.gridViewImages)
        tvNext = findViewById(R.id.tvNext)

        checkAndRequestPermission()

        gridViewImages.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            selectedImageUri = imageUriList[position]
            ivSelectedImage.setImageURI(selectedImageUri)
        }

        tvNext.setOnClickListener {
            if (selectedImageUri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, PostActivity::class.java)
                intent.putExtra("selectedImageUri", selectedImageUri.toString())
                startActivity(intent)
            }
        }

        findViewById<BottomNavigationView>(R.id.bottomNavigation).setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        findViewById<ImageView>(R.id.openCamera).setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadGalleryImages() {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(uriExternal, projection, null, null, sortOrder)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            //limit to recent 40 images
            var count = 0
            while (cursor.moveToNext() && count < 40) {
                val imageId = cursor.getLong(columnIndex)
                val imageUri = Uri.withAppendedPath(uriExternal, imageId.toString())
                imageUriList.add(imageUri)
                count++
            }
        }

        val adapter = GalleryAdapter(this, imageUriList)
        gridViewImages.adapter = adapter

        if (imageUriList.isNotEmpty()) {
            selectedImageUri = imageUriList[0]
            ivSelectedImage.setImageURI(selectedImageUri)
        }
    }
}
