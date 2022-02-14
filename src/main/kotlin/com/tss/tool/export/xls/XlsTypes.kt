package com.tss.tool.export.xls

class XlsData(vararg val sheets: XlsSheet, var fileName: String = "")

class XlsSheet(val name: String, val layout: XlsLayout?, vararg val rows: XlsRow)
open class XlsLayout(val mergeList: List<XlsMerge>? = null, val widthAdjustment: XlsColumnWidth? = null)
class XlsDefaultLayout : XlsLayout(listOf())
class XlsMerge(val columnStart: Int, val columnEnd: Int, val rowStart: Int, val rowEnd: Int) {
    init {
        if (columnStart < 1 || columnEnd < 1 || rowStart < 1 || rowEnd < 1)
            throw IllegalStateException("Excel merge index starts from 1")
    }
}

class XlsColumnWidth(val autoArrange: Boolean, val columnWidth: List<Int> = listOf()) {
    init {
        if (!autoArrange && columnWidth.isEmpty()) throw IllegalStateException("Excel column width arrangement needed")
    }
}

class XlsRow {
    val cells: List<XlsCell>

    constructor(vararg cells: XlsCell) {
        this.cells = cells.toList()
    }

    constructor(cells: List<XlsCell>) {
        this.cells = cells
    }
}

open class XlsCell(val value: Any, val options: XlsCellOptions? = null)
class XlsEmptyCell : XlsCell("", null)
open class XlsImageCell(value: ByteArray, val width: Int, val height: Int) : XlsCell(value, null)

data class XlsCellOptions(
    val type: ElementType? = null,
    val alignment: ElementAlignment? = null,
    val weight: ElementWeight? = null,
    val size: ElementSize? = null,
    val border: Float? = null,
    val background: Color? = null,
    val color: Color? = null
)

data class ElementType(val type: Type, val pattern: String = "") {
    fun pattern(): String {
        if (pattern.isNotEmpty()) return pattern

        return when (type) {
            Type.NUMBER -> "###,##0"
            Type.DOUBLE -> "###,##0.######################"
            Type.DECIMAL -> "###,##0.00"
            Type.DATE -> "yyyy-MM-dd"
            Type.DATETIME -> "yyyy-MM-dd hh:mm:ss"
            else -> ""
        }
    }
}
