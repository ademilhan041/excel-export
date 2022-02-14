package com.tss.tool.export.xls

import com.tss.tool.export.Exporter
import com.tss.tool.export.content.Content
import com.tss.tool.export.xls.poi.XlsPoiExporter

class XlsExporter(private val xlsPoiExporter: XlsPoiExporter) : Exporter {
    override fun export(data: Any): Content {
        return xlsPoiExporter.export(data)
    }
}
