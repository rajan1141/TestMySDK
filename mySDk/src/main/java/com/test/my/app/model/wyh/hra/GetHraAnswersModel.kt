package com.test.my.app.model.wyh.hra

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetHraAnswersModel {

    data class GetHraAnswersRequest(
        @SerializedName("isComplete")
        @Expose
        val isComplete: Int? = 0,
        @SerializedName("integrationid")
        @Expose
        val integrationId: String? = "",
        @SerializedName("surveyVersion")
        @Expose
        val surveyVersion: String? = "",
    )

    data class GetHraAnswersResponse(
        @SerializedName("success")
        @Expose
        val success: Boolean? = false,
        @SerializedName("msg")
        @Expose
        val msg: String? = "",
        @SerializedName("isUnderMaintenance")
        @Expose
        val isUnderMaintenance: Boolean? = false,
        @SerializedName("data")
        @Expose
        val data: Data = Data(),

        @SerializedName("appversion")
        @Expose
        val appversion: String? = "",
        @SerializedName("isCloud")
        @Expose
        val isCloud: Boolean? = false,
        @SerializedName("rewards")
        @Expose
        val rewards: Rewards = Rewards()
    )

    data class Data(
        @SerializedName("answerJson")
        @Expose
        val questionsList: String? = "",
        @SerializedName("clientId")
        @Expose
        val clientId: Int? = 0,
        @SerializedName("customerId")
        @Expose
        val customerId: String? = "",
        @SerializedName("integrationid")
        @Expose
        val integrationid: String? = "",
        @SerializedName("conversationId")
        @Expose
        val conversationId: String? = "",
        @SerializedName("surveyVersion")
        @Expose
        val surveyVersion: String? = "",
        @SerializedName("status")
        @Expose
        val status: String? = "",
        @SerializedName("createdDate")
        @Expose
        val createdDate: String? = "",
        @SerializedName("modifiedDate")
        @Expose
        val modifiedDate: String? = ""
    )

    data class WyhHraQuestions(
        @Expose
        val questionsList: List<Question> = listOf()
    )

    data class Question(
        @SerializedName("QuestionId")
        @Expose
        val questionId: String? = "",
        @SerializedName("QuestionType")
        @Expose
        val questionType: String? = "",
        @SerializedName("Question")
        @Expose
        val question: String? = "",
        @SerializedName("Options")
        @Expose
        val options: List<String> = listOf(),
        @SerializedName("Answer")
        @Expose
        val answer: List<Any> = listOf(),

        @SerializedName("IsSkipped")
        @Expose
        val isSkipped: Boolean? = false,
        @SerializedName("IsVisible")
        @Expose
        val isVisible: Boolean? = false
    )

    data class Rewards(
        @SerializedName("bonusRewards")
        @Expose
        val bonusRewards: Any? = Any(),
        @SerializedName("reward")
        @Expose
        val reward: Any? = Any()
    )

}