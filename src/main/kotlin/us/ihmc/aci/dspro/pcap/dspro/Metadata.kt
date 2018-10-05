package us.ihmc.aci.dspro.pcap.dspro

import io.pkts.buffer.Buffer
import us.ihmc.aci.dspro.pcap.Body
import us.ihmc.aci.dspro.pcap.zdecompress

/**
 * Created by gbenincasa on 10/31/17.
 */
data class Metadata(private val buf: Buffer) : Body {
    val metadata: String

    init {
        val uncompressedStringLength = buf.readUnsignedInt()
        metadata = zdecompress(buf.readBytes(buf.readableBytes), uncompressedStringLength.toInt())
    }

    override fun toString(): String = metadata
}
