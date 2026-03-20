package com.mlml.ashlight.auction.network

import java.io.DataInputStream
import java.io.DataOutputStream

fun DataInputStream.readVarInt(): Int {
    var result = 0; var shift = 0; var b: Int
    do {
        b = readByte().toInt() and 0xFF
        result = result or ((b and 0x7F) shl shift); shift += 7
    } while ((b and 0x80) != 0)
    return result
}

fun DataOutputStream.writeVarInt(value: Int) {
    var v = value
    while (true) {
        if ((v and -0x80) == 0) { writeByte(v); return }
        writeByte((v and 0x7F) or 0x80)
        v = v ushr 7
    }
}

fun DataInputStream.readMcString(): String {
    val bytes = ByteArray(readVarInt())
    readFully(bytes)
    return String(bytes, Charsets.UTF_8)
}

fun DataOutputStream.writeMcString(str: String) {
    val bytes = str.toByteArray(Charsets.UTF_8)
    writeVarInt(bytes.size)
    write(bytes)
}
