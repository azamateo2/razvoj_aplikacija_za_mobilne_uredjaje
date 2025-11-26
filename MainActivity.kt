package ba.sum.fsre.zadacisamostalno1


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.text.DecimalFormat
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView

    private var firstOperand: Double? = null
    private var operator: String? = null
    private var isSecondOperand = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)

        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        numberButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                numberPressed((it as Button).text.toString())
            }
        }

        findViewById<Button>(R.id.btnPlus).setOnClickListener { operatorPressed("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { operatorPressed("-") }
        findViewById<Button>(R.id.btnMul).setOnClickListener { operatorPressed("*") }
        findViewById<Button>(R.id.btnDiv).setOnClickListener { operatorPressed("/") }

        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculate() }

        findViewById<Button>(R.id.btnAC).setOnClickListener { allClear() }
        findViewById<Button>(R.id.btnDEL).setOnClickListener { deleteLast() }

        tvDisplay.text = "0"
    }

    private fun numberPressed(number: String) {
        val currentText = tvDisplay.text.toString()


        if (currentText == "0" || isSecondOperand || currentText == "Error") {

            tvDisplay.text = number

            isSecondOperand = false
        } else {

            tvDisplay.append(number)
        }
    }

    private fun operatorPressed(op: String) {
        if (firstOperand != null && !isSecondOperand) {
            calculate()
        }

        firstOperand = tvDisplay.text.toString().toDoubleOrNull()
        operator = op
        isSecondOperand = true // Display se resetira kod sljedećeg unosa broja
    }

    private fun calculate() {
        val secondOperand = tvDisplay.text.toString().toDoubleOrNull()

        if (firstOperand == null || secondOperand == null || operator == null) {
            return
        }

        val result = try {
            when (operator) {
                "+" -> firstOperand!! + secondOperand
                "-" -> firstOperand!! - secondOperand
                "*" -> firstOperand!! * secondOperand
                "/" -> {
                    if (secondOperand == 0.0) throw Exception("Error")
                    firstOperand!! / secondOperand
                }
                else -> 0.0
            }
        } catch (e: Exception) {
            tvDisplay.text = "Error"
            firstOperand = null
            operator = null
            isSecondOperand = true
            return
        }

        tvDisplay.text = formatResult(result)

        firstOperand = result
        operator = null
        isSecondOperand = true
    }

    private fun allClear() {
        tvDisplay.text = "0"
        firstOperand = null
        operator = null
        isSecondOperand = false // Mora biti false!
    }

    private fun deleteLast() {
        if (tvDisplay.text == "Error" || isSecondOperand) {
            return
        }

        val text = tvDisplay.text.toString()

        tvDisplay.text = if (text.length > 1) {
            text.dropLast(1)
        } else {
            "0"
        }
    }

    private fun formatResult(result: Double): String {
        val df = DecimalFormat("#.##########")
        df.roundingMode = RoundingMode.HALF_UP

        val formatted = df.format(result)
        return formatted.replace(",",".")
    }
}
