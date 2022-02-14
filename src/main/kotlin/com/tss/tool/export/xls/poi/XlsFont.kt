@file:Suppress("INACCESSIBLE_TYPE")

package com.tss.tool.export.xls.poi

import com.tss.tool.export.xls.Color
import com.tss.tool.export.xls.ElementSize
import com.tss.tool.export.xls.ElementWeight
import com.tss.tool.export.xls.XlsCellOptions
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Workbook
import java.util.concurrent.ConcurrentHashMap

object XlsFont {
    private var fonts = ConcurrentHashMap<String, Font>()

    fun getFont(wb: Workbook, options: XlsCellOptions?): Font {
        val font = fonts[fontName(options)]
        if (font == null) fonts[fontName(options)] = createFont(wb, options)
        return fonts[fontName(options)]!!
    }

    private fun createFont(wb: Workbook, options: XlsCellOptions?): Font {
        val font = wb.createFont()
        font.fontHeightInPoints = 11.toShort()
        font.fontName = "Calibri"
        font.bold = false

        if (options?.weight != null && isBold(options.weight)) font.bold = true
        if (options?.size != null) font.fontHeightInPoints = findPixel(options.size)
        if (options?.color != null) font.color = findColor(options.color)

        return font
    }

    private fun fontName(options: XlsCellOptions?): String {
        return if (options == null) "font-default"
        else {
            var key = "font"
            if (options.weight != null) key += "-" + options.weight.name
            if (options.size != null) key += "-" + options.size.name
            if (options.color != null) key += "-" + options.color.name
            key
        }
    }

    private fun isBold(weight: ElementWeight?) = weight != null && weight == ElementWeight.BOLD

    private fun findPixel(size: ElementSize?): Short {
        if (size == null) return 11
        if (size == ElementSize.H1) return 24
        if (size == ElementSize.H2) return 19
        if (size == ElementSize.H3) return 16
        if (size == ElementSize.H4) return 12
        if (size == ElementSize.H5) return 10
        if (size == ElementSize.H6) return 8
        return 11
    }

    private fun findColor(color: Color): Short {
        return when (color) {
            Color.BLACK -> IndexedColors.BLACK.index
            Color.WHITE -> IndexedColors.WHITE.index
            Color.RED -> IndexedColors.RED.index
        }
    }
}
