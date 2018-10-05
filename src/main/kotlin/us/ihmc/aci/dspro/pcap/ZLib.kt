package us.ihmc.aci.dspro.pcap

import io.pkts.buffer.Buffer
import org.apache.commons.codec.binary.Hex
import java.nio.charset.Charset

/**
 * Created by gbeni on 11/1/2017.
 */
fun zdecompress(buf: Buffer, decompressedBufLen: Int): String {
    val zlibEncodedMetadata = buf.array
    println(Hex.encodeHexString(zlibEncodedMetadata))
    val decompresser = java.util.zip.Inflater()
    try {
        decompresser.setInput(zlibEncodedMetadata, 0, zlibEncodedMetadata.size)
        val result = ByteArray(decompressedBufLen)
        val resultLength = decompresser.inflate(result)
        return String(result, 0, resultLength, Charset.defaultCharset())
        //return result.toString(Charset.defaultCharset())
    }
    finally {
        decompresser.end()
    }
}

