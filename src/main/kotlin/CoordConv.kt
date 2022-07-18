import kotlin.math.*

/**
 * Created on 2022-07-18
 * @author Laszlo TAMAS9
 */
class CoordConv {
    private val gen = GeneralData()
    private fun rad2deg(number: Double): Double {
        return number * 180 / Math.PI
    }

    private fun deg2rad(number: Double): Double {
        return number * Math.PI / 180
    }

    fun deg2UTM(coord: DEGData): UTMData {
        val retVal = GeneralData.emptyUTMData
        val ellipRadius = 6378137.0
        val ellipEccSquared = 0.00669438
        val kZero = 0.9996
        val latRad = deg2rad(coord.Latitude)
        println("latlon2UTM - latRad $latRad")
        val lonRad = deg2rad(coord.Longitude)
        println("latlon2UTM - lonRad $lonRad")
        var zoneNumber = Math.floor((coord.Longitude + 180) / 6) + 1
        if (coord.Longitude == 180.0) {
            zoneNumber = 60.0
        }
        // Norway
        if (coord.Latitude >= 56.0 && coord.Latitude < 64.0 && coord.Longitude >= 3.0 && coord.Longitude < 12.0) {
            zoneNumber = 32.0
        }
        // Svalbard
        if (coord.Latitude >= 72.0 && coord.Latitude < 84.0) {
            if (coord.Longitude >= 0.0 && coord.Longitude < 9.0) {
                zoneNumber = 31.0
            } else if (coord.Longitude >= 9.0 && coord.Longitude < 21.0) {
                zoneNumber = 33.0
            } else if (coord.Longitude >= 21.0 && coord.Longitude < 33.0) {
                zoneNumber = 35.0
            } else if (coord.Longitude >= 33.0 && coord.Longitude < 42.0) {
                zoneNumber = 37.0
            }
        }
        println("latlon2UTM - zoneNumber $zoneNumber")
        val lonOrigin = (zoneNumber - 1) * 6 - 180 + 3
        println("latlon2UTM - lonOrigin $lonOrigin")
        val lonOriginRad = deg2rad(lonOrigin)
        println("latlon2UTM - lonOriginRad $lonOriginRad")
        val eccPrimeSquared = ellipEccSquared / (1 - ellipEccSquared)
        println("latlon2UTM - eccPrimeSquared $eccPrimeSquared")
        val nVal = ellipRadius / Math.sqrt(1 - ellipEccSquared * Math.sin(latRad) * Math.sin(latRad))
        println("latlon2UTM - nVal $nVal")
        val tVal = Math.tan(latRad) * Math.tan(latRad)
        println("latlon2UTM - tVal $tVal")
        val cVal = eccPrimeSquared * Math.cos(latRad) * Math.cos(latRad)
        println("latlon2UTM - cVal $cVal")
        val aVal = Math.cos(latRad) * (lonRad - lonOriginRad)
        println("latlon2UTM - aVal $aVal")

        val mVal =
            ellipRadius * ((1 - ellipEccSquared / 4 - 3 * ellipEccSquared * ellipEccSquared / 64 - 5
                    * ellipEccSquared * ellipEccSquared * ellipEccSquared / 256)
                    * latRad - (3 * ellipEccSquared / 8 + 3 * ellipEccSquared * ellipEccSquared / 32
                    + 45 * ellipEccSquared * ellipEccSquared * ellipEccSquared / 1024)
                    * sin(2 * latRad)
                    + (15 * ellipEccSquared * ellipEccSquared / 256 + 45 * ellipEccSquared * ellipEccSquared * ellipEccSquared / 1024)
                    * sin(4 * latRad)
                    - (35 * ellipEccSquared * ellipEccSquared * ellipEccSquared / 3072) * sin(6 * latRad)
                    )

        println("latlon2UTM - mVal $mVal")
        val utmEasting =
            kZero * nVal * (aVal + (1 - tVal + cVal) * aVal * aVal * aVal / 6.0 + (5 - 18 * tVal + tVal * tVal + 72 * cVal - 58 * eccPrimeSquared) * aVal * aVal * aVal * aVal *
                    aVal / 120.0) + 500000.0
        println("latlon2UTM - utmEasting $utmEasting")
        var utmNorthing =
            kZero * (mVal + nVal * tan(latRad) * (aVal * aVal / 2 + (5 - tVal + 9 * cVal + 4 * cVal * cVal) *
                    aVal * aVal * aVal * aVal / 24.0 + (61 - 58 * tVal + tVal * tVal + 600 * cVal - 330 * eccPrimeSquared) *
                    aVal * aVal * aVal * aVal * aVal * aVal / 720.0))
        if (coord.Latitude < 0.0) {
            utmNorthing += 10000000.0
        }
        println("latlon2UTM - utmNorthing $utmNorthing")
        retVal.Northing = Math.round(Math.floor(utmNorthing + 0.5)).toInt()
        retVal.Easting = Math.round(Math.floor(utmEasting + 0.5)).toInt()
        retVal.ZoneNumber = Math.round(zoneNumber).toInt()
        retVal.ZoneLetter = utmLetterDesignator(coord.Latitude)
        return retVal
    }

    fun utm2MGRS(coord: UTMData, accuracy: Int): MGRSData {
        println("utm2MGRS - coord $coord")
        val retVal = GeneralData.emptyMGRSData
        val tempEasting = "00000" + coord.Easting
        val tempNorthing = "00000" + coord.Northing
        retVal.ZoneNumber = coord.ZoneNumber
        retVal.ZoneLetter = coord.ZoneLetter
        val hunK = get100kID(coord.Easting.toDouble(), coord.Northing.toDouble(), coord.ZoneNumber.toDouble())
        retVal.ID100kCol = hunK.substring(0, 1)
        retVal.ID100kRow = hunK.substring(1)
        retVal.Accuracy = accuracy
        retVal.Easting = tempEasting.substring(tempEasting.length - 5).substring(0, accuracy).toInt()
        retVal.Northing = tempNorthing.substring(tempNorthing.length - 5).substring(0, accuracy).toInt()
        println("utm2MGRS - RETURN $retVal")
        return retVal
    }

    fun mgrs2UTM(coord: MGRSData): UTMData {
        val retVal = GeneralData.emptyUTMData
        retVal.ZoneNumber = coord.ZoneNumber
        retVal.ZoneLetter = coord.ZoneLetter
        val hunK = coord.ID100kCol + coord.ID100kRow
        val mgrsSet = get100kSet4Zone(coord.ZoneNumber.toDouble())
        println("mgrs2UTM - mgrsSet $mgrsSet")
        val east100k = getEastingFromChar(hunK.substring(0, 1), mgrsSet)
        println("mgrs2UTM - east100k $east100k")
        var north100k = getNorthingFromChar(hunK.substring(1, 2), mgrsSet)
        while (north100k < getMinNorthing(coord.ZoneLetter)) {
            north100k += 2000000.0
        }
        println("mgrs2UTM - north100k $north100k")
        var separatedEasting = 0.0
        var separatedNorthing = 0.0
        var accuracyBonus = 0.0
        var separatedEastingStr = ""
        var separatedNorthingStr = ""
        val tempEasting = coord.Easting.toString().padStart(5, '0').takeLast(coord.Accuracy)
        val tempNorthing = coord.Northing.toString().padStart(5, '0').takeLast(coord.Accuracy)
        val coordPart = tempEasting + tempNorthing
        if (coord.Accuracy > 0) {
            accuracyBonus = 100000.0 / Math.pow(10.0, coord.Accuracy.toDouble())
            separatedEastingStr = coordPart.substring(0, coord.Accuracy)
            separatedEasting = separatedEastingStr.toDouble() * accuracyBonus
            separatedNorthingStr = coordPart.substring(coord.Accuracy)
            separatedNorthing = separatedNorthingStr.toDouble() * accuracyBonus
        }
        println("mgrs2UTM - accuracyBonus $accuracyBonus")
        println("mgrs2UTM - separatedEastingStr $separatedEastingStr")
        println("mgrs2UTM - separatedEasting $separatedEasting")
        println("mgrs2UTM - separatedNorthingStr $separatedNorthingStr")
        println("mgrs2UTM - separatedNorthing $separatedNorthing")
        val easting = separatedEasting + east100k
        val northing = separatedNorthing + north100k
        retVal.Northing = northing.toInt()
        retVal.Easting = easting.toInt()
        println("mgrs2UTM - RETURN $retVal")
        return retVal
    }

    fun utm2DEG(coord: UTMData): DEGData {
        val retVal = GeneralData.emptyDEGData
        val utmNorthing = coord.Northing
        val utmEasting = coord.Easting
        val zoneLetter = coord.ZoneLetter
        val zoneNumber = coord.ZoneNumber
        if (zoneNumber < 0) {
            return retVal
        }
        val kZero = 0.9996
        val ellipRadius = 6378137.0
        val ellipEccSquared = 0.00669438
        val e1Val = (1 - Math.sqrt(1 - ellipEccSquared)) / (1 + Math.sqrt(1 - ellipEccSquared))
        val xValue = utmEasting.toDouble() - 500000.0
        var yValue = utmNorthing.toDouble()
        if (zoneLetter.toCharArray()[0].code < "N".toCharArray()[0].code) {
            yValue -= 10000000.0
        }
        println("utm2DEG - yValue $yValue")
        val lonOrigin = (zoneNumber - 1) * 6 - 180 + 3
        println("utm2DEG - lonOrigin $lonOrigin")
        val eccPrimeSquared = ellipEccSquared / (1 - ellipEccSquared)
        println("utm2DEG - eccPrimeSquared $eccPrimeSquared")
        val mVal = yValue / kZero
        println("utm2DEG - mVal $mVal")
        val muVal = mVal / (ellipRadius * (1 - ellipEccSquared / 4 - 3 * ellipEccSquared *
                ellipEccSquared / 64 - 5 * ellipEccSquared * ellipEccSquared * ellipEccSquared / 256))
        println("utm2DEG - muVal $muVal")
        val phi1Rad =
            muVal + (3 * e1Val / 2 - 27 * e1Val * e1Val * e1Val / 32) * sin(2 * muVal) + (21 * e1Val * e1Val / 16 - 55 * e1Val * e1Val * e1Val * e1Val / 32) * sin(
                4 * muVal
            ) + 151 * e1Val * e1Val * e1Val / 96 * sin(6 * muVal)
        println("utm2DEG - phi1Rad $phi1Rad")
        val vEllipse = 1 - ellipEccSquared * Math.sin(phi1Rad) * Math.sin(phi1Rad)
        println("utm2DEG - vEllipse $vEllipse")
        val n1Val = ellipRadius / Math.sqrt(vEllipse)
        println("utm2DEG - n1Val $n1Val")
        val t1Val = Math.tan(phi1Rad) * Math.tan(phi1Rad)
        println("utm2DEG - t1Val $t1Val")
        val c1Val = eccPrimeSquared * Math.cos(phi1Rad) * Math.cos(phi1Rad)
        println("utm2DEG - c1Val $c1Val")
        val r1Val = ellipRadius * (1 - ellipEccSquared) / Math.pow(vEllipse, 1.5)
        println("utm2DEG - r1Val $r1Val")
        val dVal = xValue / (n1Val * kZero)
        println("utm2DEG - dVal $dVal")
        val lat = phi1Rad - (n1Val * tan(phi1Rad) / r1Val) * (dVal * dVal / 2 -
                (5 + 3 * t1Val + 10 * c1Val - 4 * c1Val * c1Val - 9 * eccPrimeSquared) *
                dVal * dVal * dVal * dVal / 24 + (61 + 90 * t1Val + 298 * c1Val + 45 *
                t1Val * t1Val - 252 * eccPrimeSquared - 3 * c1Val * c1Val) * dVal * dVal * dVal * dVal * dVal * dVal / 720)
        println("utm2DEG - lat $lat")
        val latDeg = rad2deg(lat)
        val lon = (dVal - (1 + 2 * t1Val + c1Val) * dVal * dVal * dVal / 6 +
                (5 - 2 * c1Val + 28 * t1Val - 3 * c1Val * c1Val + 8 * eccPrimeSquared + 24 * t1Val * t1Val) *
                dVal * dVal * dVal * dVal * dVal / 120) / cos(phi1Rad)
        println("utm2DEG - lon $lon")
        val lonDeg = lonOrigin.toDouble() + rad2deg(lon)
        retVal.Latitude = latDeg
        retVal.Longitude = lonDeg
        println("utm2DEG - RETURN $retVal")
        return retVal
    }

    fun deg2DMS(coord: DEGData): DMSData {
        println("deg2DMS - coord $coord")
        var retVal = GeneralData.emptyDMSData
        retVal.LatHemisphere = "N"
        retVal.LonHemisphere = "E"
        if (coord.Latitude < 0) {
            retVal.LatHemisphere = "S"
        }
        if (coord.Longitude < 0) {
            retVal.LonHemisphere = "W"
        }
        println("deg2DMS - LatHemisphere ${retVal.LatHemisphere}")
        println("deg2DMS - LonHemisphere ${retVal.LonHemisphere}")
        retVal.LatDeg = abs(coord.Latitude.toInt())
        val latmin = (coord.Latitude - coord.Latitude.toInt().toDouble()).toDouble() * 60.0
        retVal.LatMin = abs(latmin.toInt())
        val latsec = (latmin - latmin.toInt().toDouble()).toDouble() * 60.0
        retVal.LatSec = abs(latsec * 1000.0).roundToInt() / 1000.0
        retVal.LonDeg = abs(coord.Longitude.toInt())
        val lonmin = (coord.Longitude - coord.Longitude.toInt().toDouble()).toDouble() * 60.0
        retVal.LonMin = abs(lonmin.toInt())
        val lonsec = (lonmin - lonmin.toInt().toDouble()).toDouble() * 60.0
        retVal.LonSec = abs(lonsec * 1000.0).roundToInt() / 1000.0
        println("deg2DMS - RETURN $retVal")
        return retVal
    }

    fun deg2MGRS(coord: DEGData): MGRSData {
        return utm2MGRS(deg2UTM(coord), 5)
    }
    fun mgrs2DEG(coord: MGRSData):DEGData{
        return utm2DEG(mgrs2UTM(coord))
    }

    fun mgrs2DMS(coord:MGRSData):DMSData{
        return deg2DMS(mgrs2DEG(coord))
    }
    fun dms2DEG(coord: DMSData): DEGData {
        println("deg2DMS - coord $coord")
        var retVal = GeneralData.emptyDEGData
        retVal.Latitude = coord.LatDeg.toDouble() + coord.LatMin / 60.0 + coord.LatSec / 3600.0
        if (coord.LatHemisphere == "S") {
            retVal.Latitude = -1 * retVal.Latitude
        }
        println("deg2DMS - Latitude ${retVal.Latitude}")
        retVal.Longitude = coord.LonDeg.toDouble() + coord.LonMin / 60.0 + coord.LonSec / 3600.0
        if (coord.LonHemisphere == "W") {
            retVal.Longitude = -1 * retVal.Longitude
        }
        println("deg2DMS - Longitude ${retVal.Longitude}")
        println("deg2DMS - RETURN $retVal")
        return retVal
    }

    fun dms2UTM(coord: DMSData): UTMData {
        return deg2UTM(dms2DEG(coord))
    }

    fun utm2DMS(coord:UTMData):DMSData{
        return deg2DMS(utm2DEG(coord))
    }
    fun dms2MGRS(coord: DMSData): MGRSData {
        return deg2MGRS(dms2DEG(coord))
    }

    private fun getEastingFromChar(mgrsFirstLetter: String, mgrsSet: Int): Double {
        var index = mgrsSet - 1
        var currentColumn = SET_ORIGIN_COLUMN_LETTERS.toCharArray()[index].code
        println("getEastingFromChar - currentColumn $currentColumn")
        var eastingValue = 100000.0
        index = 0
        while (currentColumn != mgrsFirstLetter.toCharArray()[0].code) {
            index += 1
            currentColumn += 1
            if (currentColumn == VAL_I) {
                currentColumn += 1
            }
            if (currentColumn == VAL_O) {
                currentColumn += 1
            }
            if (currentColumn > VAL_Z) {
                currentColumn = VAL_A
            }
            eastingValue += 100000.0
        }
        println("getEastingFromChar - RETURN $eastingValue")
        return eastingValue
    }

    private fun getNorthingFromChar(mgrsSecondLetter: String, mgrsSet: Int): Double {
        println("getNorthingFromChar - mgrsSecondLetter $mgrsSecondLetter")
        val index = mgrsSet - 1
        var currentRow = SET_ORIGIN_ROW_LETTERS.toCharArray()[index].code
        println("getNorthingFromChar - currentRow $currentRow")
        var northingValue = 0.0
        while (currentRow != mgrsSecondLetter.toCharArray()[0].code) {
            currentRow += 1
            if (currentRow == VAL_I) {
                currentRow += 1
            }
            if (currentRow == VAL_O) {
                currentRow += 1
            }
            if (currentRow > VAL_V) {
                currentRow = VAL_A
            }
            northingValue += 100000.0
        }
        println("getNorthingFromChar - RETURN $northingValue")
        return northingValue
    }

    private fun getMinNorthing(zoneLetter: String): Double {
        println("getMinNorthing - zoneLetter $zoneLetter")
        val retVal: Double
        val letters: MutableMap<String, Double> = HashMap()
        letters["C"] = 1100000.0
        letters["D"] = 2000000.0
        letters["E"] = 2800000.0
        letters["F"] = 3700000.0
        letters["G"] = 4600000.0
        letters["H"] = 5500000.0
        letters["J"] = 6400000.0
        letters["K"] = 7300000.0
        letters["L"] = 8200000.0
        letters["M"] = 9100000.0
        letters["N"] = 0.0
        letters["P"] = 800000.0
        letters["Q"] = 1700000.0
        letters["R"] = 2600000.0
        letters["S"] = 3500000.0
        letters["T"] = 4400000.0
        letters["U"] = 5300000.0
        letters["V"] = 6200000.0
        letters["W"] = 7000000.0
        letters["X"] = 7900000.0
        retVal = letters[zoneLetter]!!
        println("getMinNorthing - RETURN $retVal")
        return retVal
    }

    private fun get100kID(easting: Double, northing: Double, zone_number: Double): String {
        println("get100kID - easting $easting")
        println("get100kID - northing $northing")
        println("get100kID - zone_number $zone_number")
        val setParm = get100kSet4Zone(zone_number)
        println("get100kID - setParm $setParm")
        val setColumn = Math.floor(easting / 100000)
        println("get100kID - setColumn $setColumn")
        val setRow = Math.floor(northing / 100000) % 20
        println("get100kID - setRow $setRow")
        val retVal = getLetter100kID(Math.round(setColumn).toInt(), Math.round(setRow).toInt(), setParm)
        println("get100kID - RETURN $retVal")
        return retVal
    }

    private fun get100kSet4Zone(zoneNumber: Double): Int {
        println("get100kSet4Zone - zoneNumber $zoneNumber")
        val NUM_100K_SETS = 6
        var retVal = Math.round(zoneNumber).toInt() % NUM_100K_SETS
        if (retVal == 0) {
            retVal = NUM_100K_SETS
        }
        println("get100kSet4Zone - RETURN $retVal")
        return retVal
    }

    private fun getLetter100kID(column: Int, row: Int, parm: Int): String {
        println("getLetter100kID - column $column, row $row, parm $parm")
        val retVal: String
        val index = parm - 1
        val colOrigin = SET_ORIGIN_COLUMN_LETTERS.toCharArray()[index].code
        val rowOrigin = SET_ORIGIN_ROW_LETTERS.toCharArray()[index].code
        println("getLetter100kID - colOrigin $colOrigin, rowOrigin $rowOrigin")
        var colInt = colOrigin + column - 1
        var rowInt = rowOrigin + row
        println("getLetter100kID - colInt $colInt, rowInt $rowInt")
        var rollover = false
        if (colInt > VAL_Z) {
            colInt = colInt - VAL_Z + VAL_A - 1
            rollover = true
        }
        if (colInt == VAL_I || (colOrigin < VAL_I && colInt > VAL_I) || ((colInt > VAL_I || colOrigin < VAL_I) && rollover)) {
            colInt += 1
        }
        if (colInt == VAL_O || (colOrigin < VAL_O && colInt > VAL_O) || ((colInt > VAL_O || colOrigin < VAL_O) && rollover)) {
            colInt += 1
        }
        if (colInt == VAL_I) {
            colInt += 1
        }
        if (colInt > VAL_Z) {
            colInt = colInt - VAL_Z + VAL_A - 1
        }
        if (rowInt > VAL_V) {
            rowInt = rowInt - VAL_V + VAL_A - 1
            rollover = true
        } else {
            rollover = false
        }
        if (((rowInt == VAL_I) || ((rowOrigin < VAL_I) && (rowInt > VAL_I))) || (((rowInt > VAL_I) || (rowOrigin < VAL_I)) && rollover)) {
            rowInt += 1
        }
        if (((rowInt == VAL_O) || ((rowOrigin < VAL_O) && (rowInt > VAL_O))) || (((rowInt > VAL_O) || (rowOrigin < VAL_O)) && rollover)) {
            rowInt += 1
        }
        if (rowInt == VAL_I) {
            rowInt += 1
        }
        if (rowInt > VAL_V) {
            rowInt = rowInt - VAL_V + VAL_A - 1
        }
        println("getLetter100kID - colInt mod $colInt, rowInt mod $rowInt")
        retVal = colInt.toChar().toString() + Character.toString(rowInt.toChar())
        println("getLetter100kID - RETURN $retVal")
        return retVal
    }

    private fun utmLetterDesignator(lat: Double): String {
        var retVal = "Z"
        if (72 <= lat && lat <= 84) {
            retVal = "X"
        } else if (64 <= lat && lat < 72) {
            retVal = "W"
        } else if (56 <= lat && lat < 64) {
            retVal = "V"
        } else if (48 <= lat && lat < 56) {
            retVal = "U"
        } else if (40 <= lat && lat < 48) {
            retVal = "T"
        } else if (32 <= lat && lat < 40) {
            retVal = "S"
        } else if (24 <= lat && lat < 32) {
            retVal = "R"
        } else if (16 <= lat && lat < 24) {
            retVal = "Q"
        } else if (8 <= lat && lat < 16) {
            retVal = "P"
        } else if (0 <= lat && lat < 8) {
            retVal = "N"
        } else if (-8 <= lat && lat < 0) {
            retVal = "M"
        } else if (-16 <= lat && lat < -8) {
            retVal = "L"
        } else if (-24 <= lat && lat < -16) {
            retVal = "K"
        } else if (-32 <= lat && lat < -24) {
            retVal = "J"
        } else if (-40 <= lat && lat < -32) {
            retVal = "H"
        } else if (-48 <= lat && lat < -40) {
            retVal = "G"
        } else if (-56 <= lat && lat < -48) {
            retVal = "F"
        } else if (-64 <= lat && lat < -56) {
            retVal = "E"
        } else if (-72 <= lat && lat < -64) {
            retVal = "D"
        } else if (-80 <= lat && lat < -72) {
            retVal = "C"
        }
        println("utmLetterDesignator - RETURN $retVal")
        return retVal
    }

    // String To Data
    fun mgrsstringToData(coord: String): MGRSData {
        var retVal = GeneralData.emptyMGRSData
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternMGRS
        val matcher = pattern.matcher(tempStr)
        var matchCount = 0
        while (matcher.find()) {
//            matchCount++
//            System.out.printf(
//                "Match count: %s, Group Zero Text: '%s'%n", matchCount,
//                matcher.group()
//            )
//            for (i in 1..matcher.groupCount()) {
//                System.out.printf(
//                    "Capture Group Number: %s, Captured Text: '%s'%n", i,
//                    matcher.group(i)
//                )
//            }
            if (matcher.group(5).length % 2 == 0) {
                val temp1 = matcher.group(1).toInt()
                val temp2 = matcher.group(5).count() / 2
                val temp3 = matcher.group(5).take(temp2)
                val temp4 = matcher.group(5).takeLast(temp2)
                retVal.ZoneNumber = temp1
                retVal.ZoneLetter = matcher.group(2)
                retVal.ID100kCol = matcher.group(3)
                retVal.ID100kRow = matcher.group(4)
                retVal.Easting = temp3.toInt()
                retVal.Northing = temp4.toInt()
                retVal.Accuracy = temp2
            }
        }
        println("mgrsstringToData - RETURN $retVal")
        return retVal
    }

    fun utmtringToData(coord: String): UTMData {
        var retVal = GeneralData.emptyUTMData
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternUTM
        val matcher = pattern.matcher(tempStr)
        var matchCount = 0
        while (matcher.find()) {
//            matchCount++
//            System.out.printf(
//                "Match count: %s, Group Zero Text: '%s'%n", matchCount,
//                matcher.group()
//            )
//            for (i in 1..matcher.groupCount()) {
//                System.out.printf(
//                    "Capture Group Number: %s, Captured Text: '%s'%n", i,
//                    matcher.group(i)
//                )
//            }
            val temp1 =matcher.group(1).toInt()
            val temp2 =matcher.group(3).toInt()
            val temp3 =matcher.group(4).toInt()
            retVal.ZoneNumber=temp1
            retVal.ZoneLetter=matcher.group(2)
            retVal.Easting=temp2
            retVal.Northing=temp3
        }
        return retVal
    }

    // Data To String
    fun degdataToString(coord: DEGData): String {
        return coord.Latitude.toString() + "," + coord.Longitude
    }

    fun utmdataToString(coord: UTMData): String {
        return (coord.ZoneNumber.toString() + "," + coord.ZoneLetter + ","
                + coord.Easting + "," + coord.Northing)
    }

    fun mgrsdataToString(coord: MGRSData): String {
        println("mgrsdataToString - coord $coord")
        var retVal = ""
        val tempEasting = coord.Easting.toString().padStart(5, '0').takeLast(coord.Accuracy)
        val tempNorthing = coord.Northing.toString().padStart(5, '0').takeLast(coord.Accuracy)
        println("mgrsdataToString - tempEasting $tempEasting tempNorthing $tempNorthing")
        if (coord.Accuracy >= 0 && coord.Accuracy <= 5) {
            retVal = (coord.ZoneNumber.toString() + coord.ZoneLetter + coord.ID100kCol
                    + coord.ID100kRow + tempEasting + tempNorthing)
        }
        return retVal
    }

    fun dmsdataToString(coord: DMSData): String {
        println("dmsdataToString - coord $coord")
        return coord.LatDeg.toString() + GeneralData.degChar +
                coord.LatMin.toString() + GeneralData.minChar +
                coord.LatSec.toString() + GeneralData.secChar +
                coord.LatHemisphere + "," +
                coord.LonDeg.toString() + GeneralData.degChar +
                coord.LonMin.toString() + GeneralData.minChar +
                coord.LonSec.toString() + GeneralData.secChar +
                coord.LonHemisphere

    }

    companion object {
        private const val SET_ORIGIN_COLUMN_LETTERS = "AJSAJS"
        private const val SET_ORIGIN_ROW_LETTERS = "AFAFAF"
        private const val VAL_A = 65
        private const val VAL_I = 73
        private const val VAL_O = 79
        private const val VAL_V = 86
        private const val VAL_Z = 90
    }
}