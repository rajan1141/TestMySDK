package com.test.my.app.tools_calculators.model

import android.util.ArrayMap
import com.test.my.app.common.utils.Utilities
import com.test.my.app.model.toolscalculators.UserInfoModel

class CalculatorDataSingleton private constructor() {

    var quizId = ""
    var participantID = ""
    var templateId = ""
    var smartPhoneScore = "0"
    var riskLabel = ""
    var riskScorePercentage = ""
    var heartAge = ""

    var personAge = "0"
    var userPreferences = UserInfoModel()
    var heartAgeModel = "BMI"

    var healthConditionSelection = ArrayList<String>()

    var heartAgeSummery = HeartAgeSummeryModel()
    val heartAgeSummeryList: ArrayList<HeartAgeSummeryModel> = ArrayList()
    var diabetesSummeryModel = DiabetesSummeryModel()
    var hypertensionSummery = HypertensionSummeryModel()
    var stressSummeryData = StressCalculatorSummeryModel()
    var smartPhoneSummeryData = SmartPhoneSummeryModel()

    var answerArrayMap = ArrayMap<String, Answer>()
    var lmpDate: String? = null

    fun clearData() {
        instance = null
        Utilities.printLogError("Cleared ToolsTracker Data")
    }

    companion object {
        private var instance: CalculatorDataSingleton? = null
        fun getInstance(): CalculatorDataSingleton? {
            if (instance == null) {
                instance = CalculatorDataSingleton()
            }
            return instance
        }
    }
}