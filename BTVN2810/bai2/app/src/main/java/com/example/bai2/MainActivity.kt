package com.example.bai2

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var editFirstName: EditText
    private lateinit var editLastName: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var editBirthday: EditText
    private lateinit var buttonSelectDate: Button
    private lateinit var editAddress: EditText
    private lateinit var editEmail: EditText
    private lateinit var checkboxTerms: CheckBox
    private lateinit var buttonRegister: Button

    // default background colors to restore after error highlight
    private var defaultEditBgColor: Int = Color.TRANSPARENT
    private var defaultRadioBgColor: Int = Color.TRANSPARENT
    private var defaultCheckboxBgColor: Int = Color.TRANSPARENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // find views
        editFirstName = findViewById(R.id.edit_first_name)
        editLastName = findViewById(R.id.edit_last_name)
        radioGroupGender = findViewById(R.id.radio_group_gender)
        editBirthday = findViewById(R.id.edit_birthday)
        buttonSelectDate = findViewById(R.id.button_select_date)
        editAddress = findViewById(R.id.edit_address)
        editEmail = findViewById(R.id.edit_email)
        checkboxTerms = findViewById(R.id.checkbox_terms)
        buttonRegister = findViewById(R.id.button_register)

        // lưu màu nền mặc định (để restore khi người dùng sửa)
        defaultEditBgColor = getBackgroundColor(editFirstName)
        defaultRadioBgColor = getBackgroundColor(radioGroupGender)
        defaultCheckboxBgColor = getBackgroundColor(checkboxTerms)

        buttonSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Register button: kiểm tra các thông tin
        buttonRegister.setOnClickListener {
            validateAndRegister()
        }
    }

    /**
     * Hiển thị hộp thoại DatePickerDialog để chọn ngày sinh.
     */
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) // 0-based
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // DatePickerDialog là standard Android picker
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Month là 0-based, nên ta cần +1
                val month1 = month + 1

                // Định dạng (Format) ngày thành YYYY-MM-DD với 2 chữ số (padding 0)
                val day = String.format("%02d", dayOfMonth)
                val mon = String.format("%02d", month1)
                val dateStr = "$year-$mon-$day"

                editBirthday.setText(dateStr)

                // Khi có ngày hợp lệ => bỏ highlight đỏ nếu có
                editBirthday.setBackgroundColor(defaultEditBgColor)
            },
            currentYear,
            currentMonth, // Bắt đầu từ tháng hiện tại
            currentDay
        )
        datePickerDialog.show()
    }


    private fun validateAndRegister() {
        var ok = true

        // reset background trước khi validate
        resetAllBackgrounds()

        // First name
        if (editFirstName.text.toString().trim().isEmpty()) {
            editFirstName.setBackgroundColor(Color.parseColor("#FFCDD2")) // đỏ nhạt
            ok = false
        }

        // Last name
        if (editLastName.text.toString().trim().isEmpty()) {
            editLastName.setBackgroundColor(Color.parseColor("#FFCDD2"))
            ok = false
        }

        // Gender (kiểm tra radioGroup)
        if (radioGroupGender.checkedRadioButtonId == -1) {
            // highlight container radio group background (khoảng trống) - ta đổi background của RadioGroup
            radioGroupGender.setBackgroundColor(Color.parseColor("#FFCDD2"))
            ok = false
        }

        // Birthday
        if (editBirthday.text.toString().trim().isEmpty()) {
            editBirthday.setBackgroundColor(Color.parseColor("#FFCDD2"))
            ok = false
        }

        // Address
        if (editAddress.text.toString().trim().isEmpty()) {
            editAddress.setBackgroundColor(Color.parseColor("#FFCDD2"))
            ok = false
        }

        // Email (validate basic)
        val email = editEmail.text.toString().trim()
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setBackgroundColor(Color.parseColor("#FFCDD2"))
            ok = false
        }

        // Terms checkbox
        if (!checkboxTerms.isChecked) {
            checkboxTerms.setBackgroundColor(Color.parseColor("#FFCDD2"))
            ok = false
        }

        if (ok) {
            // Tất cả hợp lệ -> có thể xử lý lưu / chuyển màn hình / thông báo
            Toast.makeText(this, "Đã đăng ký thành công!", Toast.LENGTH_LONG).show()
            // reset form nếu muốn
            // resetForm()
        } else {
            Toast.makeText(this, "Vui lòng điền/ chọn các trường màu đỏ.", Toast.LENGTH_LONG).show()
        }
    }

    private fun resetAllBackgrounds() {
        editFirstName.setBackgroundColor(defaultEditBgColor)
        editLastName.setBackgroundColor(defaultEditBgColor)
        radioGroupGender.setBackgroundColor(defaultRadioBgColor)
        editBirthday.setBackgroundColor(defaultEditBgColor)
        editAddress.setBackgroundColor(defaultEditBgColor)
        editEmail.setBackgroundColor(defaultEditBgColor)
        checkboxTerms.setBackgroundColor(defaultCheckboxBgColor)
    }

    // Helper: lấy màu nền hiện tại nếu có, mặc định trả về TRANSPARENT
    private fun getBackgroundColor(view: View): Int {
        return Color.TRANSPARENT
    }

    // (Tùy chọn) reset form
    private fun resetForm() {
        editFirstName.text.clear()
        editLastName.text.clear()
        radioGroupGender.clearCheck()
        editBirthday.text.clear()
        editAddress.text.clear()
        editEmail.text.clear()
        checkboxTerms.isChecked = false
        resetAllBackgrounds()
    }
}