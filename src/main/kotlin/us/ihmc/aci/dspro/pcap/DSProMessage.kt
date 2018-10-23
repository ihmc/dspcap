package us.ihmc.aci.dspro.pcap

import us.ihmc.aci.dspro.pcap.disservice.Data
import java.text.ParseException

/**
 * Created by gbenincasa on 10/31/17.
 */

data class DSProMessage(private val ds: Data) : Message {

    val isChunk: Boolean
    val isMetadata: Boolean
    val body: Body
    val len: Int

    init {
        if (!ds.msgInfo.isDSProMessage()) {
            throw ParseException("Could not parse as " + DSProMessage::class.simpleName, ds.data.readerIndex)
        }
        if (!ds.msgInfo.isComplete()) {
            throw ParseException("Could not parse DisService fragment as "
                    + DSProMessage::class.simpleName, ds.data.readerIndex)
        }
        len = ds.msgInfo.fragmentLength.toInt()
        var offset = 0
        isChunk = (ds.msgInfo.group.endsWith("[od]") || ds.msgInfo.group.endsWith("cu"));
        isMetadata = if (isChunk) false else {
            offset = 1
            ds.data.readUnsignedByte().toInt() == 1
        }
        val buf = ds.data.readBytes(len - offset)
        body = if (isMetadata) us.ihmc.aci.dspro.pcap.dspro.Metadata(buf) else Empty(ds.data)
    }

    override fun toString(): String {
        fun getType(): String {
            return when {
                isChunk -> "Chunked"
                isMetadata -> "Metadata"
                else -> "Data"
            }
        }
        var msg = "DSPro ${getType()} Message.\n"
        if (isMetadata) {
            msg += body
        }
        return msg
    }

    fun isEmpty(): Boolean = body is Empty
    override fun getMessage(protocol: Protocol): Message = this

    override fun getType() = Protocol.DSPro
}
