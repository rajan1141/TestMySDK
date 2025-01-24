package com.test.my.app.wyh.common

import com.test.my.app.common.utils.Utilities
import com.test.my.app.model.hra.Option

class IraSingleton private constructor() {

    var dateTime = ""
    var faceScanData : MutableList<Option> = mutableListOf()

    fun clearData() {
        instance = null
        faceScanData.clear()
        Utilities.printLogError("Cleared IRA Data")
    }

    companion object {
        private var instance: IraSingleton? = null
        fun getInstance(): IraSingleton? {
            if (instance == null) {
                instance = IraSingleton()
            }
            return instance
        }
    }

}