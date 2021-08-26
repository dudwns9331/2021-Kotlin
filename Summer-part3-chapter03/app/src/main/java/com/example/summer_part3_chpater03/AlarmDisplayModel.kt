package com.example.summer_part3_chpater03

/**
 * 알람에 대한 정보
 * TextView 에 보여지는 데이터 정보들
 * 시, 분, on/off 에 대한 정보를 저장한다.
 */
data class AlarmDisplayModel(
    val hour: Int,
    val minute: Int,
    var onOff: Boolean
) {
    val timeText: String
        get() {
            val h = "%02d".format(if (hour < 12) hour else hour - 12)
            val m = "%02d".format(minute)

            return "$h:$m"
        }
    val ampmText: String
        get() {
            return if (hour < 12) "AM" else "PM"
        }

    val onOffText: String
        get() {
            return if (onOff) "알람 끄기" else "알람 켜기"
        }

    fun makeDataForDB(): String? {
        return "$hour:$minute"
    }
}
