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
            buf.readInt()  // TODO: workaround because readUnsignedInt() does not increment index
        metadata = zdecompress(buf.readBytes(buf.readableBytes.toInt()), uncompressedStringLength.toInt())
    }

    override fun toString(): String = metadata
}
