package com.tss.tool.export.xls.poi

import com.tss.tool.export.Exporter
import com.tss.tool.export.content.Content
import com.tss.tool.export.content.ContentData
import com.tss.tool.export.content.MimeTypeMap
import com.tss.tool.export.isNotNull
import com.tss.tool.export.xls.Type
import com.tss.tool.export.xls.XlsCell
import com.tss.tool.export.xls.XlsColumnWidth
import com.tss.tool.export.xls.XlsData
import com.tss.tool.export.xls.XlsImageCell
import com.tss.tool.export.xls.XlsLayout
import com.tss.tool.export.xls.XlsMerge
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.ClientAnchor
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.util.Units
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class XlsPoiExporter : Exporter {
    override fun export(data: Any): Content {
        val xlsData = data as XlsData

        val workbook = WorkbookFactory.create(false)

        xlsData.sheets.forEach { xlsSheet ->
            val sheet = workbook.createSheet(xlsSheet.name)
            xlsSheet.rows.forEachIndexed { rowIndex, xlsRow ->
                val row = sheet.createRow(rowIndex)
                xlsRow.cells.forEachIndexed { columnIndex, xlsCell ->
                    sheet.autoSizeColumn(columnIndex)

                    val cell = row.createCell(columnIndex)
                    if (xlsCell is XlsImageCell) {
                        createImageCell(workbook, sheet, cell, xlsCell)
                    } else {
                        createCell(workbook, cell, xlsCell)
                    }
                }
            }

            if (xlsSheet.layout != null) setLayout(xlsSheet.layout, sheet)
        }

        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        workbook.close()

        val result = outputStream.toByteArray()

        return Content(
            "${decideFileName(data)}.xls",
            MimeTypeMap.mimeTypeFromExtension("xls")!!,
            ContentData.byte(result)
        )
    }

    private fun createCell(wb: Workbook, cell: Cell, xlsCell: XlsCell) {
        if (xlsCell.options?.type != null) {
            when (xlsCell.options.type.type) {
                Type.NUMBER -> createNumberCell(cell, xlsCell)
                Type.DOUBLE -> cell.setCellValue((xlsCell.value as Double))
                Type.DECIMAL -> cell.setCellValue((xlsCell.value as BigDecimal).toDouble())
                Type.DATE -> cell.setCellValue(xlsCell.value as LocalDate)
                Type.DATETIME -> cell.setCellValue(xlsCell.value as LocalDateTime)
                else -> throw java.lang.IllegalArgumentException("Unsupported cell type[${Type.IMAGE}]")
            }
        } else {
            cell.setCellValue(xlsCell.value as String)
        }

        val newStyle: CellStyle = wb.createCellStyle()
        newStyle.cloneStyleFrom(XlsStyler.getStyle(wb, xlsCell.options))
        cell.cellStyle = newStyle
    }

    private fun createNumberCell(cell: Cell, xlsCell: XlsCell) {
        when (xlsCell.value) {
            is Int -> cell.setCellValue(xlsCell.value.toDouble())
            is Long -> cell.setCellValue(xlsCell.value.toDouble())
            else -> throw IllegalArgumentException("Unsupported cell type[${xlsCell.value.javaClass.simpleName}]")
        }
    }

    private fun createImageCell(wb: Workbook, sheet: Sheet, cell: Cell, xlsCell: XlsCell) {
        xlsCell as XlsImageCell

        val helper = wb.creationHelper
        val anchor = helper.createClientAnchor()
        anchor.anchorType = ClientAnchor.AnchorType.MOVE_AND_RESIZE
        anchor.row1 = cell.rowIndex
        anchor.setCol1(cell.columnIndex)
        anchor.dx1 = 1 * Units.EMU_PER_PIXEL
        anchor.dx2 = xlsCell.width * Units.EMU_PER_PIXEL
        anchor.dy1 = 1 * Units.EMU_PER_PIXEL
        anchor.dy2 = xlsCell.height * Units.EMU_PER_PIXEL

        val drawing = sheet.createDrawingPatriarch()
        val pictureIndex = wb.addPicture(xlsCell.value as ByteArray, Workbook.PICTURE_TYPE_PNG)
        val pict = drawing.createPicture(anchor, pictureIndex)
        pict.resize()
    }

    private fun setLayout(layout: XlsLayout, sheet: Sheet) {
        handleMerging(layout, layout.mergeList, sheet)
        handleWidthAdjustment(layout, sheet, layout.widthAdjustment)
    }

    private fun handleMerging(layout: XlsLayout, mergeList: List<XlsMerge>?, sheet: Sheet) {
        if (!layout.mergeList.isNullOrEmpty()) {
            mergeList!!.forEach {
                sheet.addMergedRegion(
                    CellRangeAddress(
                        it.rowStart - 1,
                        it.rowEnd - 1,
                        it.columnStart - 1,
                        it.columnEnd - 1
                    )
                )
            }
        }
    }

    private fun handleWidthAdjustment(layout: XlsLayout, sheet: Sheet, widthAdjustment: XlsColumnWidth?) {
        if (layout.widthAdjustment.isNotNull()) {
            if (layout.widthAdjustment!!.autoArrange) {
                sheet.getRow(0).cellIterator().forEach { sheet.autoSizeColumn(it.columnIndex) }
            } else {
                widthAdjustment!!.columnWidth.forEachIndexed { index, width ->
                    sheet.setColumnWidth(
                        index,
                        width * 256
                    )
                }
            }
        }
    }

    private fun decideFileName(data: XlsData): String {
        if (data.fileName.isNotBlank()) return data.fileName
        return data.sheets.first().name
    }
}
