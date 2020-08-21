package com.testtube.gstreporter.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class DocumentExportService<T : Any> {

    suspend fun createSheet(
        context: Context,
        dataList: List<T>,
        exclusions: List<String> = Collections.emptyList()
    ): Deferred<File?>? {
        if (dataList.isEmpty()) {
            return null
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
            return null
        }

        return coroutineScope {
            async {
                val workbook: Workbook = XSSFWorkbook()
                val sheet: Sheet = workbook.createSheet("Users") //Creating a sheet
                val header: Row = sheet.createRow(0)

                val type = dataList[0]
                val memberProperties =
                    type::class.memberProperties.filter { getFilter(it, exclusions) }
                memberProperties.forEachIndexed { index, member ->
                    header.createCell(index)
                        .setCellValue(member.name.replace("_", " ").capitalize())
                }

                dataList.forEachIndexed { index, data ->
                    val row: Row = sheet.createRow(index + 2)
                    memberProperties.forEachIndexed { i, member ->
                        val name = member.name
                        row.createCell(i).setCellValue(readInstanceProperty(data, name))
                    }
                }

                try {
                    val fileName = "${Common.getUser()}_${Common.getFormattedDate("dd_MMM_yy")}"
                    val file =
                        Common.createFile(
                            context,
                            Constants.Ext.XLSX,
                            Constants.DirType.Doc,
                            fileName
                        )
                    val fileOut = FileOutputStream(file)
                    workbook.write(fileOut)
                    fileOut.close()
                    Log.i(this::class.simpleName, "createSheet: " + file.path)
                    file
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    null
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    private fun getFilter(
        property: KProperty1<out T, Any?>,
        exclusions: List<String>
    ): Boolean {
        return property.name != "equals" || property.name != "toString" || !exclusions.contains(
            property.name
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun readInstanceProperty(instance: Any, propertyName: String): String {
        val property = instance::class.memberProperties
            // don't cast here to <Any, R>, it would succeed silently
            .first { it.name == propertyName } as KProperty1<Any, *>
        // force a invalid cast exception if incorrect type here
        return property.get(instance).toString()
    }

}