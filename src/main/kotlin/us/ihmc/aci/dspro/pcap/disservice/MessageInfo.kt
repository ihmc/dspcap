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
    val annotationMetadata: ByteArray
    val tag: Int
    val clientId: Int
    val clientType: Short
    val mimeType: String
    val totalLength: Long
    val fragmentOffset: Long
    val fragmentLength: Long
    val historyWindow: Int
    val priority: Short
    val acknowledgment: Boolean

    val referredObject: ByteArray
    val totalNumberOfChunks: Short

    init {
        val isChunk = buf.readUnsignedByte().toInt() == 1
        group = readString(buf, buf.readUnsignedShort())
        publisher = readString(buf, buf.readUnsignedShort())
        sequenceId = buf.readUnsignedInt()
        chunkId = buf.readUnsignedByte()
        objectId = readString(buf, buf.readUnsignedShort())
        instanceId = readString(buf, buf.readUnsignedShort())
        referredObjectId = readString(buf, buf.readUnsignedShort())
        var annotationMetadataLen = buf.readUnsignedInt()
        annotationMetadata = if (annotationMetadataLen > 0) buf.readBytes(annotationMetadataLen.toInt()).array
                    else ByteArray(0)
        tag = buf.readUnsignedShort()
        clientId = buf.readUnsignedShort()
        clientType = buf.readUnsignedByte()
        mimeType = readString(buf, buf.readUnsignedShort())
        totalLength = buf.readUnsignedInt()
        fragmentOffset = buf.readUnsignedInt()
        fragmentLength = buf.readUnsignedInt()
        historyWindow = buf.readUnsignedShort()
        priority = buf.readUnsignedByte()
        buf.readBytes(8);   // Expiration
        acknowledgment = buf.readUnsignedByte().toInt() == 1

        if (isChunk) {
            referredObject = ByteArray(0)
            totalNumberOfChunks = buf.readUnsignedByte()
        } else {
            referredObject = buf.readBytes(buf.readUnsignedShort()).array
            buf.readUnsignedByte()
            buf.readUnsignedInt()
            totalNumberOfChunks = 0
        }
    }

    fun isComplete(): Boolean = fragmentLength == totalLength
    fun isDSProMessage(): Boolean = group.startsWith("DSPro")
}