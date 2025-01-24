package com.test.my.app.common.utils

import com.test.my.app.R

/*class ParameterDataModel(title: String,value:String,description:String,finalValue:String,
                         minRange:Double,maxRange:Double,color:Int,unit:String,img:Int,code:String) {*/
class ParameterDataModel {

    var title = ""
    var value = ""
    var description = ""
    var finalValue = ""
    var minRange = 0.0
    var maxRange = 100.0
    var color: Int = R.color.vivantActive
    var unit = ""
    var img: Int = R.drawable.img_height
    var code = ""

    /*    init {
            this.title = title
            this.value = value
            this.description = description
            this.finalValue = finalValue
            this.minRange = minRange
            this.maxRange = maxRange
            this.color = color
            this.unit = unit
            this.img = img
            this.code = code
        }*/

}