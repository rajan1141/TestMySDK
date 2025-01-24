package com.test.my.app.home.common

import android.content.Context
import com.test.my.app.R
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.LocaleHelper
import com.test.my.app.home.common.DataHandler.FeatureModel
import java.util.Locale

object HomeHelper {

    fun getSmitFitFeatures(context:Context): List<FeatureModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<FeatureModel> = ArrayList()
        list.add(FeatureModel(Constants.MEDITATION,localResource.getString(R.string.MEDITATION), R.drawable.img_meditation, R.color.color_meditation))
        list.add(FeatureModel(Constants.YOGA,localResource.getString(R.string.YOGA), R.drawable.img_yoga, R.color.color_yoga))
        list.add(FeatureModel(Constants.EXERCISE,localResource.getString(R.string.EXERCISE), R.drawable.img_exercise, R.color.color_exercise))
        list.add(FeatureModel(Constants.HEALTHY_BITES,localResource.getString(R.string.HEALTHY_BITES), R.drawable.img_healthy_bites, R.color.color_yoga))
        list.add(FeatureModel(Constants.WOMEN_HEALTH,localResource.getString(R.string.WOMENS_HEALTH), R.drawable.img_women_health, R.color.color_meditation))
        return list
    }

    fun getRiskToolsFeatures(context:Context): List<FeatureModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<FeatureModel> = ArrayList()
        list.add(FeatureModel(Constants.HRA,localResource.getString(R.string.TITLE_HRA), R.drawable.img_hra_new, R.color.vivantTeal))
        list.add(FeatureModel(Constants.IRA,localResource.getString(R.string.IMMUNITY_RISK_ASSESSMENT), R.drawable.img_ira_new, R.color.vivantTeal))
        list.add(FeatureModel(Constants.HEART_AGE_CALCULATOR,localResource.getString(R.string.HEART_AGE_CALCULATOR), R.drawable.img_heart_age_new, R.color.vivantTeal))
        list.add(FeatureModel(Constants.DIABETES_CALCULATOR,localResource.getString(R.string.DIABETES_CALCULATOR), R.drawable.img_diabetes_new, R.color.vivantTeal))
        return list
    }

    fun getHealthSelfCareFeatures(context:Context): List<FeatureModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<FeatureModel> = ArrayList()
        list.add(FeatureModel(Constants.HEALTH_LIBRARY,localResource.getString(R.string.HEALTH_LIBRARY), R.drawable.img_health_blog, R.color.transparent))
        list.add(FeatureModel(Constants.TRACK_PARAMETERS,localResource.getString(R.string.TRACK_HEALTH_PARAMETERS), R.drawable.img_health_insights, R.color.transparent))
        //list.add(FeatureModel(Constants.STORE_HEALTH_RECORDS,localResource.getString(R.string.MY_HEALTH_RECORDS), R.drawable.img_meditation, R.color.transparent))
        list.add(FeatureModel(Constants.MENTAL_WELLNESS,localResource.getString(R.string.MENTAL_WELLNESS), R.drawable.img_mental_wellness, R.color.transparent))
        return list
    }

}