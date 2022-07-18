import kotlin.math.sin
import kotlin.math.tan

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

}