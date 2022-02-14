package com.tss.tool.export.xls.poi

import com.tss.tool.export.xls.Color
import com.tss.tool.export.xls.ElementAlignment
import com.tss.tool.export.xls.ElementType
import com.tss.tool.export.xls.Type
import com.tss.tool.export.xls.XlsCellOptions
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.usermodel.Workbook
import java.util.concurrent.ConcurrentHashMap

object XlsStyler {
    private var formats = ConcurrentHashMap<String, CellStyle>()

    fun getStyle(wb: Workbook, options: XlsCellOptions?): CellStyle {
        val format = formats[formatName(options)]
        if (format == null) formats[formatName(options)] = createFormat(wb, options)
        return formats[formatName(options)]!!
    }

    private fun createFormat(wb: Workbook, options: XlsCellOptions?): CellStyle {
        val cellStyle = wb.createCellStyle()
        cellStyle.setFont(XlsFont.getFont(wb, options))

        if (options?.type != null) cellStyle.dataFormat = getCellFormat(wb.creationHelper, options.type)

        cellStyle.wrapText = false
        cellStyle.verticalAlignment = VerticalAlignment.CENTER

        if (options?.alignment != null) cellStyle.alignment = getAlignment(options.alignment)
        if (options?.background != null) cellStyle.fillBackgroundColor = getBackground(options.background).index

        return cellStyle
    }

    private fun getCellFormat(createHelper: CreationHelper, type: ElementType): Short {
        return when (type.type) {
            Type.NUMBER -> createHelper.createDataFormat().getFormat(type.pattern())
            Type.DOUBLE -> createHelper.createDataFormat().getFormat(type.pattern())
            Type.DECIMAL -> createHelper.createDataFormat().getFormat(type.pattern())
            Type.DATE -> createHelper.createDataFormat().getFormat(type.pattern())
            Type.DATETIME -> createHelper.createDataFormat().getFormat(type.pattern())
            else -> throw RuntimeException("EXPORT_XLS_CELL_TYPE_NOT_VALID")
        }
    }

    private fun getAlignment(alignment: ElementAlignment): HorizontalAlignment {
        return when (alignment) {
            ElementAlignment.LEFT -> HorizontalAlignment.LEFT
            ElementAlignment.CENTER -> HorizontalAlignment.CENTER
            ElementAlignment.RIGHT -> HorizontalAlignment.RIGHT
        }
    }

    private fun getBackground(color: Color): IndexedColors {
        return when (color) {
            Color.BLACK -> IndexedColors.BLACK
            Color.WHITE -> IndexedColors.WHITE
            Color.RED -> IndexedColors.RED
        }
    }

    private fun formatName(options: XlsCellOptions?): String {
        return if (options == null) "format-default"
        else {
            var key = "format"
            if (options.type != null) key += "-" + options.type.type.name
            if (options.alignment != null) key += "-" + options.alignment.name
            if (options.weight != null) key += "-" + options.weight.name
            if (options.background != null) key += "-" + options.background.name
            if (options.color != null) key += "-" + options.color.name
            key
        }
    }
}
