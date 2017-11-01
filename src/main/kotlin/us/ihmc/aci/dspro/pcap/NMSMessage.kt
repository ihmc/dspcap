package us.ihmc.aci.dspro.pcap

import us.ihmc.aci.dspro.pcap.ByteSwapper.swapUnsignedShort
import com.google.common.net.InetAddresses
import io.pkts.buffer.Buffer
import io.pkts.buffer.Buffers
import java.net.Inet4Address
import java.text.ParseException

/**
 * Created by gbenincasa on 10/31/17.
 */
data class NMSMessage(private var buf: Buffer) : Message {

    constructor(byteArr: ByteArray): this(Buffers.wrap(byteArr))

    enum class Version {
        V1, V2
    }

    enum class Type (val code: Short){
        SAck (0x00),
        DataMsgComplete (0x01),
        DataMsgStart (0x02),
        DataMsgInter (0x03),
        DataMsgEnd (0x04);

        companion object {
            private val map = Type.values().associateBy(Type::code)
            fun fromShort(type: Short) = map[type]
        }
    }

    val type: Char
    val version: Version
    val length: Int
    val messageType: Short
    val ipv4Src: Inet4Address
    val ipv4Dst: Inet4Address
    val sessionId: Int
    val messageId: Int
    val hopCount: Short
    val ttl: Short
    val chunkType: Type
    val reliable: Boolean
    val encrypted: Boolean
    val checksum: Int
    val queueLength: Short
    val metadata: Buffer
    val data: Buffer

    init {
        val versionAndType = buf.readUnsignedByte().toInt()
        type = (versionAndType and 0x0F).toChar()
        version = if ((versionAndType ushr 4) == 2) Version.V2 else Version.V1
        length = swapUnsignedShort(buf.readUnsignedShort())
        messageType = buf.readUnsignedByte()
        ipv4Src = InetAddresses.fromInteger(buf.readInt())
        ipv4Dst = InetAddresses.fromInteger(buf.readInt())
        sessionId = swapUnsignedShort(buf.readUnsignedShort())
        messageId = swapUnsignedShort(buf.readUnsignedShort())
        hopCount = buf.readUnsignedByte()
        ttl = buf.readUnsignedByte()
        chunkType = Type.fromShort(buf.readUnsignedByte())
                ?: throw ParseException("Could not parse chunk type", buf.readerIndex)
        val reliableAndEncrypted = buf.readUnsignedByte().toInt()
        reliable = (reliableAndEncrypted and 0x01) == 1
        encrypted = (reliableAndEncrypted and (reliableAndEncrypted shl 1)) == 1
        checksum = swapUnsignedShort(buf.readUnsignedShort())
        queueLength = if (version == Version.V2) buf.readUnsignedByte() else 0
        val metadataLen = swapUnsignedShort(buf.readUnsignedShort())
        println(java.lang.Integer.toHexString(metadataLen))
        val dataLen = swapUnsignedShort(buf.readUnsignedShort())
        println(java.lang.Integer.toHexString(dataLen))
        metadata = buf.slice(buf.readerIndex, buf.readerIndex+metadataLen)
        data = buf.slice(buf.readerIndex, buf.readerIndex+dataLen)
    }

    override fun toString(): String = "NMS Message $version: $chunkType"
    override fun getMessage(protocol: Protocol): Message = when(protocol) {
            Protocol.DisService -> if (isDisServicePacket(this)) DisServiceMessage(data) else this
            else -> this
        }

}