package us.ihmc.aci.dspro.pcap

import com.jcraft.jzlib.JZlib
import io.pkts.buffer.Buffer
import org.apache.commons.codec.binary.Hex
import java.nio.charset.Charset

/**
 * Created by gbeni on 11/1/2017.
 */
fun zdecompress(buf: Buffer, decompressedBufLen: Int): String {
    val zlibEncodedMetadata = buf.rawArray
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

fun zdecompress2(buf: Buffer): String {
    fun CHECK_ERR(z: com.jcraft.jzlib.Inflater, err: Int, msg: String) {
        if(err!=JZlib.Z_OK) {
            if (z.msg != null) {
                print(z.msg + " ")
            }
            println(msg + " error: " + err);
        }
    }

    val inflater = com.jcraft.jzlib.Inflater()

    inflater.setInput(buf.array)
    val uncompr = ByteArray(1400)
    inflater.setOutput(uncompr)

    var err = inflater.init()
    CHECK_ERR(inflater, err, "inflateInit")

    while (inflater.total_out < uncompr.size && inflater.total_in < buf.array.size) {
        inflater.avail_out = 1
        inflater.avail_in = inflater.avail_out /* force small buffers */
        err = inflater.inflate(JZlib.Z_NO_FLUSH)
        if (err === JZlib.Z_STREAM_END) break
        CHECK_ERR(inflater, err, "inflate")
    }

    return uncompr.toString(Charset.defaultCharset())
}
