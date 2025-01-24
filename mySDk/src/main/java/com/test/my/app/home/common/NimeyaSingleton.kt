package com.test.my.app.home.common

import com.test.my.app.common.utils.Utilities
import com.test.my.app.model.nimeya.GetProtectoMeterHistoryModel
import com.test.my.app.model.nimeya.GetRetiroMeterHistoryModel
import com.test.my.app.model.nimeya.GetRiskoMeterHistoryModel
import com.test.my.app.model.nimeya.SaveProtectoMeterModel
import com.test.my.app.model.nimeya.SaveRetiroMeterModel
import com.test.my.app.model.nimeya.SaveRiskoMeterModel


class NimeyaSingleton private constructor() {

    var saveRiskoMeter = SaveRiskoMeterModel.SaveRiskoMeter()
    var saveProtectoMeter = SaveProtectoMeterModel.SaveProtectoMeter()
    var saveRetiroMeter = SaveRetiroMeterModel.SaveRetiroMeter()

    var riskoMeterHistory = GetRiskoMeterHistoryModel.GetRiskoMeterHistory()
    var protectoMeterHistory = GetProtectoMeterHistoryModel.GetProtectoMeterHistory()
    var retiroMeterHistory = GetRetiroMeterHistoryModel.GetRetiroMeterHistory()

    fun clearData() {
        instance = null
        saveRiskoMeter = SaveRiskoMeterModel.SaveRiskoMeter()
        saveProtectoMeter = SaveProtectoMeterModel.SaveProtectoMeter()
        saveRetiroMeter = SaveRetiroMeterModel.SaveRetiroMeter()
        riskoMeterHistory = GetRiskoMeterHistoryModel.GetRiskoMeterHistory()
        protectoMeterHistory = GetProtectoMeterHistoryModel.GetProtectoMeterHistory()
        retiroMeterHistory = GetRetiroMeterHistoryModel.GetRetiroMeterHistory()
        Utilities.printLogError("Cleared Nimeya Data")
    }

    companion object {
        private var instance: NimeyaSingleton? = null
        fun getInstance(): NimeyaSingleton? {
            if (instance == null) {
                instance = NimeyaSingleton()
            }
            return instance
        }
    }

}