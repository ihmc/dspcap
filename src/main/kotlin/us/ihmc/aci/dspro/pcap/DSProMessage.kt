package us.ihmc.aci.dspro.pcap

import us.ihmc.aci.dspro.pcap.disservice.Data
import java.text.ParseException

/**
 * Created by gbenincasa on 10/31/17.
 */

data class DSProMessage(private var ds: Data) : Message {

    val isChunk: Boolean
    val isMetadata: Boolean
    val body: Body

    init {
        if (!ds.msgInfo.isDSProMessage()) {
            throw ParseException("Could not parse as " + DSProMessage::class.simpleName, ds.data.readerIndex)
        }
        if (!ds.msgInfo.isComplete()) {
            throw ParseException("Could not parse DisService fragment as "
                    + DSProMessage::class.simpleName, ds.data.readerIndex)
        }
        var offset = 0
        isChunk = (ds.msgInfo.group.endsWith("[od]") || ds.msgInfo.group.endsWith("cu"));
        isMetadata = if (isChunk) false else {
            offset = 1
            ds.data.readUnsignedByte().toInt() == 1
        }
        val buf = ds.data.readBytes(ds.msgInfo.fragmentLength.toInt() - offset)
        body = if (isMetadata) us.ihmc.aci.dspro.pcap.dspro.Metadata(buf) else Empty(ds.data)
    }

    fun isEmpty(): Boolean = body is Empty
    override fun getMessage(protocol: Protocol): Message = this
}
