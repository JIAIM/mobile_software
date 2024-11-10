package com.example.my4

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ikInput = findViewById<EditText>(R.id.ik_input)
        val skInput = findViewById<EditText>(R.id.sk_input)
        val ukmaxInput = findViewById<EditText>(R.id.ukmax_input)
        val calculateButton = findViewById<Button>(R.id.calculate_button)
        val resultOutput = findViewById<TextView>(R.id.result_output)

        calculateButton.setOnClickListener {
            val ik = ikInput.text.toString().toDoubleOrNull() ?: 0.0
            val sk = skInput.text.toString().toDoubleOrNull() ?: 0.0
            val ukmax = ukmaxInput.text.toString().toDoubleOrNull() ?: 0.0

            val minDiameter = findDiameterOfWire(ik)
            val startIp = findStartKZ(sk)
            val kzResults = findKzKhmelnytsk(ukmax)

            resultOutput.text = "Мінімальний діаметр провода: $minDiameter мм²\n" +
                    "Початкове значення струму КЗ: $startIp кА\n" +
                    "Результати Хмельницький:\n$kzResults"
        }
    }

    // Ваши функции для расчетов

    fun round(x: Double): Double {
        return BigDecimal(x).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    fun findDiameterOfWire(Ik: Double): Double {
        return round(Ik * sqrt(2.5) / 92)
    }

    fun findStartKZ(Sk: Double): Double {
        val Usn = 10.5
        val Uk = 10.5
        val Snomt = 6.3
        val Xc = round(Usn * Usn / Sk)
        val Xt = round((Uk * Usn * Usn) / (100 * Snomt))
        val Xs = Xc + Xt
        return round(Usn / (sqrt(3.0) * Xs))
    }

    fun findKzKhmelnytsk(Ukmax: Double): String {
        val Uvn = 115
        val Snomt = 6.3
        val Xt = round((Ukmax * Uvn * Uvn) / (100 * Snomt))

        val Rsn = 10.65
        val Xsn = 24.02
        val Zsh = round(sqrt(Rsn * Rsn + (Xsn + Xt) * (Xsn + Xt)))

        val Xcmin = 65.68
        val Rcmin = 34.88
        val Zshmin = round(sqrt(Rcmin * Rcmin + (Xcmin + Xt) * (Xcmin + Xt)))

        val I3sh = round(Uvn * 1000 / (sqrt(3.0) * Zsh))
        val I2sh = round(I3sh * sqrt(3.0) / 2.0)
        val I3shmin = round(Uvn * 1000 / (sqrt(3.0) * Zshmin))
        val I2shmin = round(I3shmin * sqrt(3.0) / 2.0)

        val Unn = 11.0
        val kpr = (Unn * Unn / Uvn / Uvn)

        val Rshn = round(Rsn * kpr)
        val Xshn = round((Xsn + Xt) * kpr)
        val Zshn = round(sqrt(Rshn * Rshn + Xshn * Xshn))

        val Rshnmin = round(Rcmin * kpr)
        val Xshnmin = round((Xcmin + Xt) * kpr)
        val Zshnmin = round(sqrt(Rshnmin * Rshnmin + Xshnmin * Xshnmin))

        val I3shn = round(Unn * 1000 / (sqrt(3.0) * Zshn))
        val I2shn = round(I3shn * sqrt(3.0) / 2.0)
        val I3shnmin = round(Unn * 1000 / (sqrt(3.0) * Zshnmin))
        val I2shnmin = round(I3shnmin * sqrt(3.0) / 2.0)

        return """
        Нормальний режим - Трифазне КЗ для 110кВ = $I3sh А
        Мінімальний режим - Трифазне КЗ для 110кВ = $I3shmin А
        Нормальний режим - Двофазне КЗ для 110кВ = $I2sh А
        Мінімальний режим - Двофазне КЗ для 110кВ = $I2shmin А
        Нормальний режим - Трифазне КЗ для 10кВ = $I3shn А
        Мінімальний режим - Трифазне КЗ для 10кВ = $I3shnmin А
        Нормальний режим - Двофазне КЗ для 10кВ = $I2shn А
        Мінімальний режим - Двофазне КЗ для 10кВ = $I2shnmin А
    """.trimIndent()
    }

}
