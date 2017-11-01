package us.ihmc.aci.dspro.pcap.dspro

import io.pkts.buffer.Buffer
import us.ihmc.aci.dspro.pcap.Body
import java.nio.charset.Charset
import java.util.zip.Inflater

/**
 * Created by gbenincasa on 10/31/17.
 */
class Metadata(private var buf: Buffer) : Body {

    val metadata: String

    init {
        var zlibEncodedMetadata = buf.rawArray
        val decompresser = Inflater()
        try {
            decompresser.setInput(zlibEncodedMetadata, 0, zlibEncodedMetadata.size)
            val result = ByteArray(1400)
            val resultLength = decompresser.inflate(result)
            metadata = String(result, 0, resultLength, Charset.defaultCharset())
        }
        finally {
            decompresser.end()
        }
    }
}