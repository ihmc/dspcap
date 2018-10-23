package us.ihmc.aci.dspro.pcap

import io.pkts.buffer.Buffer
import io.pkts.buffer.Buffers
import us.ihmc.aci.dspro.pcap.disservice.Data
import java.nio.charset.Charset
import java.text.ParseException

/**
 * Created by gbenincasa on 10/31/17.
 */


data class DisServiceMessage(
        private val targetNodeId: String,
        private val senderNodeId: String,
        private val sessionId: String,
        val body: Body,
        private val type: Type = Type.Data) : Message {

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

    companion object {

        fun getDisServiceDataMessage(data: Data) = DisServiceMessage("",
                data.msgInfo.publisher, "", data, Type.Data)

        fun getDisServiceMessage(buf: Buffer): DisServiceMessage {
            val b = buf.readUnsignedByte()
            val type = Type.fromShort(b)
                    ?: throw ParseException("Could not parse chunk type $b", buf.readerIndex)
            val targetNodeId = readString(buf, buf.readUnsignedByte())
            val senderNodeId = readString(buf, buf.readUnsignedByte())
            val sessionId = readString(buf, buf.readUnsignedByte())
            val body = when (type) {
                Type.Data -> us.ihmc.aci.dspro.pcap.disservice.Data.getData(buf)
                Type.CtrlToCtrlMessage -> us.ihmc.aci.dspro.pcap.disservice.Controller(buf)
                else -> Empty(buf)
            }

            return DisServiceMessage(targetNodeId, senderNodeId, sessionId, body, type)
        }
    }

    override fun toString(): String = "DisService Message $type from <$senderNodeId> for <$targetNodeId> in session <$sessionId>"
    fun isEmpty(): Boolean = body is Empty
    override fun getMessage(protocol: Protocol): Message = when (protocol) {
        Protocol.DSPro -> if (isDSProMessage(this)) DSProMessage(this.body as Data) else this
        else -> this
    }

    override fun getType() = Protocol.DisService
}