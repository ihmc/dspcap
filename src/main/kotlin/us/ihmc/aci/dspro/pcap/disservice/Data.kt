package us.ihmc.aci.dspro.pcap.disservice

import io.pkts.buffer.Buffer
import us.ihmc.aci.dspro.pcap.Body

/**
 * Created by gbenincasa on 10/31/17.
 */
class Data(private val buf: Buffer) : Body {

    val msgInfo: MessageInfo
    val data: Buffer

    init {
        val bitmap = buf.readUnsignedByte().toInt()
        val isChunk = (bitmap and 0x0001) != 0
        val sendingComplete = (bitmap and 0x0002) != 0
        val hasStats = (bitmap and 0x0004) != 0
        val isRepair = (bitmap and 0x0008) != 0
        val hasSendRate = (bitmap and 0x0010) != 0
        val hasRateEstimate = (bitmap and 0x0020) != 0
        val doNotForward = (bitmap and 0x0040) != 0
        val rateEstimate = if (hasSendRate || hasRateEstimate) buf.readInt() else 0
        msgInfo = MessageInfo(buf)
        data = buf.readBytes(msgInfo.fragmentLength.toInt())
        assert(msgInfo.fragmentLength < Int.MAX_VALUE)
        assert(buf.readableBytes.toLong() == msgInfo.fragmentLength)
    }

}