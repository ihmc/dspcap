package us.ihmc.aci.dspro.pcap.disservice

import io.pkts.buffer.Buffer
import us.ihmc.aci.dspro.pcap.Body

/**
 * Created by gbenincasa on 10/31/17.
 */
class Data(private var buf: Buffer) : Body {

    val msgInfo: MessageInfo
    val data: Buffer

    init {
        val isChunk = buf.readUnsignedByte().toInt() != 0
        val sendingComplete = buf.readUnsignedByte().toInt() != 0
        val hasStats = buf.readUnsignedByte().toInt() != 0
        val isRepair = buf.readUnsignedByte().toInt() != 0
        val hasSendRate = buf.readUnsignedByte().toInt() != 0
        val hasRateEstimate = buf.readUnsignedByte().toInt() != 0
        val doNotForward = buf.readUnsignedByte().toInt() != 0
        val rateEstimate = if (hasSendRate || hasRateEstimate) buf.readUnsignedInt() else 0
        msgInfo = MessageInfo(buf)
        data = buf.readBytes(msgInfo.fragmentLength.toInt())
    }

}