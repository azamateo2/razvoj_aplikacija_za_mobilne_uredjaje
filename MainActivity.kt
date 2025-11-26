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
    // Oznaka da li je sljedeći unos dio drugog operanda (nakon operatora) ili je rezultat upravo izračunat.
    private var isSecondOperand = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)

        // Mapiranje brojčanih tipki
        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
            // Ovdje bi išao i R.id.btnDot (ako ga dodaš u XML)
        )

        numberButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                numberPressed((it as Button).text.toString())
            }
        }

        // Operatori
        findViewById<Button>(R.id.btnPlus).setOnClickListener { operatorPressed("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { operatorPressed("-") }
        findViewById<Button>(R.id.btnMul).setOnClickListener { operatorPressed("*") }
        findViewById<Button>(R.id.btnDiv).setOnClickListener { operatorPressed("/") }

        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculate() }

        // Posebne tipke
        findViewById<Button>(R.id.btnAC).setOnClickListener { allClear() }
        findViewById<Button>(R.id.btnDEL).setOnClickListener { deleteLast() }

        // Pravilo 1: Početni prikaz nakon pokretanja aplikacije mora biti 0.
        tvDisplay.text = "0"
    }

    private fun numberPressed(number: String) {
        val currentText = tvDisplay.text.toString()

        // KRITIČNA LOGIKA: Resetiramo ekran ako je:
        // 1. Trenutni prikaz samo "0"
        // 2. Čekamo unos drugog operanda (isSecondOperand je true)
        // 3. Na ekranu je "Error"

        if (currentText == "0" || isSecondOperand || currentText == "Error") {
            // Ako je na ekranu decimalna točka, moramo ostaviti 0 ispred nje
            // (Ova linija je važna za kompletnost, iako nemaš tipku za točku)
            // if (number == ".") {
            //     tvDisplay.text = "0."
            // } else {
            tvDisplay.text = number
            // }

            isSecondOperand = false
        } else {
            // Osiguravamo da ne unosiš dvije decimalne točke (ako je tipka dodana)
            // if (number == "." && currentText.contains(".")) {
            //     return
            // }

            // U suprotnom, nadoveži znamenku
            tvDisplay.append(number)
        }
    }

    private fun operatorPressed(op: String) {
        // Omogući lančane operacije (10+5+2)
        if (firstOperand != null && !isSecondOperand) {
            calculate()
        }

        // Pravilo 3: Spremanje operanda i operatora
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
                    // Pravilo 7: Dijeljenje s nulom (Error)
                    if (secondOperand == 0.0) throw Exception("Error")
                    firstOperand!! / secondOperand
                }
                else -> 0.0
            }
        } catch (e: Exception) {
            tvDisplay.text = "Error"
            // Resetiramo stanja kod greške
            firstOperand = null
            operator = null
            isSecondOperand = true
            return
        }

        // Prikaz rezultata
        tvDisplay.text = formatResult(result)

        // Pravilo 4: Omogući nastavak računanja
        firstOperand = result
        operator = null
        isSecondOperand = true
    }

    // Pravilo 5: AC
    private fun allClear() {
        tvDisplay.text = "0"
        firstOperand = null
        operator = null
        isSecondOperand = false // Mora biti false!
    }

    // Pravilo 6: DEL
    private fun deleteLast() {
        if (tvDisplay.text == "Error" || isSecondOperand) {
            // Ne dozvoli brisanje Error poruke ili ako je samo rezultat na ekranu
            return
        }

        val text = tvDisplay.text.toString()

        tvDisplay.text = if (text.length > 1) {
            text.dropLast(1)
        } else {
            // Prikaz se vraća na 0
            "0"
        }
    }

    // Pomoćna funkcija za formatiranje rezultata (bez suvišnih decimala)
    private fun formatResult(result: Double): String {
        // #.########## dozvoljava do 10 decimala, ali uklanja suvišne nule
        val df = DecimalFormat("#.##########")
        df.roundingMode = RoundingMode.HALF_UP

        val formatted = df.format(result)
        // Osiguravamo da se koristi točka (.)
        return formatted.replace(",",".")
    }
}
