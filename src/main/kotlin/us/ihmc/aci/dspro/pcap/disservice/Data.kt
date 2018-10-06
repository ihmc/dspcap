package us.ihmc.aci.dspro.pcap.disservice

import io.pkts.buffer.Buffer
import io.pkts.buffer.Buffers
import us.ihmc.aci.dspro.pcap.Body

/**
 * Created by gbenincasa on 10/31/17.
 */
class Data(
        val msgInfo: MessageInfo,
        val data: Buffer) : Body {

    companion object {
        fun getData(buf: Buffer): Data {
            val bitmap = buf.readUnsignedByte().toInt()
            val isChunk = (bitmap and 0x0001) != 0
            val sendingComplete = (bitmap and 0x0002) != 0
            val hasStats = (bitmap and 0x0004) != 0
            val isRepair = (bitmap and 0x0008) != 0
            val hasSendRate = (bitmap and 0x0010) != 0
            val hasRateEstimate = (bitmap and 0x0020) != 0
            val doNotForward = (bitmap and 0x0040) != 0
            val rateEstimate = if (hasSendRate || hasRateEstimate) buf.readInt() else 0
            val msgInfo = MessageInfo.getMessageInfo(buf)
            val dataLen = msgInfo.fragmentLength.toInt()
            val data = if (dataLen > 0) buf.readBytes(dataLen) else Buffers.EMPTY_BUFFER
            assert(msgInfo.fragmentLength < Int.MAX_VALUE)
            assert(buf.readableBytes.toLong() == msgInfo.fragmentLength)

            return Data(msgInfo, data)
        }
    }
}