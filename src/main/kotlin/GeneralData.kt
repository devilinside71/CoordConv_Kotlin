import java.util.regex.Pattern

/**
 * Created on 2022-07-18
 * @author Laszlo TAMAS9
 */
class GeneralData {


    companion object {
        const val degChar = "°"
        const val minChar = "'"
        const val secChar = "\""
        val emptyDEGData = DEGData(0.0, 0.0)
        val emptyUTMData = UTMData(0, "", 0, 0)
        val emptyDMSData = DMSData(0, 0, 0.0, "X", 0, 0, 0.0, "X")
        val emptySingleDMSNoHemisphereData = SingleDMSNoHemisphereData(0, 0, 0.0)
        val emptySingleDMSData = SingleDMSData(0, 0, 0.0, "")
        val emptyMGRSData = MGRSData(0, "X", "X", "X", 0, 0, 0)
        const val naStr = "N/A"

        // Coordinate patterns
        // MGRS > 30QWD0143947225 > 1:30, 2:Q, 3:W, 4:D, 5:0143947225
        var patternMGRS: Pattern =
            Pattern.compile("^([0-9]+)([CDEFGHJKLMNPQRSTUVWX])([ABCDEFGHJKLMNPQRSTUVWXYZ])([ABCDEFGHJKLMNPQRSTUV])([0-9]{0,10})$")

        // UTM > 42,S,3822196,528173 > 1:42, 2:S, 3:3822196, 4:528173
        var patternUTM: Pattern = Pattern.compile("^([0-9]+),([CDEFGHJKLMNPQRSTUVWX]),([0-9]+),([0-9]+)$")

        // DEG > 47.491195,-19.113478 > 1:47.491195, 2:-19.113478
        var patternDEG: Pattern = Pattern.compile("^([+-]?[0-9]*\\.[0-9]+),([+-]?[0-9]*\\.[0-9]+)$")

        // SingleDEG > -19.113478 > 1:47.491195, 2:-19.113478
        var patternSingleDEG: Pattern = Pattern.compile("^([+-]?[0-9]*\\.[0-9]+)$")

        // SingleDMSNoHemisphere > 34°32'27.91" > 1:34, 2:32, 3:27.91
        var patternSingleDMSNoHemisphere: Pattern = Pattern.compile("^([0-9]+)°([0-9]{1,2})\'(([0-9]*[.])?[0-9]+)\"$")

        // SingleDMSLatitude > 47°29'28.302"N > 1:47, 2:29, 3:28.302, 5:N
        var patternSingleDMSLatitude: Pattern = Pattern.compile("^([0-9]+)°([0-9]{1,2})\'(([0-9]*[.])?[0-9]+)\"([NS])$")

        // SingleDMSLongitude > 47°29'28.302"W > 1:47, 2:29, 3:28.302, 5:W
        var patternSingleDMSLongitude: Pattern = Pattern.compile("^([0-9]+)°([0-9]{1,2})\'(([0-9]*[.])?[0-9]+)\"([EW])$")
        var patternSingleDMS: Pattern = Pattern.compile("^([0-9]+)°([0-9]{1,2})\'(([0-9]*[.])?[0-9]+)\"([EWNS])$")

        // DMS > 47°29'28.302"N,19°6'48.521"E > 1:47, 2:29, 3:28.302, 5:N, 6:19, 7:6, 8: 48.521, 10:E
        var patternDMS: Pattern =
            Pattern.compile("^([0-9]+)°([0-9]{1,2})\'(([0-9]*[.])?[0-9]+)\"([NS]),([0-9]+)°([0-9]{1,2})\'(([0-9]*[.])?[0-9]+)\"([EW])$")

        var patternGEOREF: Pattern =
            Pattern.compile("^([ABCDEFGHJKLMNPQRSTUVWXYZ])([ABCDEFGHJKLM])([ABCDEFGHJKLMNPQ])([ABCDEFGHJKLMNPQ])([0-9]{4,10})$")

        var patternGARS: Pattern = Pattern.compile("^([0-9]{3})([ABCDEFGHJKLMNPQ])([ABCDEFGHJKLMNPQRSTUVWXYZ])([1-4])([0-9])$")

    }
}