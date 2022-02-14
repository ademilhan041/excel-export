package com.tss.tool.export

import com.tss.tool.export.content.Content

interface Exporter {
    fun export(data: Any): Content
}
