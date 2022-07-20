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

    var garsText by remember { mutableStateOf("") }
    var georefText by remember { mutableStateOf("") }
    var gmapsText by remember { mutableStateOf("") }
    var wazeText by remember { mutableStateOf("") }



    MaterialTheme {
        Row() {
            Spacer(Modifier.width(20.dp))
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
                        recognizedText = recognizedCoordinate.toString()
                        when (recognizedCoordinate) {
                            CoordType.MGRS -> {
                                val tempStr = inputText.replace("\\s+".toRegex(), "")
                                println("tempStr $tempStr")
                                val tempData = coordConv.mgrsstringToData(tempStr)
                                println("tempData $tempData")
                                val tempStr2 = coordConv.mgrsdataToString(tempData)
                                if (coordValidator.validMGRSString(tempStr2)) {
                                    mgrsText = tempStr2
                                } else {
                                    mgrsText = GeneralData.naStr
                                }
                                val degData = coordConv.mgrs2DEG(tempData)
                                degText = coordConv.degdataToString(degData)
                                utmText = coordConv.utmdataToString(coordConv.mgrs2UTM(tempData))
                                dmsText = coordConv.dmsdataToString(coordConv.mgrs2DMS(tempData))
                                latText = degData.Latitude.toString()
                                lonText = degData.Longitude.toString()
                                garsText = coordConv.deg2GARS(degData)
                                georefText = coordConv.deg2GEOREF(degData)
                                gmapsText = coordConv.deg2GMaps(degData)
                                wazeText = coordConv.deg2Waze(degData)
                            }
                            CoordType.UTM -> {
                                val tempStr = inputText.replace("\\s+".toRegex(), "")
                                val tempData = coordConv.utmstringToData(tempStr)
                                val tempStr2 = coordConv.utmdataToString(tempData)
                                mgrsText = coordConv.mgrsdataToString(coordConv.utm2MGRS(tempData, 5))
                                val degData = coordConv.utm2DEG(tempData)
                                degText = coordConv.degdataToString(degData)
                                utmText = tempStr2
                                dmsText = coordConv.dmsdataToString(coordConv.utm2DMS(tempData))
                                latText = degData.Latitude.toString()
                                lonText = degData.Longitude.toString()
                                garsText = coordConv.deg2GARS(degData)
                                georefText = coordConv.deg2GEOREF(degData)
                                gmapsText = coordConv.deg2GMaps(degData)
                                wazeText = coordConv.deg2Waze(degData)
                            }
                            CoordType.DEG -> {
                                val tempStr = inputText.replace("\\s+".toRegex(), "")
                                val tempData = coordConv.degstringToData(tempStr)
                                val tempStr2 = coordConv.degdataToString(tempData)
                                degText = tempStr2
                                mgrsText = coordConv.mgrsdataToString(coordConv.deg2MGRS(tempData))
                                utmText = coordConv.utmdataToString(coordConv.deg2UTM(tempData))
                                dmsText = coordConv.dmsdataToString(coordConv.deg2DMS(tempData))
                                latText = tempData.Latitude.toString()
                                lonText = tempData.Longitude.toString()
                                garsText = coordConv.deg2GARS(tempData)
                                georefText = coordConv.deg2GEOREF(tempData)
                                gmapsText = coordConv.deg2GMaps(tempData)
                                wazeText = coordConv.deg2Waze(tempData)
                            }
                            CoordType.SingleDEG -> {
                                val tempStr = inputText.replace("\\s+".toRegex(), "")
                                val tempData = coordConv.singledegstringToData(tempStr)
                                val tempStr2 = tempData.toString()
                                degText = tempStr2
                                mgrsText = GeneralData.naStr
                                utmText = GeneralData.naStr
                                dmsText = GeneralData.naStr
                                latText = GeneralData.naStr
                                lonText = GeneralData.naStr
                                garsText = GeneralData.naStr
                                georefText = GeneralData.naStr
                                gmapsText = GeneralData.naStr
                                wazeText = GeneralData.naStr
                            }
                            CoordType.DMS -> {
                                val tempStr = inputText.replace("\\s+".toRegex(), "")
                                val tempData = coordConv.dmsstringToData(tempStr)
                                val tempStr2 = coordConv.dmsdataToString(tempData)
                                dmsText = tempStr2
                                mgrsText = coordConv.mgrsdataToString(coordConv.dms2MGRS(tempData))
                                utmText = coordConv.utmdataToString(coordConv.dms2UTM(tempData))
                                val degData = coordConv.dms2DEG(tempData)
                                degText = coordConv.degdataToString(degData)
                                latText = degData.Latitude.toString()
                                lonText = degData.Longitude.toString()
                                garsText = coordConv.deg2GARS(degData)
                                georefText = coordConv.deg2GEOREF(degData)
                                gmapsText = coordConv.deg2GMaps(degData)
                                wazeText = coordConv.deg2Waze(degData)
                            }
                            CoordType.SingleDMSNoHemisphere -> {
                                val tempStr = inputText.replace("\\s+".toRegex(), "")
                                val tempData = coordConv.singledmsnohemispherestringToData(tempStr)
                                val tempStr2 = coordConv.singledmsnohemispheredataToString(tempData)
                                dmsText = tempStr2
                                val fakeData = GeneralData.emptyDMSData
                                fakeData.LonHemisphere = "E"
                                fakeData.LonDeg = tempData.Deg
                                fakeData.LonMin = tempData.Min
                                fakeData.LonSec = tempData.Sec
                                val fakeData2 = coordConv.dms2DEG(fakeData)

                                mgrsText = GeneralData.naStr
                                utmText = GeneralData.naStr
                                degText = fakeData2.Longitude.toString()
                                latText = GeneralData.naStr
                                lonText = GeneralData.naStr
                                garsText = GeneralData.naStr
                                georefText = GeneralData.naStr
                                gmapsText = GeneralData.naStr
                                wazeText = GeneralData.naStr
                            }
                            CoordType.SingleDMSLatitude -> {
                                val tempStr = inputText.replace("\\s+".toRegex(), "")
                                val tempData = coordConv.singledmsstringToData(tempStr)
                                val tempStr2 = coordConv.singledmsdataToString(tempData)
                                dmsText = tempStr2
                                val fakeData = GeneralData.emptyDMSData
                                fakeData.LatHemisphere = tempData.Hemisphere
                                fakeData.LatDeg = tempData.Deg
                                fakeData.LatMin = tempData.Min
                                fakeData.LatSec = tempData.Sec
                                val fakeData2 = coordConv.dms2DEG(fakeData)
                                mgrsText = GeneralData.naStr
                                utmText = GeneralData.naStr
                                degText = fakeData2.Latitude.toString()
                                latText = fakeData2.Latitude.toString()
                                lonText = GeneralData.naStr
                                garsText = GeneralData.naStr
                                georefText = GeneralData.naStr
                                gmapsText = GeneralData.naStr
                                wazeText = GeneralData.naStr
                            }
                            CoordType.SingleDMSLongitude -> {
                                val tempStr = inputText.replace("\\s+".toRegex(), "")
                                val tempData = coordConv.singledmsstringToData(tempStr)
                                val tempStr2 = coordConv.singledmsdataToString(tempData)
                                dmsText = tempStr2
                                val fakeData = GeneralData.emptyDMSData
                                fakeData.LonHemisphere = tempData.Hemisphere
                                fakeData.LonDeg = tempData.Deg
                                fakeData.LonMin = tempData.Min
                                fakeData.LonSec = tempData.Sec
                                val fakeData2 = coordConv.dms2DEG(fakeData)
                                mgrsText = GeneralData.naStr
                                utmText = GeneralData.naStr
                                degText = fakeData2.Longitude.toString()
                                latText = GeneralData.naStr
                                lonText = fakeData2.Longitude.toString()
                                garsText = GeneralData.naStr
                                georefText = GeneralData.naStr
                                gmapsText = GeneralData.naStr
                                wazeText = GeneralData.naStr
                            }
                            CoordType.GEOREF->{
                                val tempStr = inputText.replace("\\s+".toRegex(), "")
                                val degData = coordConv.georef2DEG(tempStr)
                                mgrsText = coordConv.mgrsdataToString(coordConv.deg2MGRS(degData))
                                degText = coordConv.degdataToString(degData)
                                utmText = coordConv.utmdataToString(coordConv.deg2UTM(degData))
                                dmsText = coordConv.dmsdataToString(coordConv.deg2DMS(degData))
                                latText = degData.Latitude.toString()
                                lonText = degData.Longitude.toString()
                                garsText = coordConv.deg2GARS(degData)
                                georefText = tempStr
                                gmapsText = coordConv.deg2GMaps(degData)
                                wazeText = coordConv.deg2Waze(degData)
                            }
                            CoordType.GARS->{
                                val tempStr = inputText.replace("\\s+".toRegex(), "")
                                val degData = coordConv.gars2DEG(tempStr)
                                mgrsText = coordConv.mgrsdataToString(coordConv.deg2MGRS(degData))
                                degText = coordConv.degdataToString(degData)
                                utmText = coordConv.utmdataToString(coordConv.deg2UTM(degData))
                                dmsText = coordConv.dmsdataToString(coordConv.deg2DMS(degData))
                                latText = degData.Latitude.toString()
                                lonText = degData.Longitude.toString()
                                garsText = tempStr
                                georefText = coordConv.deg2GEOREF(degData)
                                gmapsText = coordConv.deg2GMaps(degData)
                                wazeText = coordConv.deg2Waze(degData)
                            }
                            else -> { // Note the block
                                mgrsText = GeneralData.naStr
                                degText = GeneralData.naStr
                                utmText = GeneralData.naStr
                                dmsText = GeneralData.naStr
                                latText = GeneralData.naStr
                                lonText = GeneralData.naStr
                                garsText = GeneralData.naStr
                                georefText = GeneralData.naStr
                                gmapsText = GeneralData.naStr
                                wazeText = GeneralData.naStr
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
            Spacer(Modifier.width(20.dp))
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(Modifier.height(95.dp))
                Row() {
                    TextField(
                        value = garsText,
                        onValueChange = { garsText = it },
                        label = { Text("GARS") }
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row() {
                    TextField(
                        value = georefText,
                        onValueChange = { georefText = it },
                        label = { Text("GEOREF") }
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row() {
                    TextField(
                        value = gmapsText,
                        onValueChange = { gmapsText = it },
                        label = { Text("GoogleMaps") }
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row() {
                    TextField(
                        value = wazeText,
                        onValueChange = { wazeText = it },
                        label = { Text("Waze") }
                    )
                }

            }
            Spacer(Modifier.width(20.dp))
        }
    }


}


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Coordinate converter") {
        App()
    }
}
