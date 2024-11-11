package com.example.laba5

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import com.example.laba5.ui.theme.Laba5Theme
import java.math.BigDecimal
import java.math.RoundingMode

// Функція для заокруглення значення до сотих
fun round(x: Double): Double {
    return BigDecimal(x).setScale(1, RoundingMode.HALF_UP).toDouble()
}

// Функція для розрахунку частоти відмов двоколкової системи
fun findW2oc(Woc: Double): String {
    val Wcv = 0.02
    val tvoc = 10.7
    val kpmax = 43.0 / 8760.0
    val kaoc = round(Woc * tvoc * 10000.0 / 8760.0) / 10000.0
    val kpoc = round(1.2 * kpmax * 10000.0) / 10000.0
    val Wdk = 2 * Woc * (kaoc + kpoc)
    val Wdc = round((Wdk + Wcv) * 10000.0) / 10000.0

    val result = StringBuilder()

    when {
        Woc == Wdc -> result.append("Надійність систем однакова\n")
        Woc < Wdc -> result.append("Надійність одноколкової системи електропередачі є вищою ніж двоколкової\n")
        else -> result.append("Надійність двоколкової системи електропередачі є вищою ніж одноколкової\n")
    }

    result.append("Частота відмов одноколкової системи = $Woc\n")
    result.append("Частота відмов двоколкової системи = $Wdc\n")

    return result.toString()
}

// Функція для розрахунку збитків від вимкнень електропостачання
fun findZbitky(Za: Double, Zp: Double): Double {
    val Ma = 14900.0
    val Mp = 132400.0
    return Za * Ma + Zp * Mp
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Laba5Theme {
                // Весь контент
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var Woc by remember { mutableStateOf(TextFieldValue("0.295")) }
    var Za by remember { mutableStateOf(TextFieldValue("23.6")) }
    var Zp by remember { mutableStateOf(TextFieldValue("17.6")) }
    var result by remember { mutableStateOf("Введіть значення для розрахунку") }

    // Розрахунок результату на основі введених значень
    fun calculate() {
        val WocValue = Woc.text.toDoubleOrNull() ?: 0.0
        val ZaValue = Za.text.toDoubleOrNull() ?: 0.0
        val ZpValue = Zp.text.toDoubleOrNull() ?: 0.0

        // Перевірка на коректність введених даних
        if (WocValue > 0 && ZaValue > 0 && ZpValue > 0) {
            result = findW2oc(WocValue) + "\nЗбитки від переривання електропостачання: ${findZbitky(ZaValue, ZpValue)} грн"
        } else {
            result = "Будь ласка, введіть коректні значення!"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Ввод для частоти відмов одноколкової системи
        TextField(
            value = Woc,
            onValueChange = { Woc = it },
            label = { Text("Частота відмов одноколкової системи") },
            modifier = Modifier.fillMaxWidth()
        )

        // Ввод для ціни за збитки від аварійних вимкнень
        TextField(
            value = Za,
            onValueChange = { Za = it },
            label = { Text("Ціна за збитки від аварійних вимкнень") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        // Ввод для ціни за збитки від планових вимкнень
        TextField(
            value = Zp,
            onValueChange = { Zp = it },
            label = { Text("Ціна за збитки від планових вимкнень") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка для запуску розрахунку
        Button(
            onClick = { calculate() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Розрахувати")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Результат розрахунку
        Text(
            text = result,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Laba5Theme {
        MainScreen()
    }
}
