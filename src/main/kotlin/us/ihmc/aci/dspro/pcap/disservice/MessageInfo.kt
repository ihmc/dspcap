package us.ihmc.aci.dspro.pcap.disservice

import io.pkts.buffer.Buffer
import us.ihmc.aci.dspro.pcap.readString

/**
 * Created by gbenincasa on 10/31/17.
 */
class MessageInfo(private var buf: Buffer) {

    val group: String
    val publisher: String
    val sequenceId: Long
    val chunkId: Short
    val objectId: String
    val instanceId: String
    val referredObjectId: String
    val annotationMetadata: Buffer
    val tag: Int
    val mimeType: String
    val totalLength: Long
    val fragmentOffset: Long
    val fragmentLength: Long
    val historyWindow: Int
    val priority: Short
    val acknowledgment: Boolean

    init {
        val isChunk = buf.readUnsignedByte().toInt() == 1
        group = readString(buf, buf.readUnsignedShort())
        publisher = readString(buf, buf.readUnsignedShort())
        sequenceId = buf.readUnsignedInt()
        chunkId = buf.readUnsignedByte()
        objectId = readString(buf, buf.readUnsignedShort())
        instanceId = readString(buf, buf.readUnsignedShort())
        referredObjectId = readString(buf, buf.readUnsignedShort())
        annotationMetadata = buf.readBytes(buf.readUnsignedInt().toInt())
        tag = buf.readUnsignedShort()
        mimeType = readString(buf, buf.readUnsignedShort())
        totalLength = buf.readUnsignedInt()
        fragmentOffset = buf.readUnsignedInt()
        fragmentLength = buf.readUnsignedInt()
        historyWindow = buf.readUnsignedShort()
        priority = buf.readUnsignedByte()
        buf.readBytes(8);   // Expiration
        acknowledgment = buf.readUnsignedByte().toInt() == 1
    }

    fun isComplete(): Boolean = fragmentLength == totalLength
    fun isDSProMessage(): Boolean = group.startsWith("DSPro")
}