package com.example.bai1
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView

    // trạng thái
    private var currentInput: String = "0"
    private var operand1: Long? = null
    private var pendingOp: Char? = null
    private var lastWasError: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bai1)

        // display
        display = findViewById(R.id.display)

        // digits
        val btn0 = findViewById<Button>(R.id.btn_plus14)
        val btn1 = findViewById<Button>(R.id.btn_plus19)
        val btn2 = findViewById<Button>(R.id.btn_plus18)
        val btn3 = findViewById<Button>(R.id.btn_plus17)
        val btn4 = findViewById<Button>(R.id.btn_plus25)
        val btn5 = findViewById<Button>(R.id.btn_plus24)
        val btn6 = findViewById<Button>(R.id.btn_plus22)
        val btn7 = findViewById<Button>(R.id.btn_plus29)
        val btn8 = findViewById<Button>(R.id.btn_plus28)
        val btn9 = findViewById<Button>(R.id.btn_plus27)

        // operators & controls
        val btnPlus = findViewById<Button>(R.id.btn_plus12)
        val btnMinus = findViewById<Button>(R.id.btn_plus20)
        val btnMul = findViewById<Button>(R.id.btn_plus26)
        val btnDiv = findViewById<Button>(R.id.btn_plus31)
        val btnEq = findViewById<Button>(R.id.btn_plus16)
        val btnBS = findViewById<Button>(R.id.btn_plus30)
        val btnCE = findViewById<Button>(R.id.btn_plus33)
        val btnC = findViewById<Button>(R.id.btn_plus32)
        val btnNeg = findViewById<Button>(R.id.btn_plus15)
        // val btnDot = findViewById<Button>(R.id.btn_plus13) // bỏ qua dấu .

        // set listeners cho digits
        val digitListener: (String) -> Unit = { d ->
            if (lastWasError) {
                // nếu vừa lỗi, khi gõ mới -> reset
                clearAll()
                lastWasError = false
            }

            // tránh tiền tố 0 không cần thiết
            currentInput = if (currentInput == "0") d else currentInput + d
            updateDisplay()
        }

        btn0.setOnClickListener { digitListener("0") }
        btn1.setOnClickListener { digitListener("1") }
        btn2.setOnClickListener { digitListener("2") }
        btn3.setOnClickListener { digitListener("3") }
        btn4.setOnClickListener { digitListener("4") }
        btn5.setOnClickListener { digitListener("5") }
        btn6.setOnClickListener { digitListener("6") }
        btn7.setOnClickListener { digitListener("7") }
        btn8.setOnClickListener { digitListener("8") }
        btn9.setOnClickListener { digitListener("9") }

        // operators
        btnPlus.setOnClickListener { onOperator('+') }
        btnMinus.setOnClickListener { onOperator('-') }
        btnMul.setOnClickListener { onOperator('*') }
        btnDiv.setOnClickListener { onOperator('/') }

        btnEq.setOnClickListener { onEquals() }

        // BS: xóa chữ số hàng đơn vị
        btnBS.setOnClickListener {
            if (lastWasError) {
                clearAll()
                lastWasError = false
            } else {
                if (currentInput.length <= 1) currentInput = "0"
                else currentInput = currentInput.dropLast(1)
                updateDisplay()
            }
        }

        // CE: xóa giá trị toán hạng hiện tại về 0 (không ảnh hưởng operand1/pendingOp)
        btnCE.setOnClickListener {
            currentInput = "0"
            updateDisplay()
        }

        // C: xóa phép toán, nhập lại từ đầu (clear all)
        btnC.setOnClickListener {
            clearAll()
        }

        // +/- toggle sign (vì đề là số nguyên)
        btnNeg.setOnClickListener {
            if (lastWasError) {
                clearAll()
                lastWasError = false
            } else {
                currentInput = if (currentInput.startsWith("-")) currentInput.removePrefix("-") else if (currentInput != "0") "-" + currentInput else "0"
                updateDisplay()
            }
        }

        // hiển thị ban đầu
        updateDisplay()
    }

    private fun onOperator(op: Char) {
        if (lastWasError) {
            clearAll()
            lastWasError = false
        }

        try {
            val inputVal = currentInput.toLong()
            if (operand1 == null) {
                operand1 = inputVal
            } else {
                // nếu đã có phép chờ, tính trước
                if (pendingOp != null) {
                    val result = compute(operand1!!, pendingOp!!, inputVal)
                    operand1 = result
                } else {
                    // nếu chưa có pendingOp, chỉ cập nhật operand1
                    operand1 = inputVal
                }
            }
            pendingOp = op
            currentInput = "0"
            updateDisplayWithOperand(operand1!!)
        } catch (e: NumberFormatException) {
            showError()
        } catch (e: ArithmeticException) {
            showError()
        }
    }

    private fun onEquals() {
        if (lastWasError) {
            clearAll()
            lastWasError = false
            return
        }

        if (pendingOp == null || operand1 == null) {
            // không có phép toán, chỉ hiển thị input hiện tại
            updateDisplay()
            return
        }

        try {
            val inputVal = currentInput.toLong()
            val result = compute(operand1!!, pendingOp!!, inputVal)
            // hiển thị kết quả và cho phép tiếp tục tính trên kết quả
            currentInput = result.toString()
            operand1 = null
            pendingOp = null
            updateDisplay()
        } catch (e: NumberFormatException) {
            showError()
        } catch (e: ArithmeticException) {
            showError()
        }
    }

    private fun compute(a: Long, op: Char, b: Long): Long {
        return when (op) {
            '+' -> {
                // kiểm tra overflow cơ bản bằng toBigInteger nếu cần, nhưng ta dùng Long và try/catch
                a + b
            }
            '-' -> a - b
            '*' -> a * b
            '/' -> {
                if (b == 0L) throw ArithmeticException("Division by zero")
                a / b // chia kiểu số nguyên
            }
            else -> throw IllegalArgumentException("Unknown op")
        }
    }

    private fun updateDisplay() {
        display.text = currentInput
    }

    private fun updateDisplayWithOperand(value: Long) {
        display.text = value.toString()
    }

    private fun clearAll() {
        currentInput = "0"
        operand1 = null
        pendingOp = null
        updateDisplay()
    }

    private fun showError() {
        display.text = "Error"
        currentInput = "0"
        operand1 = null
        pendingOp = null
        lastWasError = true
    }
}
