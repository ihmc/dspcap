package us.ihmc.aci.dspro.pcap

import io.pkts.buffer.Buffer
import us.ihmc.aci.dspro.pcap.disservice.Data
import java.nio.charset.Charset

/**
 * Created by gbenincasa on 11/1/17.
 */
enum class Protocol {
    DisService,
    DSPro,
    NMS
}

fun isDisServicePacket(pkt: NMSMessage): Boolean = pkt.chunkType == NMSMessage.Type.DataMsgComplete

fun isDSProMessage(pkt: DisServiceMessage): Boolean = pkt.body is Data
        && (pkt.body as Data).msgInfo.group.startsWith("DSPro")

fun <T> readString(buf: Buffer, len: T): String
        where T: Comparable<T>, T: Number
        = if (len.toInt() > 0) buf.readBytes(len.toInt()).array.toString(Charset.defaultCharset()) else ""