package com.testtube.gstreporter.model

import com.testtube.gstreporter.utils.Constants
import java.util.*

class Filter(
    var startDate: Date?,
    var endDate: Date?,
    var isRange: Boolean,
    var filterType: Enum<Constants.FilterType> = Constants.FilterType.Date
)