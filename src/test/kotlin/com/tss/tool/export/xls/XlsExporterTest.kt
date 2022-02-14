package com.tss.tool.export.xls

import com.tss.tool.export.builder.ExporterBuilder
import org.junit.Test
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class XlsExporterTest {

    @Test
    fun testXls() {
        val xlsExporter = ExporterBuilder()
            .xls(XlsConfig(mapOf("export.xls.type" to "POI").toProperties()))
            .build()

        val content = xlsExporter.export(
            XlsData(
                XlsSheet(
                    "TSS",
                    XlsLayout(
                        listOf(
                            XlsMerge(1, 2, 2, 2),
                            XlsMerge(1, 2, 3, 4)
                        )
                    ),
                    XlsRow(
                        XlsCell(
                            "text", XlsCellOptions(
                                alignment = ElementAlignment.RIGHT,
                                color = Color.RED
                            )
                        ),
                        XlsCell(
                            1, XlsCellOptions(
                                type = ElementType(Type.NUMBER),
                                alignment = ElementAlignment.RIGHT
                            )
                        ),
                        XlsCell(
                            1.491231, XlsCellOptions(
                                type = ElementType(Type.DOUBLE),
                                alignment = ElementAlignment.RIGHT
                            )
                        ),
                        XlsCell(
                            BigDecimal(1.2), XlsCellOptions(
                                type = ElementType(Type.DECIMAL),
                                alignment = ElementAlignment.RIGHT
                            )
                        ),
                        XlsCell(
                            LocalDate.parse("2018-12-31"), XlsCellOptions(
                                type = ElementType(Type.DATE),
                                alignment = ElementAlignment.CENTER
                            )
                        ),
                        XlsCell(
                            LocalDateTime.parse("2018-12-31T12:00:00"), XlsCellOptions(
                                type = ElementType(Type.DATETIME)
                            )
                        ),
                    )
                )
            )
        )

        val file = File("test.xls")
        file.writeBytes(content.data.byte!!)
        file.delete()
    }
}