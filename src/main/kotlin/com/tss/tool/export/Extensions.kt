package com.tss.tool.export

import java.io.InputStream
import java.util.*

fun Any?.isNotNull() = this != null

fun String.removeLast() = substring(0, length - 1)

fun ByteArray.toBase64(): String {
    return Base64.getEncoder().encodeToString(this)
}

fun InputStream.safeClose() {
    try {
        close()
    } catch (e: Exception) {
    }
}