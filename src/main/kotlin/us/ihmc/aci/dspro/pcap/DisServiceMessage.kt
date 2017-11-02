package us.ihmc.aci.dspro.pcap

import io.pkts.buffer.Buffer
import io.pkts.buffer.Buffers
import us.ihmc.aci.dspro.pcap.disservice.Data
import java.nio.charset.Charset
import java.text.ParseException

/**
 * Created by gbenincasa on 10/31/17.
 */
data class DisServiceMessage(private val buf: Buffer) : Message {

    constructor(byteArr: ByteArray) : this(Buffers.wrap(byteArr))

    enum class Type(val code: Short) {

        Unknown(0x00),
        Data(0x01),
        DataReq(0x02),
        WorldStateSeqId(0x03),
        SubStateMessage(0x04),
        SubStateReq(0x05),
        TopologyStateMessage(0x06),
        DataCacheQuery(0x07),
        DataCacheQueryReply(0x08),
        DataCacheMessagesRequest(0x09),
        AcknowledgmentMessage(0x10),
        CompleteMessageReq(0x11),
        CacheEmpty(0x12),
        CtrlToCtrlMessage(0x13),
        ChunkReq(0x14),
        HistoryReq(0x15),
        HistoryReqReply(0x16),

        Query(0x17),
        QueryHits(0x18),
        SubAdvMessage(0x19),

        SearchMsg(0x20),
        SearchMsgReply(0x2A),
        VolatileSearchMsgReply(0x2B),

        ImprovedSubStateMessage(0x2C),
        ProbabilitiesMsg(0x2D);

        companion object {
            private val map = values().associateBy(Type::code)
            fun fromShort(type: Short) = map[type]
        }
    }

    val type: Type
    val targetNodeId: String
    val senderNodeId: String
    val sessionId: String
    val body: Body

    init {
        type = Type.fromShort(buf.readUnsignedByte())
                ?: throw ParseException("Could not parse chunk type", buf.readerIndex)
        targetNodeId = readString(buf, buf.readUnsignedByte())
        senderNodeId = readString(buf, buf.readUnsignedByte())
        sessionId = readString(buf, buf.readUnsignedByte())
        body = when (type) {
            Type.Data -> us.ihmc.aci.dspro.pcap.disservice.Data(buf)
            Type.CtrlToCtrlMessage -> us.ihmc.aci.dspro.pcap.disservice.Controller(buf)
            else -> Empty(buf)
        }
    }

    override fun toString(): String = "DisService Message $type from <$senderNodeId> for <$targetNodeId> in session <$sessionId>"
    fun isEmpty(): Boolean = body is Empty
    override fun getMessage(protocol: Protocol): Message = when (protocol) {
        Protocol.DSPro -> if (isDSProMessage(this)) DSProMessage(this.body as Data) else this
        else -> this
    }
}