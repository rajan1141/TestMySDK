package com.test.my.app.home.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.view.View
import androidx.core.content.ContextCompat
//import com.aktivolabs.aktivocore.data.models.challenge.Challenge
import com.test.my.app.R
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.LocaleHelper
import com.test.my.app.common.view.SpinnerModel
import com.test.my.app.model.sudLifePolicy.PolicyProductsModel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.Locale

class DataHandler(val context: Context) {

    object NavDrawer {
        const val HOME = "HOME"
        const val LINK = "Link"
        const val SPREAD_THE_WORD = "SPREAD_THE_WORD"
        const val SETTINGS = "SETTINGS"
    }

    object ProfileImgOption {
        const val View = "View"
        const val Avatar = "Avatar"
        const val Gallery = "Gallery"
        const val Photo = "Photo"
        const val Remove = "Remove"
    }

    fun getNavDrawerList(): List<NavDrawerOption> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<NavDrawerOption> = ArrayList()
        list.add(NavDrawerOption(R.drawable.img_drawer_home, localResource.getString(R.string.HOME), NavDrawer.HOME, ContextCompat.getColor(context, R.color.transparent)))
        list.add(NavDrawerOption(R.drawable.ic_settings, localResource.getString(R.string.SETTINGS), NavDrawer.SETTINGS, ContextCompat.getColor(context, R.color.transparent)))
        return list
    }

    fun getSwitchProfileNavDrawerList(): List<NavDrawerOption> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<NavDrawerOption> = ArrayList()
        list.add(NavDrawerOption(R.drawable.img_drawer_home, localResource.getString(R.string.HOME), NavDrawer.HOME, ContextCompat.getColor(context, R.color.transparent)))
        list.add(NavDrawerOption(R.drawable.ic_settings, localResource.getString(R.string.SETTINGS), NavDrawer.SETTINGS, ContextCompat.getColor(context, R.color.transparent)))
        return list
    }

    fun getSettingsOptionListData(): List<Option> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<Option> = ArrayList()
        //list.add(Option(R.drawable.img_setting_language, localResource.getString(R.string.LANGUAGE), ContextCompat.getColor(context, R.color.transparent),Constants.LANGUAGE))
        //list.add(Option(R.drawable.img_setting_change_password, localResource.getString(R.string.CHANGE_PASSWORD), ContextCompat.getColor(context, R.color.transparent),Constants.CHANGE_PASSWORD))
        list.add(Option(R.drawable.img_delete_account, localResource.getString(R.string.DELETE_ACCOUNT), ContextCompat.getColor(context, R.color.transparent),Constants.DELETE_ACCOUNT))
        //list.add(Option(R.drawable.img_drawer_feedback, localResource.getString(R.string.FEEDBACK), ContextCompat.getColor(context, R.color.transparent), "FEEDBACK"))
        //list.add(Option(R.drawable.img_drawer_rate_us, localResource.getString(R.string.RATE_US), ContextCompat.getColor(context, R.color.transparent), "RATE_US"))
        return list
    }

    fun getSwitchProfileSettingsOptionListData(): List<Option> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<Option> = ArrayList()
        //list.add(Option(R.drawable.img_setting_language, localResource.getString(R.string.LANGUAGE), ContextCompat.getColor(context, R.color.transparent),Constants.LANGUAGE))
        //list.add(Option(R.drawable.img_setting_change_password, localResource.getString(R.string.CHANGE_PASSWORD), ContextCompat.getColor(context, R.color.transparent),Constants.CHANGE_PASSWORD))
        list.add(Option(R.drawable.img_delete_account, localResource.getString(R.string.DELETE_ACCOUNT), ContextCompat.getColor(context, R.color.transparent),Constants.DELETE_ACCOUNT))
        //list.add(Option(R.drawable.img_drawer_rate_us, localResource.getString(R.string.RATE_US), ContextCompat.getColor(context, R.color.transparent), "RATE_US"))
        //list.add(Option(R.drawable.img_setting_language, localResource.getString(R.string.LANGUAGE), ContextCompat.getColor(context, R.color.transparent), "LANGUAGE"))
        return list
    }

    fun getFamilyRelationListMale(): List<FamilyRelationOption> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<FamilyRelationOption> = ArrayList()
        list.add(FamilyRelationOption(R.drawable.img_father, localResource.getString(R.string.FATHER), "FAT", context.resources.getString(R.string.MALE)))
        list.add(FamilyRelationOption(R.drawable.img_mother, localResource.getString(R.string.MOTHER), "MOT", context.resources.getString(R.string.FEMALE)))
        list.add(FamilyRelationOption(R.drawable.img_son, localResource.getString(R.string.SON), "SON", context.resources.getString(R.string.MALE)))
        list.add(FamilyRelationOption(R.drawable.img_daughter, localResource.getString(R.string.DAUGHTER), "DAU", context.resources.getString(R.string.FEMALE)))
        list.add(FamilyRelationOption(R.drawable.img_brother,localResource.getString(R.string.BROTHER), "BRO", context.resources.getString(R.string.MALE)))
        list.add(FamilyRelationOption(R.drawable.img_sister, localResource.getString(R.string.SISTER), "SIS", context.resources.getString(R.string.FEMALE)))
        list.add(FamilyRelationOption(R.drawable.img_gf, localResource.getString(R.string.GRAND_FATHER), "GRF", context.resources.getString(R.string.MALE)))
        list.add(FamilyRelationOption(R.drawable.img_gm, localResource.getString(R.string.GRAND_MOTHER), "GRM", context.resources.getString(R.string.FEMALE)))
        list.add(FamilyRelationOption(R.drawable.img_wife, localResource.getString(R.string.WIFE), "WIF", context.resources.getString(R.string.FEMALE)))
        return list
    }

    fun getFamilyRelationListFemale(): List<FamilyRelationOption> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<FamilyRelationOption> = ArrayList()
        list.add(FamilyRelationOption(R.drawable.img_father, localResource.getString(R.string.FATHER), "FAT", context.resources.getString(R.string.MALE)))
        list.add(FamilyRelationOption(R.drawable.img_mother, localResource.getString(R.string.MOTHER), "MOT", context.resources.getString(R.string.FEMALE)))
        list.add(FamilyRelationOption(R.drawable.img_son, localResource.getString(R.string.SON), "SON", context.resources.getString(R.string.MALE)))
        list.add(FamilyRelationOption(R.drawable.img_daughter, localResource.getString(R.string.DAUGHTER), "DAU", context.resources.getString(R.string.FEMALE)))
        list.add(FamilyRelationOption(R.drawable.img_brother,localResource.getString(R.string.BROTHER), "BRO", context.resources.getString(R.string.MALE)))
        list.add(FamilyRelationOption(R.drawable.img_sister, localResource.getString(R.string.SISTER), "SIS", context.resources.getString(R.string.FEMALE)))
        list.add(FamilyRelationOption(R.drawable.img_gf, localResource.getString(R.string.GRAND_FATHER), "GRF", context.resources.getString(R.string.MALE)))
        list.add(FamilyRelationOption(R.drawable.img_gm, localResource.getString(R.string.GRAND_MOTHER), "GRM", context.resources.getString(R.string.FEMALE)))
        list.add(FamilyRelationOption(R.drawable.img_husband, localResource.getString(R.string.HUSBAND), "HUS", context.resources.getString(R.string.MALE)))
        return list
    }

    fun getSmitFitFeatures(): List<SmitFitModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<SmitFitModel> = ArrayList()
        list.add(SmitFitModel(Constants.MEDITATION,localResource.getString(R.string.MEDITATION),R.drawable.img_meditation,R.color.color_meditation))
        list.add(SmitFitModel(Constants.YOGA,localResource.getString(R.string.YOGA),R.drawable.img_yoga,R.color.color_yoga))
        list.add(SmitFitModel(Constants.EXERCISE,localResource.getString(R.string.EXERCISE),R.drawable.img_exercise,R.color.color_exercise))
        list.add(SmitFitModel(Constants.HEALTHY_BITES,localResource.getString(R.string.HEALTHY_BITES),R.drawable.img_healthy_bites,R.color.color_yoga))
        list.add(SmitFitModel(Constants.WOMEN_HEALTH,localResource.getString(R.string.WOMENS_HEALTH),R.drawable.img_women_health,R.color.color_meditation))
        return list
    }

    fun getNimeyaCalculators(): List<FinancialCalculatorModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<FinancialCalculatorModel> = ArrayList()
        list.add(FinancialCalculatorModel(Constants.NIMEYA_RISKO_METER,localResource.getString(R.string.RISKO_METER),R.drawable.img_riskometer,R.color.color_exercise,localResource.getString(R.string.RISKO_METER_DESC)))
        list.add(FinancialCalculatorModel(Constants.NIMEYA_PROTECTO_METER,localResource.getString(R.string.PROTECTO_METER),R.drawable.img_protectometer,R.color.color_exercise,localResource.getString(R.string.PROTECTO_METER_DESC)))
        list.add(FinancialCalculatorModel(Constants.NIMEYA_RETIRO_METER,localResource.getString(R.string.RETIRO_METER),R.drawable.img_retirometer,R.color.color_exercise,localResource.getString(R.string.RETIRO_METER_DESC)))
        return list
    }

    fun getFinancialCalculators(): List<FinancialCalculatorModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<FinancialCalculatorModel> = ArrayList()
        list.add(FinancialCalculatorModel(Constants.EDUCATION,localResource.getString(R.string.EDUCATION),R.drawable.img_education,R.color.color_meditation))
        list.add(FinancialCalculatorModel(Constants.MARRIAGE,localResource.getString(R.string.MARRIAGE),R.drawable.img_marriage,R.color.color_yoga))
        list.add(FinancialCalculatorModel(Constants.WEALTH,localResource.getString(R.string.WEALTH),R.drawable.img_wealth,R.color.color_exercise))
        list.add(FinancialCalculatorModel(Constants.HOUSE,localResource.getString(R.string.HOME2),R.drawable.img_house,R.color.color_house))
        list.add(FinancialCalculatorModel(Constants.RETIREMENT,localResource.getString(R.string.RETIREMENT),R.drawable.img_retirement,R.color.color_retirement))
        list.add(FinancialCalculatorModel(Constants.HUMAN_VALUE,localResource.getString(R.string.HUMAN_VALUE),R.drawable.img_human_value,R.color.color_human))
        return list
    }

    fun getPolicyDownloadsList(): MutableList<SudPolicyDownloadModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: MutableList<SudPolicyDownloadModel> = ArrayList()
        list.add(SudPolicyDownloadModel(Constants.POLICY_PMJJBY,localResource.getString(R.string.PMJJBY_TITLE),localResource.getString(R.string.PMJJBY_DESC)))
        list.add(SudPolicyDownloadModel(Constants.POLICY_CREDIT_LIFE,localResource.getString(R.string.CREDIT_LIFE_TITLE),localResource.getString(R.string.CREDIT_LIFE_DESC)))
        list.add(SudPolicyDownloadModel(Constants.POLICY_GROUP_COI,localResource.getString(R.string.GROUP_COI_TITLE),localResource.getString(R.string.GROUP_COI_DESC)))
        return list
    }

    fun getPolicyOptionsList(context: Context): MutableList<Option> {
        val localResource = LocaleHelper.getLocalizedResources(context,Locale(LocaleHelper.getLanguage(context)))!!
        val list: MutableList<Option> = ArrayList()
        list.add(Option(R.drawable.img_sudlife_logo,localResource.getString(R.string.WEBSITE_BOT),R.color.vivantOrange,Constants.OPTION_WEBSITE_BOT))
        //list.add(Option(R.drawable.ic_sud_logo,localResource.getString(R.string.NETRA_CHAT_BOT),R.color.red,Constants.OPTION_NETRA_BOT))
        list.add(Option(R.drawable.img_whats_app,localResource.getString(R.string.WHATSAPP_CHATBOT),R.color.vivantGreen,Constants.OPTION_WHATS_APP_BOT))
        list.add(Option(R.drawable.ic_call,localResource.getString(R.string.CALL_CUSTOMER_CARE),R.color.blue,Constants.OPTION_CALL_CENTRE))
        return list
    }

/*    fun getSudBannersPolicySectionList(): MutableList<SudPolicyBannerModel> {
        //val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: MutableList<SudPolicyBannerModel> = mutableListOf()
        //val smartHealthUrl = Utilities.getSmartHealthRedirectionUrlBasedOnEmployeeType(Utilities.getEmployeeType())
        //list.add(SudPolicyBannerModel(Constants.SMART_HEALTH_PRODUCT,"Smart Health Product",smartHealthUrl,R.drawable.banner_smart_healthcare))
        list.add(SudPolicyBannerModel(Constants.CENTURY_ROYALE,"Century Royale",Constants.CENTURY_ROYALE_URL,R.drawable.banner_century_royale))
        list.add(SudPolicyBannerModel(Constants.CENTURY_GOLD,"Century Gold",Constants.CENTURY_GOLD_URL,R.drawable.banner_century_gold))
        list.add(SudPolicyBannerModel(Constants.PROTECT_SHIELD_PLUS,"Protect Shield Plus",Constants.PROTECT_SHIELD_PLUS_URL,R.drawable.banner_protect_shield_plus))
        return list
    }*/

    fun getWellfieStatusMsg(code: String,apiMsg: String): String {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val msg = when(code) {
            "CALIBRATING" -> localResource.getString(R.string.STATUS_CALIBRATING)
            "NOISE_DURING_EXECUTION" -> localResource.getString(R.string.STATUS_NOISE_DURING_EXECUTION)
            "RECALIBRATING" -> localResource.getString(R.string.STATUS_RECALIBRATING)
            "NO_FACE" -> localResource.getString(R.string.STATUS_NO_FACE)
            "FACE_LOST" -> localResource.getString(R.string.STATUS_FACE_LOST)
            "BRIGHT_LIGHT_ISSUE" -> localResource.getString(R.string.STATUS_BRIGHT_LIGHT_ISSUE)
            "UNKNOWN" -> localResource.getString(R.string.STATUS_UNKNOWN)
            "SUCCESS" -> localResource.getString(R.string.STATUS_SUCCESS)
            "MOVING_WARNING" -> localResource.getString(R.string.STATUS_MOVING_WARNING)
            //"UNSTABLE_CONDITIONS_WARNING" -> localResource.getString(R.string.STATUS_UNSTABLE_CONDITIONS_WARNING)
            //"INTERFERENCE_WARNING" -> localResource.getString(R.string.STATUS_INTERFERENCE_WARNING)
            else -> apiMsg
        }
        return msg
    }

    /*    fun getWellfieStatusMsg(code: String,apiMsg: String): String {
            val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
            val msg = when(code) {
                "SUCCESS" -> localResource.getString(R.string.STATUS_SUCCESS)
                "NO_FACE" -> localResource.getString(R.string.STATUS_NO_FACE)
                "FACE_LOST" -> localResource.getString(R.string.STATUS_FACE_LOST)
                "CALIBRATING" -> localResource.getString(R.string.STATUS_CALIBRATING)
                "RECALIBRATING" -> localResource.getString(R.string.STATUS_RECALIBRATING)
                "BRIGHT_LIGHT_ISSUE" -> localResource.getString(R.string.STATUS_BRIGHT_LIGHT_ISSUE)
                "NOISE_DURING_EXECUTION" -> localResource.getString(R.string.STATUS_NOISE_DURING_EXECUTION)

                "MOVING_WARNING" -> localResource.getString(R.string.STATUS_MOVING_WARNING)
                "UNSTABLE_CONDITIONS_WARNING" -> localResource.getString(R.string.STATUS_UNSTABLE_CONDITIONS_WARNING)
                //"INTERFERENCE_WARNING" -> localResource.getString(R.string.STATUS_INTERFERENCE_WARNING)

                "UNKNOWN" -> localResource.getString(R.string.STATUS_UNKNOWN)
                else -> apiMsg
            }
            return msg
        }*/

    fun getWellfieDashboardParameterImageByCode(code: String): Int {
        var img = R.drawable.img_rate
        when (code) {
            "HEART_RATE","BREATHING_RATE" -> {
                img = R.drawable.img_rate
            }
            "BMI" -> {
                img = R.drawable.img_dash_bmi
            }
            "BP" -> {
                img = R.drawable.img_dash_bp
            }
        }
        return img
    }

    fun getTrackersList(): java.util.ArrayList<CalculatorModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!    //Constants.CODE_DUE_DATE_CALCULATOR
        val list: ArrayList<CalculatorModel> = ArrayList()
        list.add(CalculatorModel(localResource.getString(R.string.TRACKER_HEART_AGE), localResource.getString(R.string.TRACKER_DESC_HEART_AGE), R.drawable.img_tools_heart_age, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_HEART_AGE_CALCULATOR))
        list.add(CalculatorModel(localResource.getString(R.string.TRACKER_DIABETES), localResource.getString(R.string.TRACKER_DESC_DIABETES), R.drawable.img_tools_diabetes, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_DIABETES_CALCULATOR))
        list.add(CalculatorModel(localResource.getString(R.string.TRACKER_HYPERTENSION), localResource.getString(R.string.TRACKER_DESC_HYPERTENSION), R.drawable.img_tools_hypertension, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_HYPERTENSION_CALCULATOR))
        list.add(CalculatorModel(localResource.getString(R.string.TRACKER_STRESS_ANXIETY), localResource.getString(R.string.TRACKER_DESC_STRESS_ANXIETY), R.drawable.img_tools_stress_anxiety, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_STRESS_ANXIETY_CALCULATOR))
        list.add(CalculatorModel(localResource.getString(R.string.TRACKER_SMART_PHONE), localResource.getString(R.string.TRACKER_DESC_SMART_PHONE), R.drawable.img_tools_smartphone_addiction, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_SMART_PHONE_ADDICTION_CALCULATOR))
        list.add(CalculatorModel(localResource.getString(R.string.TRACKER_DUE_DATE), localResource.getString(R.string.TRACKER_DESC_DUE_DATE), R.drawable.img_due_date_cal, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_DUE_DATE_CALCULATOR))
        //list.add(TrackerDashboardModel(context.resources.getString(R.string.tracker_vaccination), context.resources.getString(R.string.tracker_desc__vaccination), R.drawable.img_vaccination,ContextCompat.getColor(context,R.color.vivantRed), "VC"))
        return list
    }

    fun showEmailClientIntent(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(intent, "Select App..."))
        }
    }

    // DoctorDetailsModel
    data class Option(val imageId: Int, val title: String, val color: Int, val code: String)

    data class NavDrawerOption(val imageId: Int, val title: String, val id: String, val color: Int)

    data class AktiVoParamModel(
        var paramId: Int = 0,
        val paramCode:String = "",
        val paramName:String = "",
        val paramValue:String = "",
        val paramImgId: Int = 0,
        val paramColor: Int = 0,
        val bgColor: Int = 0
    )

    data class FamilyRelationOption(
        val relationImgId: Int,
        val relation: String,
        val relationshipCode: String,
        val gender: String
    )

    data class WellfieResultModel(
        var paramId: Int = 0,
        val paramCode:String = "",
        val paramName:String = "",
        val paramValue:String = "",
        val paramObs:String? = "",
        val paramColor:String? = "",
        val paramToolTip:String = ""
    )

    data class FaceScanDataModel(
        var paramId: Int = 0,
        val paramCode:String = "",
        val paramName:String = "",
        val paramValue:String = "",
        val paramObs:String = ""
    )

    data class SmitFitModel(
        val featureCode:String = "",
        val featureTitle:String = "",
        val imgId: Int = 0,
        val color: Int = 0
    )

    data class FeatureModel(
        val featureCode:String = "",
        val featureTitle:String = "",
        val imgId: Int = 0,
        val color: Int = 0
    )

    data class FinancialCalculatorModel(
        val calculatorCode:String = "",
        val calculatorTitle:String = "",
        val calculatorImgId: Int = 0,
        val color: Int = 0,
        val calculatorDesc:String = "",
    )

    data class SudPolicyBannerModel(
        val bannerCode:String = "",
        val bannerName:String = "",
        val redirectLink:String = "",
        val bannerUrl:Int = 0
    )

    data class SudPolicyDownloadModel(
        val code:String = "",
        val title:String = "",
        val desc:String = ""
    )

    data class NimeyaFamilyMemberModel(
        var id:Int = 0,
        var name:String = "",
        var dob:String = "",
        var isDependent:String = "",
        var relation:String = ""
    )

    data class TutorialModel(
        var size:Int = 0,
        var title:String = "",
        var desc:String = "",
        var view: View? = null
    )

    @Parcelize
    sealed class DashboardBannerModel : Parcelable {
        data class TypePolicy(val data: @RawValue PolicyProductsModel.PolicyProducts) : DashboardBannerModel()
//        data class TypeChallenge(val data: Challenge) : DashboardBannerModel()
    }

/*    sealed class DashboardBannerModel : Serializable {
        data class TypePolicy(val data: PolicyProductsModel.PolicyProducts) : DashboardBannerModel()
        data class TypeChallenge(val data: Challenge) : DashboardBannerModel()
    }*/

    fun getWellfieParametersConverted(paramCode:String) : String {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var paramName = ""
        when(paramCode) {
            "BP" -> paramName = localResource.getString(R.string.BP)
            "STRESS_INDEX" -> paramName = localResource.getString(R.string.STRESS)
            "HEART_RATE" -> paramName = localResource.getString(R.string.HEART_RATE)
            "BREATHING_RATE" -> paramName = localResource.getString(R.string.BREATHING_RATE)
            "BLOOD_OXYGEN" -> paramName = localResource.getString(R.string.BLOOD_OXYGEN)
            "BMI" -> paramName = localResource.getString(R.string.BMI)
        }
        return paramName
    }

    fun getCreditLifeParametersList(): ArrayList<SpinnerModel> {
        val localResource = LocaleHelper.getLocalizedResources(context,Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<SpinnerModel> = ArrayList()
        list.add(SpinnerModel(localResource.getString(R.string.LOAN_ACCOUNT_NUMBER),Constants.CL_CODE_LOAN_ACCOUNT_NO, 0, false))
        list.add(SpinnerModel(localResource.getString(R.string.MEMBERSHIP_NUMBER),Constants.CL_CODE_MEMBERSHIP_NO, 1, false))
        list.add(SpinnerModel(localResource.getString(R.string.APPLICATION_NUMBER),Constants.CL_CODE_APPLICATION_NO, 2, false))
        list.add(SpinnerModel(localResource.getString(R.string.COI_NUMBER),Constants.CL_CODE_COI_NO, 3, false))
        return list
    }

    fun getSmitFitCategoryFeatures(): List<FeatureModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<FeatureModel> = ArrayList()
        list.add(FeatureModel(Constants.YOGA,localResource.getString(R.string.YOGA), R.drawable.img_yoga_new, R.color.color_yoga))
        list.add(FeatureModel(Constants.MEDITATION,localResource.getString(R.string.MEDITATION), R.drawable.img_meditation_new, R.color.color_meditation))
        list.add(FeatureModel(Constants.EXERCISE,localResource.getString(R.string.EXERCISE), R.drawable.img_exercise_new, R.color.color_exercise))
        list.add(FeatureModel(Constants.HEALTHY_BITES,localResource.getString(R.string.HEALTHY_BITES), R.drawable.img_bites, R.color.color_yoga))
        list.add(FeatureModel(Constants.WOMEN_HEALTH,localResource.getString(R.string.WOMENS_HEALTH), R.drawable.img_women_health_new, R.color.color_meditation))
        return list
    }

    fun getRiskToolsFeatures(): List<FeatureModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<FeatureModel> = ArrayList()
        list.add(FeatureModel(Constants.HRA,localResource.getString(R.string.TITLE_HRA), R.drawable.img_hra_new, R.color.vivantTeal))
        list.add(FeatureModel(Constants.IRA,localResource.getString(R.string.IMMUNITY_RISK_ASSESSMENT), R.drawable.img_ira_new, R.color.vivantTeal))
        list.add(FeatureModel(Constants.CODE_HEART_AGE_CALCULATOR,localResource.getString(R.string.HEART_AGE_CALCULATOR), R.drawable.img_heart_age_new, R.color.vivantTeal))
        list.add(FeatureModel(Constants.CODE_DIABETES_CALCULATOR,localResource.getString(R.string.DIABETES_CALCULATOR), R.drawable.img_diabetes_new, R.color.vivantTeal))
        return list
    }

    fun getHealthSelfCareFeatures(): List<FeatureModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<FeatureModel> = ArrayList()
        list.add(FeatureModel(Constants.HEALTH_LIBRARY,localResource.getString(R.string.HEALTH_LIBRARY), R.drawable.img_health_blog, R.color.transparent))
        list.add(FeatureModel(Constants.TRACK_PARAMETERS,localResource.getString(R.string.TRACK_HEALTH_PARAMETERS), R.drawable.img_health_insights, R.color.transparent))
        //list.add(FeatureModel(Constants.STORE_HEALTH_RECORDS,localResource.getString(R.string.MY_HEALTH_RECORDS), R.drawable.img_meditation, R.color.transparent))
        list.add(FeatureModel(Constants.MENTAL_WELLNESS,localResource.getString(R.string.MENTAL_WELLNESS), R.drawable.img_mental_wellness, R.color.transparent))
        return list
    }

}