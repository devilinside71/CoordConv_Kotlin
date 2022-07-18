// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    val coordConv = CoordConv()
    val degStringTest01 = "16.707746,-2.986502"
    val degTest01 = DEGData(16.707746, -2.986502)
    val mgrsStringTest01 = "30QWD0143947225"
    val mgrsTest01 = MGRSData(30, "Q", "W", "D", 1439, 47225, 5)
    val utmStringTest01 = "30,Q,501439,1847225"
    val utmTest01 = UTMData(30, "Q", 501439, 1847225)
    val dmsStringTest01 = "16°42'27.89\"N,2°59'11.41\"W"
    val dmsStringSingleDMSNoHemisphereTest01 = "16°42'27.89\""
    val dmsStringSingleDMSLatitudeTest01 = "16°42'27.89\"N"
    val dmsStringSingleDMSLongitudeTest01 = "2°59'11.41\"W"
    val dmsTest01 = DMSData(DMSCoord(16, 42, 27.89, "N"), DMSCoord(2, 59, 11.41, "W"))
    val dmsCoordTest01 = DMSCoord(16, 42, 27.89, "N")
    val singleDegStringTest01 = "16.707746"
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
            print(coordConv.deg2UTM(degTest01))
        }) {
            Text(text)
        }
    }





}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
