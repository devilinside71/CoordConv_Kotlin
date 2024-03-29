import kotlin.math.*

/**
 * Created on 2022-07-18
 * @author Laszlo TAMAS9
 */
class CoordConv {


    //region From DEG

    fun degstringToData(coord: String): DEGData {
        val retVal = GeneralData.emptyDEGData
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternDEG
        val matcher = pattern.matcher(tempStr)
        var matchCount = 0
        while (matcher.find()) {
            matchCount++
            System.out.printf(
                "Match count: %s, Group Zero Text: '%s'%n", matchCount,
                matcher.group()
            )
            for (i in 1..matcher.groupCount()) {
                System.out.printf(
                    "Capture Group Number: %s, Captured Text: '%s'%n", i,
                    matcher.group(i)
                )
            }
            val temp1 = matcher.group(1).toDouble()
            val temp2 = matcher.group(2).toDouble()
            if (temp1 <= 90.0 && temp1 >= -90.0 && temp2 <= 180.0 && temp2 >= -180.0) {
                retVal.Latitude = temp1
                retVal.Longitude = temp2
            }

        }
        println("degstringToData - RETURN $retVal")
        return retVal
    }

    fun degdataToString(coord: DEGData): String {
        return coord.Latitude.toString() + "," + coord.Longitude
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
        var zoneNumber = floor((coord.Longitude + 180) / 6) + 1
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
        val nVal = ellipRadius / sqrt(1 - ellipEccSquared * sin(latRad) * sin(latRad))
        println("latlon2UTM - nVal $nVal")
        val tVal = tan(latRad) * tan(latRad)
        println("latlon2UTM - tVal $tVal")
        val cVal = eccPrimeSquared * cos(latRad) * cos(latRad)
        println("latlon2UTM - cVal $cVal")
        val aVal = cos(latRad) * (lonRad - lonOriginRad)
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
        retVal.Northing = floor(utmNorthing + 0.5).roundToLong().toInt()
        retVal.Easting = floor(utmEasting + 0.5).roundToLong().toInt()
        retVal.ZoneNumber = zoneNumber.roundToLong().toInt()
        retVal.ZoneLetter = utmLetterDesignator(coord.Latitude)
        return retVal
    }

    fun deg2DMS(coord: DEGData): DMSData {
        println("deg2DMS - coord $coord")
        val retVal = GeneralData.emptyDMSData
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
        val latmin = (coord.Latitude - coord.Latitude.toInt().toDouble()) * 60.0
        retVal.LatMin = abs(latmin.toInt())
        val latsec = (latmin - latmin.toInt().toDouble()) * 60.0
        retVal.LatSec = abs(latsec * 1000.0).roundToInt() / 1000.0
        retVal.LonDeg = abs(coord.Longitude.toInt())
        val lonmin = (coord.Longitude - coord.Longitude.toInt().toDouble()) * 60.0
        retVal.LonMin = abs(lonmin.toInt())
        val lonsec = (lonmin - lonmin.toInt().toDouble()) * 60.0
        retVal.LonSec = abs(lonsec * 1000.0).roundToInt() / 1000.0
        println("deg2DMS - RETURN $retVal")
        return retVal
    }

    fun deg2MGRS(coord: DEGData): MGRSData {
        return utm2MGRS(deg2UTM(coord), 5)
    }

    fun deg2GMaps(coord: DEGData): String {
        var retVal = "https://www.google.hu/maps/place/"
        retVal = retVal + coord.Latitude.toString() + "%2C" +
                coord.Longitude.toString() + "/@" +
                coord.Latitude.toString() + "%2C" +
                coord.Longitude.toString() + ",17z"
        return retVal
    }

    fun deg2Waze(coord: DEGData): String {
        var retVal = "https://ul.waze.com/ul?ll="
        retVal = retVal + coord.Latitude.toString() + "%2C" +
                coord.Longitude.toString() + "&navigate=yes"
        return retVal
    }
    //endregion

    //region From MGRS

    fun mgrsstringToData(coord: String): MGRSData {
        val retVal = GeneralData.emptyMGRSData
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

    fun mgrs2DEG(coord: MGRSData): DEGData {
        return utm2DEG(mgrs2UTM(coord))
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
            accuracyBonus = 100000.0 / 10.0.pow(coord.Accuracy.toDouble())
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

    fun mgrs2DMS(coord: MGRSData): DMSData {
        return deg2DMS(mgrs2DEG(coord))
    }

    //endregion


    //region From UTM

    fun utmstringToData(coord: String): UTMData {
        val retVal = GeneralData.emptyUTMData
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
            val temp1 = matcher.group(1).toInt()
            val temp2 = matcher.group(3).toInt()
            val temp3 = matcher.group(4).toInt()
            retVal.ZoneNumber = temp1
            retVal.ZoneLetter = matcher.group(2)
            retVal.Easting = temp2
            retVal.Northing = temp3
        }
        println("utmstringToData - RETURN $retVal")
        return retVal
    }

    fun utmdataToString(coord: UTMData): String {
        return (coord.ZoneNumber.toString() + "," + coord.ZoneLetter + ","
                + coord.Easting + "," + coord.Northing)
    }

    fun utm2DMS(coord: UTMData): DMSData {
        return deg2DMS(utm2DEG(coord))
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
        val e1Val = (1 - sqrt(1 - ellipEccSquared)) / (1 + sqrt(1 - ellipEccSquared))
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
        val vEllipse = 1 - ellipEccSquared * sin(phi1Rad) * sin(phi1Rad)
        println("utm2DEG - vEllipse $vEllipse")
        val n1Val = ellipRadius / sqrt(vEllipse)
        println("utm2DEG - n1Val $n1Val")
        val t1Val = tan(phi1Rad) * tan(phi1Rad)
        println("utm2DEG - t1Val $t1Val")
        val c1Val = eccPrimeSquared * cos(phi1Rad) * cos(phi1Rad)
        println("utm2DEG - c1Val $c1Val")
        val r1Val = ellipRadius * (1 - ellipEccSquared) / vEllipse.pow(1.5)
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

    //endregion

    //region From DMS

    fun dmsstringToData(coord: String): DMSData {
        val retVal = GeneralData.emptyDMSData
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternDMS
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
            val valD1 = matcher.group(1).toDouble()
            val valD2 = matcher.group(2).toDouble()
            val valD3 = matcher.group(3).toDouble()
            val valD6 = matcher.group(6).toDouble()
            val valD7 = matcher.group(7).toDouble()
            val valD8 = matcher.group(8).toDouble()
            if (valD1 <= 90.0 && valD1 >= -90.0 && valD2 >= 0.0 && valD2 <= 60.0 && valD3 >= 0.0
                && valD3 <= 60.0 && valD6 <= 180.0 && valD6 >= -180.0 && valD7 >= 0.0
                && valD7 <= 60.0 && valD8 >= 0.0 && valD8 <= 60.0
            ) {
                retVal.LatHemisphere = matcher.group(5)
                retVal.LatDeg = valD1.toInt()
                retVal.LatMin = valD2.toInt()
                retVal.LatSec = valD3
                retVal.LonHemisphere = matcher.group(10)
                retVal.LonDeg = valD6.toInt()
                retVal.LonMin = valD7.toInt()
                retVal.LonSec = valD8

            }
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

    fun dms2DEG(coord: DMSData): DEGData {
        println("deg2DMS - coord $coord")
        val retVal = GeneralData.emptyDEGData
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

    fun dms2MGRS(coord: DMSData): MGRSData {
        return deg2MGRS(dms2DEG(coord))
    }

    //endregion

    //region From SingleDEG

    fun singledegstringToData(coord: String): Double {
        var retVal = 0.0
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternSingleDEG
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
            val valD1 = matcher.group(1).toDouble()
            if (valD1 <= 180.0 && valD1 >= -180.0) {
                retVal = valD1
            }
        }
        return retVal
    }

    //endregion

    //region From SingleDMS
    fun singledmsdataToString(coord: SingleDMSData): String {
        return coord.Deg.toString() + GeneralData.degChar +
                coord.Min.toString() + GeneralData.minChar +
                coord.Sec.toString() + GeneralData.secChar +
                coord.Hemisphere
    }

    fun singledmsstringToData(coord: String): SingleDMSData {
        val retVal = GeneralData.emptySingleDMSData
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternSingleDMS
        val matcher = pattern.matcher(tempStr)
        var matchCount = 0
        while (matcher.find()) {
            matchCount++
            System.out.printf(
                "Match count: %s, Group Zero Text: '%s'%n", matchCount,
                matcher.group()
            )
            for (i in 1..matcher.groupCount()) {
                System.out.printf(
                    "Capture Group Number: %s, Captured Text: '%s'%n", i,
                    matcher.group(i)
                )
            }
            val valD1 = matcher.group(1).toDouble()
            val valD2 = matcher.group(2).toDouble()
            val valD3 = matcher.group(3).toDouble()
            if (valD1 <= 180.0 && valD1 >= -180.0 && valD2 >= 0.0 && valD2 <= 60.0 && valD3 >= 0.0 && valD3 <= 60.0) {
                retVal.Deg = valD1.toInt()
                retVal.Min = valD2.toInt()
                retVal.Sec = valD3
                retVal.Hemisphere = matcher.group(5)
            }
        }

        return retVal
    }

    fun singledmsnohemispheredataToString(coord: SingleDMSNoHemisphereData): String {
        return coord.Deg.toString() + GeneralData.degChar +
                coord.Min.toString() + GeneralData.minChar +
                coord.Sec.toString() + GeneralData.secChar
    }

    fun singledmsnohemispherestringToData(coord: String): SingleDMSNoHemisphereData {
        val retVal = GeneralData.emptySingleDMSNoHemisphereData
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternSingleDMSNoHemisphere
        val matcher = pattern.matcher(tempStr)
        var matchCount = 0
        while (matcher.find()) {
            matchCount++
            System.out.printf(
                "Match count: %s, Group Zero Text: '%s'%n", matchCount,
                matcher.group()
            )
            for (i in 1..matcher.groupCount()) {
                System.out.printf(
                    "Capture Group Number: %s, Captured Text: '%s'%n", i,
                    matcher.group(i)
                )
            }
            val valD1 = matcher.group(1).toDouble()
            val valD2 = matcher.group(2).toDouble()
            val valD3 = matcher.group(3).toDouble()
            if (valD1 <= 180.0 && valD1 >= -180.0 && valD2 >= 0.0 && valD2 <= 60.0 && valD3 >= 0.0 && valD3 <= 60.0) {
                retVal.Deg = valD1.toInt()
                retVal.Min = valD2.toInt()
                retVal.Sec = valD3
            }
        }
        return retVal
    }

    //endregion

    //region GEOREF
    fun deg2GEOREF(coord: DEGData): String {
        println("deg2GEOREF coord $coord")
        val retVal = georefLon15(coord.Longitude) +
                georefLat15(coord.Latitude) +
                georefLon1515(coord.Longitude) +
                georefLat1515(coord.Latitude) +
                georef151560(coord.Longitude) +
                georef151560(coord.Latitude)
        println("deg2GEOREF RETURN $retVal")
        return retVal
    }

    fun georef2DEG(coord: String): DEGData {
        val retVal = DEGData(0.0, 0.0)
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternGEOREF
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
            val mLength = matcher.group(5).length
            if (mLength % 2 == 0) {
                var lonMulti: Int
                var latMulti: Int
                val lon1 = matcher.group(1)
                var lon1Val = georef15IDVal(lon1)
                println("georef2DEG lon1Val $lon1Val")
                val lat1 = matcher.group(2)
                var lat1Val = georef15IDVal(lat1)
                println("georef2DEG lat1Val $lat1Val")
                val lon2 = matcher.group(3)
                val lon2Val = georef1515IDVal(lon2)
                println("georef2DEG lon2Val $lon2Val")
                val lat2 = matcher.group(4)
                val lat2Val = georef1515IDVal(lat2)
                println("georef2DEG lat2Val $lat2Val")
                if (lon1Val < 180) {
                    lon1Val = 180 - lon1Val
                    lonMulti = -1
                } else {
                    lon1Val -= 180
                    lonMulti = 1
                }
                println("georef2DEG lon1Val mod $lon1Val")
                lon1Val += lonMulti * lon2Val
                println("georef2DEG lon1Val mod2 $lon1Val")
                if (lat1Val < 90) {
                    lat1Val = 90 - lat1Val
                    latMulti = -1
                } else {
                    lat1Val -= 90
                    latMulti = 1
                }
                println("georef2DEG lat1Val mod $lat1Val")
                lat1Val += latMulti * lat2Val
                println("georef2DEG lat1Val mod2 $lat1Val")
                val nums = matcher.group(5).take(4)
                val lon3 = nums.substring(0, 2)
                val lat3 = nums.substring(2, 4)
                println("georef2DEG nums $lon3 $lat3")
                var lon4 = lon1Val.toDouble() + lonMulti * (lon3.toDouble() / 60.0)
                var lat4 = lat1Val.toDouble() + latMulti * (lat3.toDouble() / 60.0)
                lon4 *= lonMulti
                lat4 *= latMulti
                println("georef2DEG nums2 $lon4 $lat4")
                retVal.Latitude = lat4
                retVal.Longitude = lon4

            }
        }
        println("georef2DEG - RETURN $retVal")
        return retVal

    }

    //endregion

    //region GARS
    fun deg2GARS(coord: DEGData): String {
        println("deg2GARS coord $coord")
        var retVal = ""
        val lon1 = 180.0 + coord.Longitude
        val lon2 = ceil(lon1 * 2).toInt().toString().padStart(3, '0').take(3)

        println("deg2GARS lon2 $lon2")
        retVal += lon2 + garsLatBand(coord.Latitude) +
                garsQudrant(coord).toString() + garsNinth(coord).toString()
        return retVal
    }

    fun gars2DEG(coord: String): DEGData {
        println("gars2DEG coord $coord")
        val retVal = DEGData(0.0, 0.0)
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternGARS
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
            val lonMatch = matcher.group(1).toDouble()
            val lon = lonMatch / 2 - 180.0
            println("gars2DEG lon $lon")
            val letters01 = "ABCDEFGHJKLMNPQ"
            val letters02 = "ABCDEFGHJKLMNPQRSTUVWXYZ"
            val where01 = letters01.indexOf(matcher.group(2))
            println("gars2DEG where01 $where01")
            val where02 = letters02.indexOf(matcher.group(3))
            println("gars2DEG where02 $where02")
            val lat = (where01 * 24.0 + where02) / 2.0 - 90.0
            val valArr: Array<IntArray> = arrayOf(
                intArrayOf(0, 0),
                intArrayOf(1, 0),
                intArrayOf(1, 1),
                intArrayOf(0, 0),
                intArrayOf(0, 1)
            )
            val quadIndex = matcher.group(4).toInt()
            println("gars2DEG quadIndex $quadIndex")
            val tempArr = valArr[quadIndex]
            println("gars2DEG tempArr $tempArr")
            val extLat = tempArr[0]
            val extLon = tempArr[1]
            println("gars2DEG extLat $extLat extLon $extLon")
            val valArr2: Array<IntArray> = arrayOf(
                intArrayOf(0, 0),
                intArrayOf(2, 0),
                intArrayOf(2, 1),
                intArrayOf(2, 2),

                intArrayOf(1, 0),
                intArrayOf(1, 1),
                intArrayOf(1, 2),

                intArrayOf(0, 0),
                intArrayOf(0, 1),
                intArrayOf(0, 2),
            )
            val ninthIndex = matcher.group(5).toInt()
            val tempArr2 = valArr2[ninthIndex]
            val extLat2 = tempArr2[0]
            val extLon2 = tempArr2[1]
            println("gars2DEG extLat2 $extLat2 extLon2 $extLon2")
            retVal.Latitude = lat + extLat * 0.25 + extLat2 * 0.083333
            retVal.Longitude = lon + extLon * 0.25 + extLon2 * 0.083333
        }
        return retVal
    }


    //endregion

    fun getDistance(firstDEG: DEGData, secondDEG: DEGData): Double {
//        val firstDEG=DEGData(0.0,0.0)
//        val secondDEG=DEGData(0.0,0.0)
//        firstDEG.Latitude = 47.49119
//        firstDEG.Longitude = 19.11348
//        secondDEG.Latitude = 47.16352
//        secondDEG.Longitude = 17.96993
        println("getDistance $firstDEG")
        println("getDistance $secondDEG")
        // =ACOS(COS(RADIANS(90-$B$2)) * COS(RADIANS(90-D5)) + SIN(RADIANS(90-$B$2)) * SIN(RADIANS(90-D5)) * COS(RADIANS($C$2-E5))) * 6378137
        return acos(
            cos(deg2rad(90.0 - secondDEG.Latitude)) * cos(deg2rad(90.0 - firstDEG.Latitude)) +
                    sin(deg2rad(90.0 - secondDEG.Latitude)) * sin(deg2rad(90.0 - firstDEG.Latitude)) *
                    cos(deg2rad(secondDEG.Longitude - firstDEG.Longitude))
        ) * 6378137.0
    }

    //region Calculations

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
        val setColumn = floor(easting / 100000)
        println("get100kID - setColumn $setColumn")
        val setRow = floor(northing / 100000) % 20
        println("get100kID - setRow $setRow")
        val retVal = getLetter100kID(setColumn.roundToLong().toInt(), setRow.roundToLong().toInt(), setParm)
        println("get100kID - RETURN $retVal")
        return retVal
    }

    private fun get100kSet4Zone(zoneNumber: Double): Int {
        println("get100kSet4Zone - zoneNumber $zoneNumber")
        val num100kSets = 6
        var retVal = zoneNumber.roundToLong().toInt() % num100kSets
        if (retVal == 0) {
            retVal = num100kSets
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
        retVal = colInt.toChar().toString() + rowInt.toChar().toString()
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

    private fun rad2deg(number: Double): Double {
        return number * 180 / Math.PI
    }

    private fun deg2rad(number: Double): Double {
        return number * Math.PI / 180
    }

    private fun georefLon15(longitude: Double): String {
        println("getLon15 lonDeg $longitude")
        var tempDeg = abs(longitude.toInt())
        if (longitude >= 0) {
            tempDeg += 180
        } else {
            tempDeg = 180 - tempDeg
        }
        val retVal = georef15ID(tempDeg)
        println("getLon15 RETURN $retVal")
        return retVal
    }

    private fun georefLat15(latitude: Double): String {
        println("getLat15 latDeg $latitude")
        var tempDeg = abs(latitude.toInt())
        if (latitude >= 0) {
            tempDeg += 90
        } else {
            tempDeg = 90 - tempDeg
        }
        val retVal = georef15ID(tempDeg)
        println("getLat15 RETURN $retVal")
        return retVal
    }

    private fun georefLon1515(longitude: Double): String {
        println("getLon1515 lonDeg $longitude")
        val tempDeg = abs(longitude.toInt())
        var tempVal = tempDeg - ((tempDeg.toDouble() / 15).toInt() * 15)
        if (longitude < 0) {
            tempVal = 14 - tempVal
        }
        println("getLon1515 tempVal $tempVal")
        val retVal = georef1515ID(tempVal)
        println("getLon1515 RETURN $retVal")
        return retVal
    }

    private fun georefLat1515(latitude: Double): String {
        println("getLat1515 latDeg $latitude")
        val tempDeg = abs(latitude.toInt())
        var tempVal = tempDeg - ((tempDeg.toDouble() / 15).toInt() * 15)
        if (latitude < 0) {
            tempVal = 14 - tempVal
        }
        println("getLat1515 tempVal $tempVal")
        val retVal = georef1515ID(tempVal)
        println("getLat1515 RETURN $retVal")
        return retVal
    }

    private fun georef151560(coord: Double): String {
        var temp = abs(coord - coord.toInt()) * 60
        if (coord < 0) {
            temp = 60.0 - temp
        }
        return temp.toInt().toString().padStart(2, '0').take(2)
    }

    private fun georef15ID(deg: Int): String {
        val letters01 = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        val char01 = letters01.toCharArray()[deg / 15]
        return char01.toString()
    }

    private fun georef1515ID(index: Int): String {
        val letters01 = "ABCDEFGHJKLMNPQ"
        val char01 = letters01.toCharArray()[index]
        return char01.toString()
    }

    private fun georef1515IDVal(coord: String): Int {
        val letters: MutableMap<String, Int> = HashMap()
        letters["A"] = 0
        letters["B"] = 1
        letters["C"] = 2
        letters["D"] = 3
        letters["E"] = 4
        letters["F"] = 5
        letters["G"] = 6
        letters["H"] = 7
        letters["J"] = 8
        letters["K"] = 9
        letters["L"] = 10
        letters["M"] = 11
        letters["N"] = 12
        letters["P"] = 13
        letters["Q"] = 14
        return letters[coord]!!
    }

    private fun georef15IDVal(coord: String): Int {

        val letters: MutableMap<String, Int> = HashMap()
        letters["A"] = 0
        letters["B"] = 15
        letters["C"] = 30
        letters["D"] = 45
        letters["E"] = 60
        letters["F"] = 75
        letters["G"] = 90
        letters["H"] = 105
        letters["J"] = 120
        letters["K"] = 135
        letters["L"] = 150
        letters["M"] = 165
        letters["N"] = 180
        letters["P"] = 195
        letters["Q"] = 210
        letters["R"] = 225
        letters["S"] = 240
        letters["T"] = 255
        letters["U"] = 270
        letters["V"] = 285
        letters["W"] = 300
        letters["X"] = 315
        letters["Y"] = 330
        letters["Z"] = 345
        return letters[coord]!!
    }

    private fun garsLatBand(lat: Double): String {
        println("garsLatBand lat $lat")
        val retVal: String
        val tempLatMin = (90.0 + lat) * 2
        println("garsLatBand tempLatMin $tempLatMin")

        val letters01 = "ABCDEFGHJKLMNPQ"
        val letters02 = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        val index01 = (tempLatMin / 24.0).toInt()
        println("garsLatBand index01 $index01")
        val char01 = letters01.toCharArray()[index01]
        println("garsLatBand char01 $char01")
        val index02 = (tempLatMin - (index01 * 24)).toInt()
        println("garsLatBand index02 $index02")
        val char02 = letters02.toCharArray()[index02]
        println("garsLatBand char02 $char02")
        retVal = char01.toString() + char02.toString()
        println("garsLatBand RETURN $retVal")
        return retVal
    }

    private fun garsQudrant(coord: DEGData): Int {
        println("garsQudrant coord $coord")
        val retVal: Int
        val tempLatMin = (90.0 + coord.Latitude) * 2
        val tempLonMin = (180.0 + coord.Longitude) * 2
        println("garsQudrant tempLatMin $tempLatMin tempLonMin $tempLonMin")
        val quadLat = ((tempLatMin - tempLatMin.toInt()) * 2).toInt()
        val quadLon = ((tempLonMin - tempLonMin.toInt()) * 2).toInt()
        println("garsQudrant quadLat $quadLat quadLon $quadLon")
        val valArr: Array<IntArray> = arrayOf(
            intArrayOf(3, 4),
            intArrayOf(1, 2)
        )
        retVal = valArr[quadLat][quadLon]
        println("garsQudrant RETURN $retVal")
        return retVal
    }

    private fun garsNinth(coord: DEGData): Int {
        val retVal: Int
        val tempLatMin = (90.0 + coord.Latitude) * 2
        val tempLonMin = (180.0 + coord.Longitude) * 2
        val valArr: Array<IntArray> = arrayOf(
            intArrayOf(7, 8, 9, 7, 8, 9),
            intArrayOf(4, 5, 6, 4, 5, 6),
            intArrayOf(1, 2, 3, 1, 2, 3),
            intArrayOf(7, 8, 9, 7, 8, 9),
            intArrayOf(4, 5, 6, 4, 5, 6),
            intArrayOf(1, 2, 3, 1, 2, 3),
        )
        val quadLat = ((tempLatMin - tempLatMin.toInt()) * 6).toInt()
        val quadLon = ((tempLonMin - tempLonMin.toInt()) * 6).toInt()
        retVal = valArr[quadLat][quadLon]
        println("garsNinth RETURN $retVal")
        return retVal
    }

    //endregion

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