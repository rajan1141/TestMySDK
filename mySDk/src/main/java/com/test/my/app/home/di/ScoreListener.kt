package com.test.my.app.home.di

import com.test.my.app.model.entity.HRASummary
import com.test.my.app.model.entity.TrackParameterMaster

interface ScoreListener {
    fun onScore(hraSummary: HRASummary?)
    fun onVitalDataUpdateListener(history: List<TrackParameterMaster.History>)
}