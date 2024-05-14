package com.inappstory.sdk.utils.filepicker.utils.faststart

import java.nio.ByteBuffer
import java.nio.ByteOrder

class TypeConversion {
    fun uint32ToLong(int32: Int): Long {
        return int32.toLong() and 0x00000000ffffffffL
    }

    @Throws(UnsupportedFileException::class)
    fun uint32ToInt(uint32: Int): Int {
        if (uint32 < 0) {
            throw UnsupportedFileException("uint32 value is too large")
        }
        return uint32
    }

    @Throws(UnsupportedFileException::class)
    fun uint32ToInt(uint32: Long): Int {
        if (uint32 > Int.MAX_VALUE || uint32 < 0) {
            throw UnsupportedFileException("uint32 value is too large")
        }
        return uint32.toInt()
    }

    @Throws(UnsupportedFileException::class)
    fun uint64ToLong(uint64: Long): Long {
        if (uint64 < 0) throw UnsupportedFileException("uint64 value is too large")
        return uint64
    }

    fun fourCcToInt(byteArray: ByteArray): Int {
        return ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).int
    }
}