package com.testtube.gstreporter.utils

import android.os.Environment

class Constants {

    companion object {
        const val imageSampleSize: Int = 1
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_CAPTURE_FRAG = 2
    }

    object DirType {
        val Image = Environment.DIRECTORY_PICTURES
        val Doc = Environment.DIRECTORY_DOCUMENTS
    }

    object Ext {
        val PNG = ".png"
        val JPG = ".jpg"
        val PDF = ".pdf"
        val XLSX = ".xlsx"
    }

    enum class FilterType {
        Date, Month
    }

    enum class States  {
        NA,
        JAMMU_AND_KASHMIR,
        HIMACHAL_PRADESH,
        PUNJAB,
        CHANDIGARH,
        UTTARAKHAND,
        HARYANA,
        DELHI,
        RAJASTHAN,
        UTTAR_PRADESH,
        BIHAR,
        SIKKIM,
        ARUNACHAL_PRADESH,
        NAGALAND,
        MANIPUR,
        MIZORAM,
        TRIPURA,
        MEGHLAYA,
        ASSAM,
        WEST_BENGAL,
        JHARKHAND,
        ODISHA,
        CHATTISGARH,
        MADHYA_PRADESH,
        GUJARAT,
        DADRA_AND_NAGAR_HAVELI_AND_DAMAN_AND_DIU,
        MAHARASHTRA,
        ANDHRA_PRADESH,
        KARNATAKA,
        GOA,
        LAKSHWADEEP,
        KERALA,
        TAMIL_NADU,
        PUDUCHERRY,
        ANDAMAN_AND_NICOBAR_ISLANDS,
        TELANGANA,
        ANDHRA_PRADESH_,
        LADAKH
    }
}
