package us.ihmc.aci.dspro.pcap.disservice

import io.pkts.buffer.Buffer
import us.ihmc.aci.dspro.pcap.readString

/**
 * Created by gbenincasa on 10/31/17.
 */
class MessageInfo(
        val group: String,
        val publisher: String,
        val sequenceId: Long,
        val chunkId: Short,
        val objectId: String,
        val instanceId: String,
        val referredObjectId: String,
        val annotationMetadata: ByteArray,
        val tag: Int,
        val clientId: Int,
        val clientType: Short,
        val mimeType: String,
        val totalLength: Long,
        val fragmentOffset: Long,
        val fragmentLength: Long,
        val historyWindow: Int,
        val priority: Short,
        val acknowledgment: Boolean,

        val referredObject: ByteArray,
        val totalNumberOfChunks: Short) {

    public constructor(group: String, publisher: String, sequenceId: Long, chunkId: Short, objectId: String, instanceId: String,
                totalLength: Long, fragmentLength: Long, totalNumberOfChunks: Short)

            : this (group, publisher,sequenceId, chunkId, objectId, instanceId, "", ByteArray(0),
            0, 0, 0.toShort(), "", totalLength, 0, fragmentLength, 0,
            0.toShort(), false, ByteArray(0), totalNumberOfChunks)

    companion object {
        fun getMessageInfo(buf: Buffer): MessageInfo {
            val isChunk = buf.readUnsignedByte().toInt() == 1
            val group = readString(buf, buf.readUnsignedShort())
            val publisher = readString(buf, buf.readUnsignedShort())
            val sequenceId = buf.readUnsignedInt()
            val chunkId = buf.readUnsignedByte()
            val objectId = readString(buf, buf.readUnsignedShort())
            val instanceId = readString(buf, buf.readUnsignedShort())
            val referredObjectId = readString(buf, buf.readUnsignedShort())
            var annotationMetadataLen = buf.readUnsignedInt()
            val annotationMetadata = if (annotationMetadataLen > 0) buf.readBytes(annotationMetadataLen.toInt()).array
            else ByteArray(0)
            val tag = buf.readUnsignedShort()
            val clientId = buf.readUnsignedShort()
            val clientType = buf.readUnsignedByte()
            val mimeType = readString(buf, buf.readUnsignedShort())
            val totalLength = buf.readUnsignedInt()
            val fragmentOffset = buf.readUnsignedInt()
            val fragmentLength = buf.readUnsignedInt()
            val historyWindow = buf.readUnsignedShort()
            val priority = buf.readUnsignedByte()
            buf.readBytes(8)   // Expiration
            val acknowledgment = buf.readUnsignedByte().toInt() == 1


            val referredObjectLen = if(isChunk) 0 else buf.readUnsignedShort()
            val referredObject = if (referredObjectLen > 0) buf.readBytes(referredObjectLen).array else ByteArray(0)

            val totalNumberOfChunks = if (isChunk) {
                buf.readUnsignedByte()
            } else {
                buf.readUnsignedByte()
                buf.readUnsignedInt()
                0
            }

            return MessageInfo(group, publisher, sequenceId, chunkId, objectId, instanceId, referredObjectId,
                    annotationMetadata, tag, clientId, clientType, mimeType, totalLength, fragmentOffset,
                    fragmentLength, historyWindow, priority, acknowledgment, referredObject, totalNumberOfChunks)
        }
    }


    init {

    }

    fun isComplete(): Boolean = fragmentLength == totalLength
    fun isDSProMessage(): Boolean = group.startsWith("DSPro")
}