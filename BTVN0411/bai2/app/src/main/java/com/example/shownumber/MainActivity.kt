package com.example.shownumber

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.shownumber.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val numberList = ArrayList<Int>()
    private val numberAdapter: ArrayAdapter<Int> by lazy {
        ArrayAdapter(this, android.R.layout.simple_list_item_1, numberList)
    }

    // --- PHẦN SỬA LỖI LAG ---
    // Thêm biến này để kiểm soát, ngăn vòng lặp vô tận
    private var isUpdatingCheck = false
    // -----------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listViewNumbers.adapter = numberAdapter
        binding.radioEven.isChecked = true

        setupInputListener()
        setupRadioListeners() // <--- Hàm này đã được sửa lại
        updateNumberList()
    }

    private fun setupInputListener() {
        binding.editTextNumber.doOnTextChanged { _, _, _, _ ->
            updateNumberList()
        }
    }

    // --- PHẦN SỬA LỖI LAG ---
    /**
     * Thiết lập listener cho 2 RadioGroup.
     * Logic này dùng biến 'isUpdatingCheck' để đảm bảo việc clearCheck()
     * không kích hoạt lại listener một cách đệ quy (gây lag/treo).
     */
    private fun setupRadioListeners() {
        val listener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
            // Nếu code đang tự động clearCheck(), thì bỏ qua, không làm gì cả
            if (isUpdatingCheck) return@OnCheckedChangeListener

            // Chỉ xử lý khi người dùng *chọn* một nút
            if (checkedId != -1) {
                // Xác định group còn lại
                val groupToClear = if (group.id == binding.radioGroupRow1.id) {
                    binding.radioGroupRow2
                } else {
                    binding.radioGroupRow1
                }

                // Đặt cờ "bận", báo cho code biết là ta sắp tự động clearCheck
                isUpdatingCheck = true
                // Xóa chọn của group còn lại
                groupToClear.clearCheck()
                // Xong việc, gỡ cờ "bận"
                isUpdatingCheck = false

                // Bây giờ mới cập nhật danh sách
                updateNumberList()
            }
        }

        // Gán *cùng một* listener cho cả 2 group
        binding.radioGroupRow1.setOnCheckedChangeListener(listener)
        binding.radioGroupRow2.setOnCheckedChangeListener(listener)
    }
    // -----------------------

    /**
     * Hàm trung tâm cập nhật danh sách dựa trên lựa chọn
     */
    private fun updateNumberList() {
        val input = binding.editTextNumber.text.toString()
        val n = input.toIntOrNull()

        if (n == null) {
            numberList.clear()
            toggleEmptyView(false)
            numberAdapter.notifyDataSetChanged()
            return
        }

        val results = when {
            binding.radioEven.isChecked -> getEvenNumbers(n)
            binding.radioOdd.isChecked -> getOddNumbers(n)
            binding.radioSquare.isChecked -> getSquareNumbers(n)
            binding.radioPrime.isChecked -> getPrimeNumbers(n)
            binding.radioPerfect.isChecked -> getPerfectNumbers(n)
            binding.radioFibonacci.isChecked -> getFibonacciNumbers(n)
            else -> emptyList()
        }

        numberList.clear()
        numberList.addAll(results)
        numberAdapter.notifyDataSetChanged()

        toggleEmptyView(numberList.isEmpty())
    }

    /**
     * Helper để bật/tắt TextView "Không có số nào" và ListView
     */
    private fun toggleEmptyView(showEmpty: Boolean) {
        binding.textViewEmpty.visibility = if (showEmpty) View.VISIBLE else View.GONE
        binding.listViewNumbers.visibility = if (showEmpty) View.GONE else View.VISIBLE
    }

    // --- Các hàm tính toán ---

    private fun getEvenNumbers(n: Int): List<Int> {
        return (0 until n).filter { it % 2 == 0 }
    }

    private fun getOddNumbers(n: Int): List<Int> {
        return (0 until n).filter { it % 2 != 0 }
    }

    private fun getSquareNumbers(n: Int): List<Int> {
        return generateSequence(0) { it + 1 }
            .map { it * it }
            .takeWhile { it < n }
            .toList()
    }

    private fun isPrime(num: Int): Boolean {
        if (num < 2) return false
        if (num == 2) return true
        if (num % 2 == 0) return false
        var i = 3
        while (i * i <= num) {
            if (num % i == 0) return false
            i += 2
        }
        return true
    }

    private fun getPrimeNumbers(n: Int): List<Int> {
        return (2 until n).filter { isPrime(it) }
    }

    private fun isPerfect(num: Int): Boolean {
        if (num < 2) return false
        var sum = 1
        var i = 2
        while (i * i <= num) {
            if (num % i == 0) {
                sum += i
                if (i * i != num) {
                    sum += num / i
                }
            }
            i++
        }
        return sum == num
    }

    private fun getPerfectNumbers(n: Int): List<Int> {
        return (2 until n).filter { isPerfect(it) }
    }

    private fun getFibonacciNumbers(n: Int): List<Int> {
        return generateSequence(0 to 1) { it.second to it.first + it.second }
            .map { it.first }
            .takeWhile { it < n }
            .toList()
    }
}