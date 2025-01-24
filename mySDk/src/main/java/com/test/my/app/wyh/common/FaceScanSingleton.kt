package com.test.my.app.wyh.common

import com.test.my.app.common.utils.Utilities
import com.test.my.app.home.common.DataHandler.FaceScanDataModel

class FaceScanSingleton private constructor() {

    var dateTime = ""
    var faceScanData : MutableList<FaceScanDataModel> = mutableListOf()

    fun clearData() {
        instance = null
        faceScanData.clear()
        Utilities.printLogError("Cleared FaceScan Data")
    }

    companion object {
        private var instance: FaceScanSingleton? = null
        fun getInstance(): FaceScanSingleton? {
            if (instance == null) {
                instance = FaceScanSingleton()
            }
            return instance
        }
    }

}