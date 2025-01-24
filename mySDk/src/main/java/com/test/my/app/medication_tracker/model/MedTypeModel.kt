package com.test.my.app.medication_tracker.model

import com.test.my.app.R

data class MedTypeModel(
    var medTypeTitle: String = "",
    var medTypeCode: String = "",
    var medTypeImageId: Int = R.drawable.img_capsul
)