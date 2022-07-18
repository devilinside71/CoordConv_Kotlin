/*
 * Copyright (c) 2022. Laszlo TAMAS9
 * National Operations Management Division
 * HM Electronics, Logistics and Property Management Private Co.
 */
/**
 * Created on 2022-07-17
 *
 * @author Laszlo TAMAS9
 */
data class DMSData(
    var LatDeg: Int,
    var LatMin: Int,
    var LatSec: Double,
    var LatHemisphere: String,
    var LonDeg: Int,
    var LonMin: Int,
    var LonSec: Double,
    var LonHemisphere: String
)