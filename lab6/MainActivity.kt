package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.*
import java.math.BigDecimal
import java.math.RoundingMode
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState


fun round(x: Double): Double {
    return BigDecimal(x).setScale(4, RoundingMode.HALF_UP).toDouble()
}

fun estimatedCurrents1level(data: Int): Double {
    val cosphin = 0.9
    val etan = 0.92
    val Un = 0.38
    return round(data / (sqrt(3.0) * Un * cosphin * etan))
}

@Composable
fun CalculatorApp() {
    var Pn by remember { mutableStateOf("20") }
    var Kv by remember { mutableStateOf("0.22") }
    var tgphi by remember { mutableStateOf("1.52") }
    var tableData by remember { mutableStateOf(listOf<List<String>>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = Pn,
            onValueChange = { Pn = it },
            label = { Text("Pn") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = Kv,
            onValueChange = { Kv = it },
            label = { Text("Kv") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = tgphi,
            onValueChange = { tgphi = it },
            label = { Text("tgφ") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                tableData = calculateTable(Pn.toInt(), Kv.toDouble(), tgphi.toDouble())
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Розрахувати")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tableData.isNotEmpty()) {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Column(
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("#")
                    Text("etan")
                    Text("cosu")
                    Text("Un")
                    Text("n")
                    Text("Pn")
                    Text("n*Pn")
                    Text("Kv")
                    Text("tgφ")
                    Text("n*Pn*Kv")
                    Text("n*Pn*Kv*tgφ")
                    Text("n*Pn*Pn")
                    Text("ne")
                    Text("Kp")
                    Text("Pp")
                    Text("Qp")
                    Text("Sp")
                    Text("Ip")
                }

                tableData.forEach { row ->
                    Column(modifier = Modifier.padding(4.dp)) {
                        row.forEach { cell ->
                            Text(cell)
                        }
                    }
                }
            }
        }
    }
}

fun calculateTable(Pn: Int, Kv: Double, tgphi: Double): List<List<String>> {
    val Un = 0.38
    val n_data = intArrayOf(4, 2, 4, 1, 1, 1, 2, 1)
    val Pn_data = intArrayOf(Pn, 14, 42, 36, 20, 40, 32, 20)
    val Kv_data = doubleArrayOf(0.15, 0.12, 0.15, 0.3, 0.5, Kv, 0.2, 0.65)
    val tgphi_data = doubleArrayOf(1.33, 1.0, 1.33, tgphi, 0.75, 1.0, 1.0, 0.75)
    val n_Pn_data = mutableListOf<Int>()
    val n_Pn_Kv_data = mutableListOf<Double>()
    val n_Pn_Kv_tgphi_data = mutableListOf<Double>()
    val n_Pn_Pn_data = mutableListOf<Int>()
    val Ip_data = mutableListOf<Double>()

    for (i in n_data.indices) {
        val n = n_data[i]
        val Pn = Pn_data[i]
        val Kv = Kv_data[i]
        val tgphi = tgphi_data[i]
        n_Pn_data.add(n * Pn)
        n_Pn_Kv_data.add(round(n_Pn_data[i] * Kv))
        n_Pn_Kv_tgphi_data.add(round(n_Pn_Kv_data[i] * tgphi))
        n_Pn_Pn_data.add(n * Pn * Pn)
        Ip_data.add(estimatedCurrents1level(n_Pn_data[i]))
    }

    val SHR1_n = n_data.sum()
    val SHR1_n_Pn = n_Pn_data.sum()
    val SHR1_Kv = BigDecimal(n_Pn_Kv_data.sum()/n_Pn_data.sum()).setScale(2, RoundingMode.HALF_UP)
    val SHR1_n_Pn_Kv = n_Pn_Kv_data.sum()
    val SHR1_n_Pn_Kv_tgphi = round(n_Pn_Kv_tgphi_data.sum())
    val SHR1_n_Pn_Pn = n_Pn_Pn_data.sum()
    val SHR1_ne = SHR1_n_Pn*SHR1_n_Pn/SHR1_n_Pn_Pn + 1
    val SHR1_Kp = 1.25 //data from table 6.3
    val SHR1_Pp = round(SHR1_Kp*SHR1_n_Pn_Kv)
    val SHR1_Qp = 1.0*SHR1_n_Pn_Kv_tgphi
    val SHR1_Sp = round(sqrt(SHR1_Pp*SHR1_Pp+SHR1_Qp*SHR1_Qp))
    val SHR1_Ip = round(SHR1_Pp/Un)

    val transfor_n = 2
    val transfor_Pn = 100
    val transfor_n_Pn = transfor_n*transfor_Pn
    val transfor_Kv = 0.2
    val transfor_tgphi = 3.0
    val transfor_n_Pn_Kv = transfor_n_Pn*transfor_Kv
    val transfor_n_Pn_Kv_tgphi = transfor_n_Pn_Kv*transfor_tgphi
    val transfor_n_Pn_Pn = transfor_n_Pn*transfor_Pn
    val transfor_Ip = estimatedCurrents1level(transfor_n_Pn)

    val sushi_n = 2
    val sushi_Pn = 120
    val sushi_n_Pn = sushi_n*sushi_Pn
    val sushi_Kv = 0.8
    val sushi_tgphi = ' '
    val sushi_n_Pn_Kv = sushi_n_Pn*sushi_Kv
    val sushi_n_Pn_Kv_tgphi = ' '
    val sushi_n_Pn_Pn = sushi_n_Pn*sushi_Pn
    val sushi_Ip = estimatedCurrents1level(sushi_n_Pn)

    val all_n = 81
    val all_n_Pn = 2330
    val all_n_Pn_Kv = 752
    val all_Kv = BigDecimal(all_n_Pn_Kv.toDouble()/all_n_Pn).setScale(2, RoundingMode.HALF_UP)
    val all_n_Pn_Kv_tgphi = 657
    val all_n_Pn_Pn = 96399
    val all_ne = all_n_Pn*all_n_Pn/all_n_Pn_Pn
    val all_Kp = 0.7 //from table
    val all_Pp = round(all_Kp*all_n_Pn_Kv)
    val all_Qp = all_Kp*all_n_Pn_Kv_tgphi
    val all_Sp = round(sqrt(all_Pp*all_Pp+all_Qp*all_Qp))
    val all_Ip = round(all_Pp/Un)

    val table = mutableListOf<List<String>>()
    for (i in n_data.indices) {
        table.add(
            listOf(
                (i+1).toString(),
                String.format("%.2f", 0.92),
                String.format("%.1f", 0.9),
                String.format("%.2f", Un),
                n_data[i].toString(),
                Pn_data[i].toString(),
                n_Pn_data[i].toString(),
                String.format("%.2f", Kv_data[i]),
                String.format("%.2f", tgphi_data[i]),
                String.format("%.2f", n_Pn_Kv_data[i]),
                String.format("%.3f", n_Pn_Kv_tgphi_data[i]),
                n_Pn_Pn_data[i].toString(),
                "", "", "", "", "", String.format("%.4f", Ip_data[i])
            )
        )
    }

    for (i in 1..3){
        table.add(
            listOf(
                "SHR"+i.toString(),
                "",
                "",
                "",
                SHR1_n.toString(),
                "",
                SHR1_n_Pn.toString(),
                String.format("%.2f", SHR1_Kv),
                "",
                String.format("%.2f", SHR1_n_Pn_Kv),
                String.format("%.3f", SHR1_n_Pn_Kv_tgphi),
                SHR1_n_Pn_Pn.toString(),
                SHR1_ne.toString(),
                String.format("%.2f", SHR1_Kp),
                String.format("%.2f", SHR1_Pp),
                String.format("%.2f", SHR1_Qp),
                String.format("%.2f", SHR1_Sp),
                String.format("%.4f", SHR1_Ip),
            )
        )
    }

    table.add(
        listOf(
            "Trans",
            String.format("%.2f", 0.92),
            String.format("%.1f", 0.9),
            String.format("%.2f", Un),
            transfor_n.toString(),
            transfor_Pn.toString(),
            transfor_n_Pn.toString(),
            String.format("%.2f", transfor_Kv),
            String.format("%.2f", transfor_tgphi),
            String.format("%.2f", transfor_n_Pn_Kv),
            String.format("%.3f", transfor_n_Pn_Kv_tgphi),
            transfor_n_Pn_Pn.toString(),
            "", "", "", "", "", String.format("%.4f", transfor_Ip)
        )
    )

    table.add(
        listOf(
            "Shafa",
            String.format("%.2f", 0.92),
            String.format("%.1f", 0.9),
            String.format("%.2f", Un),
            sushi_n.toString(),
            sushi_Pn.toString(),
            sushi_n_Pn.toString(),
            String.format("%.2f", sushi_Kv),
            "",
            String.format("%.2f", sushi_n_Pn_Kv),
            "",
            transfor_n_Pn_Pn.toString(),
            "", "", "", "", "", String.format("%.4f", sushi_Ip)
        )
    )

    table.add(
        listOf(
            "All",
            "",
            "",
            "",
            all_n.toString(),
            "",
            all_n_Pn.toString(),
            String.format("%.2f", all_Kv),
            "",
            all_n_Pn_Kv.toString(),
            all_n_Pn_Kv_tgphi.toString(),
            all_n_Pn_Pn.toString(),
            all_ne.toString(),
            String.format("%.2f", all_Kp),
            String.format("%.2f", all_Pp),
            String.format("%.2f", all_Qp),
            String.format("%.2f", all_Sp),
            String.format("%.4f", all_Ip),
        )
    )

    return table
}

@Preview(showBackground = true)
@Composable
fun PreviewCalculatorApp() {
    CalculatorApp()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorApp()
        }
    }
}

