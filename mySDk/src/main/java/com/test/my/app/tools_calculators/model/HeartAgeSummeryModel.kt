package com.test.my.app.tools_calculators.model

class HeartAgeSummeryModel {
    var heartAge = ""
    var heartRisk = ""
    var riskLabel = ""
    var heartAgeReport: HeartAgeReport = HeartAgeReport()
    var parameterReport: ArrayList<HeartAgeReport> = arrayListOf()
    var heartRiskReport: ArrayList<HeartAgeReport> = arrayListOf()

    /*    fun getHeartAgeReport(): HeartAgeReport? {
            return heartAgeReport
        }

        fun setHeartAgeReport(heartAgeReport: HeartAgeReport?) {
            this.heartAgeReport = heartAgeReport
        }

        fun getParameterReport(): ArrayList<HeartAgeReport>? {
            return parameterReport
        }

        fun setParameterReport(parameterReport: ArrayList<HeartAgeReport>?) {
            this.parameterReport = parameterReport
        }

        fun getHeartRiskReport(): ArrayList<HeartAgeReport>? {
            return heartRiskReport
        }

        fun setHeartRiskReport(heartRiskReport: ArrayList<HeartAgeReport>?) {
            this.heartRiskReport = heartRiskReport
        }*/

}