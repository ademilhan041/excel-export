package com.tss.tool.export.xls

import java.util.*

class XlsConfig(props: Properties) {
    var type: XlsType = XlsType.valueOf(props.getProperty("export.xls.type", XlsType.POI.name))

    class Builder {
        private var config = XlsConfig(Properties())

        fun type(type: XlsType): Builder {
            config.type = type
            return this
        }

        fun build() = config
    }
}

enum class XlsType {
    POI
}
