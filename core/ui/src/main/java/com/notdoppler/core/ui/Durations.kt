package com.notdoppler.core.ui

sealed class Durations(val time: Int) {
    /**
     * 100
     */
    object ShortLight : Durations(100)


    /**
     * 200
     */
    object Short : Durations(200)

    /**
     * 350
     */
    object ShortMedium : Durations(350)

    /**
     * 500
     */
    object Medium : Durations(500)

    /**
     * 650
     */
    object MediumLong : Durations(650)

    /**
     * 800
     */
    object Long : Durations(800)

    /**
     * 1000
     */
    object Second : Durations(1000)
}

