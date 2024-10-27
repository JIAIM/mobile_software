package com.example.lab3
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.exp
import kotlin.math.sqrt
import kotlin.math.PI
import kotlin.math.pow
import java.math.BigDecimal
import java.math.RoundingMode


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputPc = findViewById<EditText>(R.id.inputPc)
        val inputSigma1 = findViewById<EditText>(R.id.inputSigma1)
        val inputSigma2 = findViewById<EditText>(R.id.inputSigma2)
        val inputVartist = findViewById<EditText>(R.id.inputVartist)
        val calculateButton = findViewById<Button>(R.id.calculateButton)
        val resultSigma1 = findViewById<TextView>(R.id.resultSigma1)
        val resultSigma2 = findViewById<TextView>(R.id.resultSigma2)

        calculateButton.setOnClickListener {
            val Pc = inputPc.text.toString().toDoubleOrNull() ?: 0.0
            val sigma1 = inputSigma1.text.toString().toDoubleOrNull() ?: 0.0
            val sigma2 = inputSigma2.text.toString().toDoubleOrNull() ?: 0.0
            val vartist = inputVartist.text.toString().toDoubleOrNull() ?: 0.0

            val prib1 = calculatePribytok(Pc, vartist, sigma1)
            val prib2 = calculatePribytok(Pc, vartist, sigma2)

            resultSigma1.text = "Прибуток для системи 1 = $prib1 тис. грн"
            resultSigma2.text = "Прибуток для вдосконаленої системи 2 = $prib2 тис. грн"
        }
    }

    private fun calculatePribytok(Pct: Double, vartistt: Double, sigma: Double): Double {
        val Pc = Pct
        val vartist = vartistt
        val sigmaW1 = findPercentEnergy(Pc, sigma)
        val W1 = Pc * 24 * sigmaW1
        val P1 = W1 * vartist
        val W2 = Pc * 24 * (1 - sigmaW1)
        val shtraf = W2 * vartist
        val prib1 = P1 - shtraf

        return round(prib1)
    }

    private fun findPercentEnergy(Pc: Double, sigma: Double): Double {
        val a = 4.75 // нижня межа інтегрування
        val b = 5.25 // верхня межа інтегрування
        return round(calculateIntegral(a, b, Pc, sigma))
    }

    private fun calculateIntegral(a: Double, b: Double, Pc: Double, sigma: Double): Double {
        val step = 0.001
        var sum = 0.0
        var x = a

        while (x <= b) {
            sum += normalDistribution(x, Pc, sigma) * step
            x += step
        }

        return sum
    }

    private fun normalDistribution(p: Double, Pc: Double, sigma: Double): Double {
        return (1 / (sigma * sqrt(2 * PI))) * exp(-0.5 * ((p - Pc) / sigma).pow(2))
    }

    private fun round(x: Double): Double {
        return BigDecimal(x).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}
