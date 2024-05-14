package com.inappstory.sdk.utils.filepicker.utils.faststart
/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Yuya Tanaka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import android.util.Log
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class FastStart(private val inputFileName: String, private val outputFileName: String) :
    IFastStart {

    private val typeConversion = TypeConversion()

    private val iFreeAtom = typeConversion.fourCcToInt("free".toByteArray())
    private val iJunkAtom = typeConversion.fourCcToInt("junk".toByteArray())
    private val iMdatAtom = typeConversion.fourCcToInt("mdat".toByteArray())
    private val iMoovAtom = typeConversion.fourCcToInt("moov".toByteArray())
    private val iPnotAtom = typeConversion.fourCcToInt("pnot".toByteArray())
    private val iSkipAtom = typeConversion.fourCcToInt("skip".toByteArray())
    private val iWideAtom = typeConversion.fourCcToInt("wide".toByteArray())
    private val iPictAtom = typeConversion.fourCcToInt("PICT".toByteArray())
    private val iFtypAtom = typeConversion.fourCcToInt("ftyp".toByteArray())
    private val iUuidAtom = typeConversion.fourCcToInt("uuid".toByteArray())
    private val iCmovAtom = typeConversion.fourCcToInt("cmov".toByteArray())
    private val iStcoAtom = typeConversion.fourCcToInt("stco".toByteArray())
    private val iCo64Atom = typeConversion.fourCcToInt("co64".toByteArray())

    private val atomPreambleSize = 8

    @Throws(IOException::class)
    private fun readAndFill(infile: FileChannel, buffer: ByteBuffer): Boolean {
        buffer.clear()
        val size = infile.read(buffer)
        buffer.flip()
        return size == buffer.capacity()
    }

    @Throws(IOException::class)
    private fun readAndFill(infile: FileChannel, buffer: ByteBuffer, position: Long): Boolean {
        buffer.clear()
        val size = infile.read(buffer, position)
        buffer.flip()
        return size == buffer.capacity()
    }

    private fun safeClose(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (_: IOException) {
        }
    }

    @Throws(
        IOException::class,
        MalformedFileException::class,
        UnsupportedFileException::class
    )

    override fun fastStart(): Boolean {
        var ret = false
        val startTime = System.currentTimeMillis()
        val inputFile = File(inputFileName)
        if (!inputFile.exists()) return false
        val outputFile = File(outputFileName)
        val dirAsFile: File = outputFile.parentFile!!
        if (!dirAsFile.exists()) {
            dirAsFile.mkdirs()
        }
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }
        var inStream: FileInputStream? = null
        var outStream: FileOutputStream? = null
        return try {
            inStream = FileInputStream(inputFile)
            val infile = inStream.channel
            outStream = FileOutputStream(outputFile)
            val outfile = outStream.channel
            fastStartImpl(infile, outfile).also { ret = it }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            safeClose(inStream)
            safeClose(outStream)
            Log.e("fastStartTimings", "$inputFileName $ret ${System.currentTimeMillis() - startTime}")
            if (!ret) {
                outputFile.delete()
            }
        }
    }

    @Throws(
        IOException::class,
        MalformedFileException::class,
        UnsupportedFileException::class
    )
    private fun fastStartImpl(infile: FileChannel, outfile: FileChannel): Boolean {
        val atomBytes =
            ByteBuffer.allocate(atomPreambleSize).order(ByteOrder.BIG_ENDIAN)
        var atomType = 0
        var atomSize: Long = 0 // uint64_t
        var ftypAtom: ByteBuffer? = null
        var startOffset: Long = 0
        while (readAndFill(infile, atomBytes)) {
            atomSize = typeConversion.uint32ToLong(atomBytes.int)
            atomType = atomBytes.int
            if (atomType == iFtypAtom) {
                val ftypAtomSize =
                    typeConversion.uint32ToInt(atomSize)
                ftypAtom = ByteBuffer.allocate(ftypAtomSize).order(ByteOrder.BIG_ENDIAN)
                atomBytes.rewind()
                ftypAtom.put(atomBytes)
                if (infile.read(ftypAtom) < ftypAtomSize - atomPreambleSize) break
                ftypAtom.flip()
                startOffset = infile.position()
            } else {
                if (atomSize == 1L) {
                    atomBytes.clear()
                    if (!readAndFill(infile, atomBytes)) break
                    atomSize =
                        typeConversion.uint64ToLong(atomBytes.long)
                    infile.position(infile.position() + atomSize - atomPreambleSize * 2)
                } else {
                    infile.position(infile.position() + atomSize - atomPreambleSize)
                }
            }
            if (atomType != iFreeAtom &&
                atomType != iJunkAtom &&
                atomType != iMdatAtom &&
                atomType != iMoovAtom &&
                atomType != iPnotAtom &&
                atomType != iSkipAtom &&
                atomType != iWideAtom &&
                atomType != iPictAtom &&
                atomType != iUuidAtom &&
                atomType != iFtypAtom
            ) {
                break
            }
            if (atomSize < 8) break
        }
        if (atomType != iMoovAtom) {
            return false
        }
        val moovAtomSize: Int = typeConversion.uint32ToInt(atomSize)
        val moovAtom: ByteBuffer = ByteBuffer.allocate(moovAtomSize).order(ByteOrder.BIG_ENDIAN)
        val lastOffset = infile.size() - moovAtomSize
        if (!readAndFill(infile, moovAtom, lastOffset)) {
            throw MalformedFileException("failed to read moov atom")
        }
        if (moovAtom.getInt(12) == iCmovAtom) {
            throw UnsupportedFileException("this utility does not support compressed moov atoms yet")
        }
        while (moovAtom.remaining() >= 8) {
            val atomHead = moovAtom.position()
            atomType = moovAtom.getInt(atomHead + 4)
            if (!(atomType == iStcoAtom || atomType == iCo64Atom)) {
                moovAtom.position(moovAtom.position() + 1)
                continue
            }
            atomSize = typeConversion.uint32ToLong(moovAtom.getInt(atomHead))
            if (atomSize > moovAtom.remaining()) {
                throw MalformedFileException("bad atom size")
            }
            moovAtom.position(atomHead + 12)
            if (moovAtom.remaining() < 4) {
                throw MalformedFileException("malformed atom")
            }
            val offsetCount = typeConversion.uint32ToInt(moovAtom.int)
            if (atomType == iStcoAtom) {
                if (moovAtom.remaining() < offsetCount * 4) {
                    throw MalformedFileException("bad atom size/element count")
                }
                for (i in 0 until offsetCount) {
                    val currentOffset = moovAtom.getInt(moovAtom.position())
                    val newOffset =
                        currentOffset + moovAtomSize
                    if (currentOffset < 0 && newOffset >= 0) {
                        throw UnsupportedFileException(
                            "This is bug in original qt-faststart.c: "
                                    + "stco atom should be extended to co64 atom as new offset value overflows uint32, "
                                    + "but is not implemented."
                        )
                    }
                    moovAtom.putInt(newOffset)
                }
            } else {
                if (moovAtom.remaining() < offsetCount * 8) {
                    throw MalformedFileException("bad atom size/element count")
                }
                for (i in 0 until offsetCount) {
                    val currentOffset = moovAtom.getLong(moovAtom.position())
                    moovAtom.putLong(currentOffset + moovAtomSize)
                }
            }
        }
        infile.position(startOffset)
        if (ftypAtom != null) {
            ftypAtom.rewind()
            outfile.write(ftypAtom)
        }
        moovAtom.rewind()
        outfile.write(moovAtom)
        infile.transferTo(startOffset, lastOffset - startOffset, outfile)
        return true
    }
}