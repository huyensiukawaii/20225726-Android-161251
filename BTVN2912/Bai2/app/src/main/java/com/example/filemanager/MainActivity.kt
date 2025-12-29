package com.example.filemanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvPath: TextView
    private lateinit var adapter: FileAdapter
    private var currentPath: File = Environment.getExternalStorageDirectory()
    private var fileToCopy: File? = null // File đang chờ copy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        tvPath = findViewById(R.id.tvPath)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        checkPermission()
    }

    // 1. Kiểm tra quyền truy cập bộ nhớ
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            } else {
                loadFiles(currentPath)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
            } else {
                loadFiles(currentPath)
            }
        }
    }

    // 2. Hiển thị danh sách file
    private fun loadFiles(directory: File) {
        tvPath.text = directory.absolutePath
        val isRoot = directory.absolutePath == Environment.getExternalStorageDirectory().absolutePath
        supportActionBar?.setDisplayHomeAsUpEnabled(!isRoot)
        supportActionBar?.title = directory.name
        val files = directory.listFiles()?.toList() ?: emptyList()

        adapter = FileAdapter(files,
            onFileClick = { file -> openFileOrFolder(file) },
            onMoreClick = { file, view -> showPopupMenu(file, view) }
        )
        recyclerView.adapter = adapter
    }
    
    private fun showPopupMenu(file: File, view: View) {
        val popup = PopupMenu(this, view)
        popup.menu.add(0, 101, 0, "Đổi tên")
        popup.menu.add(0, 102, 0, "Xóa")
        if (file.isFile) {
            popup.menu.add(0, 103, 0, "Sao chép")
        }
        popup.setOnMenuItemClickListener { item ->
            onContextItemSelected(item, file)
            true
        }
        popup.show()
    }

    // 3. Xử lý mở File hoặc Folder
    private fun openFileOrFolder(file: File) {
        if (file.isDirectory) {
            currentPath = file
            loadFiles(currentPath)
        } else {
            openFileContent(file)
        }
    }

    private fun openFileContent(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)

        val extension = MimeTypeMap.getFileExtensionFromUrl(file.name)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"

        // Chỉ hỗ trợ text và ảnh theo yêu cầu [cite: 7]
        if (mimeType.startsWith("text/") || mimeType.startsWith("image/")) {
            intent.setDataAndType(uri, mimeType)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Không tìm thấy ứng dụng mở file này", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Chỉ hỗ trợ xem Text hoặc Ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    // 4. Option Menu: Tạo mới [cite: 9]
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 1, 0, "Tạo thư mục mới")
        menu?.add(0, 2, 0, "Tạo file văn bản mới")
        if (fileToCopy != null) menu?.add(0, 3, 0, "Dán file đã copy") // Nút Paste
        menu?.add(0, 4, 0, "Tạo dữ liệu test") // Thêm tùy chọn tạo dữ liệu test
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Gọi lại hàm xử lý nút Back vật lý chúng ta đã viết
                return true
            }
            1 -> showCreateDialog(isFolder = true)
            2 -> showCreateDialog(isFolder = false)
            3 -> pasteFile()
            4 -> createTestData()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onContextItemSelected(item: MenuItem, file: File): Boolean {
        when (item.itemId) {
            101 -> showRenameDialog(file)
            102 -> showDeleteDialog(file)
            103 -> {
                fileToCopy = file
                Toast.makeText(this, "Đã copy. Hãy đến thư mục đích và chọn 'Dán' từ menu góc phải", Toast.LENGTH_LONG).show()
                invalidateOptionsMenu() // Cập nhật menu để hiện nút Paste
            }
        }
        return true
    }

    // --- CÁC HÀM XỬ LÝ DIALOG & LOGIC ---

    private fun createTestData() {
        val testFolder = File(Environment.getExternalStorageDirectory(), "TestFolder")
        if (!testFolder.exists()) testFolder.mkdir()

        val subFolder = File(testFolder, "SubFolder")
        if (!subFolder.exists()) subFolder.mkdir()

        val testFile1 = File(testFolder, "TestFile1.txt")
        if (!testFile1.exists()) testFile1.writeText("Đây là nội dung của file test 1.")

        val testFile2 = File(subFolder, "TestFile2.txt")
        if (!testFile2.exists()) testFile2.writeText("Đây là nội dung của file test 2.")

        val imageFile = File(testFolder, "TestImage.jpg")
        if (!imageFile.exists()) {
            // Bạn cần phải có một ảnh mẫu trong thư mục drawable với tên "sample_image"
            val inputStream = resources.openRawResource(R.drawable.sample_image)
            inputStream.use { input ->
                imageFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        loadFiles(currentPath)
        Toast.makeText(this, "Đã tạo dữ liệu test!", Toast.LENGTH_SHORT).show()
    }

    // Dialog tạo mới (Thư mục hoặc File) [cite: 9]
    private fun showCreateDialog(isFolder: Boolean) {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle(if (isFolder) "Tạo thư mục" else "Tạo file text")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val name = input.text.toString()
                if (name.isNotEmpty()) {
                    val newFile = File(currentPath, if (isFolder) name else "$name.txt")
                    if (isFolder) newFile.mkdir() else newFile.createNewFile()
                    loadFiles(currentPath)
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    // Dialog Đổi tên [cite: 5, 8]
    private fun showRenameDialog(file: File) {
        val input = EditText(this)
        input.setText(file.name)
        AlertDialog.Builder(this)
            .setTitle("Đổi tên")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val newName = input.text.toString()
                val newFile = File(file.parent, newName)
                if (file.renameTo(newFile)) {
                    loadFiles(currentPath)
                    Toast.makeText(this, "Đổi tên thành công", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    // Dialog Xóa (Xác nhận AlertDialog) [cite: 5, 8]
    private fun showDeleteDialog(file: File) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa ${file.name}?")
            .setPositiveButton("Xóa") { _, _ ->
                file.deleteRecursively() // Xóa cả thư mục con nếu có
                loadFiles(currentPath)
                Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    // Logic Copy - Paste [cite: 8]
    private fun pasteFile() {
        val src = fileToCopy ?: return
        val dst = File(currentPath, src.name)

        AlertDialog.Builder(this)
            .setTitle("Sao chép")
            .setMessage("Sao chép ${src.name} vào đây?")
            .setPositiveButton("Đồng ý") { _, _ ->
                try {
                    src.copyTo(dst, overwrite = true)
                    loadFiles(currentPath)
                    fileToCopy = null
                    invalidateOptionsMenu()
                    Toast.makeText(this, "Sao chép thành công", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Lỗi sao chép: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    // Xử lý nút Back để quay lại thư mục cha
    override fun onBackPressed() {
        if (currentPath.absolutePath != Environment.getExternalStorageDirectory().absolutePath) {
            currentPath = currentPath.parentFile ?: Environment.getExternalStorageDirectory()
            loadFiles(currentPath)
        } else {
            super.onBackPressed()
        }
    }
}
