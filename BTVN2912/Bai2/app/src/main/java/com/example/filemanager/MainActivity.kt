package com.example.filemanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
    private var selectedFile: File? = null // File đang được nhấn giữ
    private var fileToCopy: File? = null // File đang chờ copy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        val files = directory.listFiles()?.toList() ?: emptyList()

        adapter = FileAdapter(files,
            onFileClick = { file -> openFileOrFolder(file) },
            onFileLongClick = { file ->
                selectedFile = file
                openContextMenu(recyclerView) // Mở menu ngữ cảnh thủ công nếu cần
            }
        )
        recyclerView.adapter = adapter
        registerForContextMenu(recyclerView) // Đăng ký context menu cho list
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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> showCreateDialog(isFolder = true)
            2 -> showCreateDialog(isFolder = false)
            3 -> pasteFile()
        }
        return super.onOptionsItemSelected(item)
    }

    // 5. Context Menu: Đổi tên, Xóa, Copy [cite: 5, 8]
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.setHeaderTitle("Thao tác")
        menu?.add(0, 101, 0, "Đổi tên")
        menu?.add(0, 102, 0, "Xóa")
        if (selectedFile?.isFile == true) {
            menu?.add(0, 103, 0, "Sao chép")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val file = selectedFile ?: return false
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