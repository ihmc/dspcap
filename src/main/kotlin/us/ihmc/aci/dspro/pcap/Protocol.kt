package us.ihmc.aci.dspro.pcap

import us.ihmc.aci.dspro.pcap.disservice.Data

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