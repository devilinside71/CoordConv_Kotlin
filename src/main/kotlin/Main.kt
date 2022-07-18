// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    val coordConv = CoordConv()
    val coordValidator = CoordValidator()
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
    val dmsTest01 = DMSData(16, 42, 27.89, "N", 2, 59, 11.41, "W")
    val singleDegStringTest01 = "16.707746"

    var inputText by remember { mutableStateOf("") }
    var mgrsText by remember { mutableStateOf("") }
    var utmText by remember { mutableStateOf("") }
    var degText by remember { mutableStateOf("") }
    var dmsText by remember { mutableStateOf("") }
    var latText by remember { mutableStateOf("") }
    var lonText by remember { mutableStateOf("") }
    var recognizedText by remember { mutableStateOf("") }



    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            Row() {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Enter coordinate here") }
                )
                Button({
                    val coordConv = CoordConv()
                    val coordValidator = CoordValidator()
                    var mgrs: String
                    println("----------------------------------------")
                    val recognizedCoordinate = coordValidator.recognizedCoord(inputText)
                    println("Recognized: $recognizedCoordinate")
                    recognizedText=recognizedCoordinate.toString()
                    when (recognizedCoordinate) {
                        CoordType.MGRS -> {
                            var tempStr = inputText.replace("\\s+".toRegex(), "")
                            println("tempStr $tempStr")
                            var tempData = coordConv.mgrsstringToData(tempStr)
                            println("tempData $tempData")
                            var tempStr2 = coordConv.mgrsdataToString(tempData)
                            if (coordValidator.validMGRSString(tempStr2)) {
                                mgrsText = tempStr2
                            } else {
                                mgrsText = GeneralData.naStr
                            }
                            var degData = coordConv.mgrs2DEG(tempData)
                            degText = coordConv.degdataToString(degData)
                            utmText = coordConv.utmdataToString(coordConv.mgrs2UTM(tempData))
                            dmsText = coordConv.dmsdataToString(coordConv.mgrs2DMS(tempData))
                            latText = degData.Latitude.toString()
                            lonText = degData.Longitude.toString()
                        }
                        CoordType.UTM -> {
                            var tempStr = inputText.replace("\\s+".toRegex(), "")
                            var tempData = coordConv.utmstringToData(tempStr)
                            var tempStr2 = coordConv.utmdataToString(tempData)
                            mgrsText = coordConv.mgrsdataToString(coordConv.utm2MGRS(tempData, 5))
                            var degData = coordConv.utm2DEG(tempData)
                            degText = coordConv.degdataToString(degData)
                            utmText = tempStr2
                            dmsText = coordConv.dmsdataToString(coordConv.utm2DMS(tempData))
                            latText = degData.Latitude.toString()
                            lonText = degData.Longitude.toString()
                        }
                        CoordType.DEG -> {
                            var tempStr = inputText.replace("\\s+".toRegex(), "")
                            var tempData = coordConv.utmstringToData(tempStr)
                            var tempStr2 = coordConv.utmdataToString(tempData)

                        }
                        CoordType.SingleDEG -> {
                            var tempStr = inputText.replace("\\s+".toRegex(), "")
                            var tempData = coordConv.utmstringToData(tempStr)
                            var tempStr2 = coordConv.utmdataToString(tempData)

                        }
                        CoordType.DMS -> {
                            var tempStr = inputText.replace("\\s+".toRegex(), "")
                            var tempData = coordConv.utmstringToData(tempStr)
                            var tempStr2 = coordConv.utmdataToString(tempData)

                        }
                        CoordType.SingleDMSNoHemisphere -> {
                            var tempStr = inputText.replace("\\s+".toRegex(), "")
                            var tempData = coordConv.utmstringToData(tempStr)
                            var tempStr2 = coordConv.utmdataToString(tempData)

                        }
                        CoordType.SingleDMSLatitude -> {
                            var tempStr = inputText.replace("\\s+".toRegex(), "")
                            var tempData = coordConv.utmstringToData(tempStr)
                            var tempStr2 = coordConv.utmdataToString(tempData)

                        }
                        CoordType.SingleDMSLongitude -> {
                            var tempStr = inputText.replace("\\s+".toRegex(), "")
                            var tempData = coordConv.utmstringToData(tempStr)
                            var tempStr2 = coordConv.utmdataToString(tempData)

                        }
                        else -> { // Note the block
                            mgrsText = GeneralData.naStr
                            degText = GeneralData.naStr
                            utmText = GeneralData.naStr
                            dmsText = GeneralData.naStr
                            latText = GeneralData.naStr
                            lonText = GeneralData.naStr
                        }
                    }
                }) {
                    Text("Convert")
                }
            }
            Spacer(Modifier.height(10.dp))
            Row() {
                Text(
                    text = recognizedText
                )
            }
            Spacer(Modifier.height(10.dp))
            Row() {
                TextField(
                    value = mgrsText,
                    onValueChange = { mgrsText = it },
                    label = { Text("MGRS") }
                )
//                Text(
//                    text = mgrsText
//                )
            }
            Spacer(Modifier.height(10.dp))
            Row() {
                TextField(
                    value = degText,
                    onValueChange = { degText = it },
                    label = { Text("DEG") }
                )
            }
            Spacer(Modifier.height(10.dp))
            Row() {
                TextField(
                    value = utmText,
                    onValueChange = { utmText = it },
                    label = { Text("UTM") }
                )
            }
            Spacer(Modifier.height(10.dp))
            Row() {
                TextField(
                    value = dmsText,
                    onValueChange = { dmsText = it },
                    label = { Text("DMS") }
                )
            }
            Spacer(Modifier.height(10.dp))
            Row() {
                TextField(
                    value = latText,
                    onValueChange = { latText = it },
                    label = { Text("LAT") }

                )
            }
            Spacer(Modifier.height(10.dp))
            Row() {
                TextField(
                    value = lonText,
                    onValueChange = { lonText = it },
                    label = { Text("LON") }
                )
            }
        }
    }


}


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Coordinate converter") {
        App()
    }
}
