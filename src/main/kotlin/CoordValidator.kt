/**
 * Created on 2022-07-18
 * @author Laszlo TAMAS9
 */
class CoordValidator {

    fun validMGRSString(coord: String): Boolean {
        var retVal = false
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternMGRS
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
            if (matcher.group(5).length % 2 == 0) {
                retVal = true
            }
        }
        return retVal
    }

    fun validUTMString(coord: String): Boolean {
        var retVal = false
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
            retVal = true
        }
        return retVal
    }

    fun validDEGString(coord: String): Boolean {
        var retVal = false
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternDEG
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
            if (valD1 <= 90.0 && valD1 >= -90.0 && valD2 <= 180.0 && valD2 >= -180.0) {
                retVal = true
            }
        }
        return retVal
    }

    fun validSingleDegString(coord: String): Boolean {
        var retVal = false
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
                retVal = true
            }
        }
        return retVal
    }

    fun validSingleDegStringLat(coord: String): Boolean {
        var retVal = false
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
            if (valD1 <= 90.0 && valD1 >= -90.0) {
                retVal = true
            }
        }
        return retVal
    }

    fun validSingleDEGStringLon(coord: String): Boolean {
        return validSingleDegString(coord)
    }

    fun validSingleDMSStringLat(coord: String): Boolean {
        var retVal = false
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternSingleDMSLatitude
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
            if (valD1 <= 90.0 && valD1 >= -90.0 && valD2 >= 0.0 && valD2 <= 60.0 && valD3 >= 0.0 && valD3 <= 60.0) {
                retVal = true
            }
        }
        return retVal
    }


    fun validSingleDMSStringLon(coord: String): Boolean {
        var retVal = false
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternSingleDMSLongitude
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
            if (valD1 <= 180.0 && valD1 >= -180.0 && valD2 >= 0.0 && valD2 <= 60.0 && valD3 >= 0.0 && valD3 <= 60.0) {
                retVal = true
            }
        }
        return retVal
    }

    fun validSingleDMSStringNoHemisphere(coord: String): Boolean {
        var retVal = false
        val tempStr = coord.replace("\\s+".toRegex(), "")
        val pattern = GeneralData.patternSingleDMSNoHemisphere
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
            if (valD1 <= 180.0 && valD1 >= -180.0 && valD2 >= 0.0 && valD2 <= 60.0 && valD3 >= 0.0 && valD3 <= 60.0) {
                retVal = true
            }
        }
        return retVal
    }

    fun validDMSString(coord: String): Boolean {
        var retVal = false
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
                retVal = true
            }
        }
        return retVal
    }

    fun recognizedCoord(coord: String): CoordType {
        var retVal = CoordType.Unknown
        val tempStr = coord.replace("\\s+".toRegex(), "")
        println("String to recognize: $tempStr")
        if (validMGRSString(tempStr)){
            retVal=CoordType.MGRS
        }
        if (validUTMString(tempStr)){
            retVal=CoordType.UTM
        }
        if (validDEGString(tempStr)){
            retVal=CoordType.DEG
        }
        if (validSingleDegString(tempStr)){
            retVal=CoordType.SingleDEG
        }
        if (validDMSString(tempStr)){
            retVal=CoordType.DMS
        }
        if (validSingleDMSStringLat(tempStr)){
            retVal=CoordType.SingleDMSLatitude
        }
        if (validSingleDMSStringLon(tempStr)){
            retVal=CoordType.SingleDMSLongitude
        }
        if (validSingleDMSStringNoHemisphere(tempStr)){
            retVal=CoordType.SingleDMSNoHemisphere
        }
        return retVal
    }

}
