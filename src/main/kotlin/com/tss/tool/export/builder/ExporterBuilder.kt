package com.tss.tool.export.builder

import com.tss.tool.export.Exporter
import com.tss.tool.export.xls.XlsConfig
import com.tss.tool.export.xls.XlsExporter
import com.tss.tool.export.xls.XlsType
import com.tss.tool.export.xls.poi.XlsPoiExporter

class ExporterBuilder {
    private lateinit var _xlsConfig: XlsConfig

    fun xls(config: XlsConfig): ExporterBuilder {
        this._xlsConfig = config
        return this
    }

    fun build(): Exporter {
        val xlsExporterImplementation = when (_xlsConfig.type) {
            XlsType.POI -> XlsPoiExporter()
        }

        return XlsExporter(xlsExporterImplementation)
    }
}
