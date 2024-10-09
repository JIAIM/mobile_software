package com.example.lab2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Получаем ссылки на элементы интерфейса
        val coalInput = findViewById<EditText>(R.id.coalInput)
        val mazutInput = findViewById<EditText>(R.id.mazutInput)
        val gasInput = findViewById<EditText>(R.id.gasInput)
        val calculateButton = findViewById<Button>(R.id.calculateButton)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)

        // При натисканні на кнопку
        calculateButton.setOnClickListener {
            val coalAmount = coalInput.text.toString().toDoubleOrNull() ?: 0.0
            val mazutAmount = mazutInput.text.toString().toDoubleOrNull() ?: 0.0
            val gasAmount = gasInput.text.toString().toDoubleOrNull() ?: 0.0

            // Параметры для вугілля
            val dataForCoal = listOf(20.47, 0.8, 25.20, 1.5, 0.985, 0.0)
            val ktvCoal = findEmisia(dataForCoal)
            val etvCoal = findVikid(ktvCoal, dataForCoal[0], coalAmount)

            // Параметры для мазуту
            val dataForMazut = listOf(39.48, 1.0, 0.15, 0.0, 0.985, 0.0)
            val ktvMazut = findEmisia(dataForMazut)
            val etvMazut = findVikid(ktvMazut, dataForMazut[0], mazutAmount)

            // Параметри для газу
            val ktvGas = 0.0
            val etvGas = 0.0

            // Формуємо текст для виводу результатів
            val resultText = """
                Викиди для вугілля:
                KTV: $ktvCoal г/ГДЖ
                Etv: $etvCoal т

                Викиди для мазуту:
                KTV: $ktvMazut г/ГДЖ
                Etv: $etvMazut т

                Викиди для газу:
                KTV: $ktvGas г/ГДЖ
                Etv: $etvGas т
            """.trimIndent()

            // Відображаємо результати на екрані
            resultTextView.text = resultText
        }
    }

    // Функція для округлення значення до сотих
    private fun round(x: Double): Double {
        return BigDecimal(x).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    // Функція для розрахунку емісії твердих частинок
    private fun findEmisia(data: List<Double>): Double {
        if (data.size != 6) {
            throw IllegalArgumentException("Недостатньо параметрів.")
        }
        val Qri = data[0]
        val avin = data[1]
        val Ar = data[2]
        val Gvin = data[3]
        val nzy = data[4]
        val ktvs = data[5]
        return round((1000000 / Qri) * avin * (Ar / (100 - Gvin)) * (1 - nzy) + ktvs)
    }

    // Функція для розрахунку викидів
    private fun findVikid(ktv: Double, Qri: Double, B: Double): Double {
        return round(ktv * Qri * B / 1000000)
    }
}
