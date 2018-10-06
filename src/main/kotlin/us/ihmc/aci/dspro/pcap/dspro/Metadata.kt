package us.ihmc.aci.dspro.pcap.dspro

import io.pkts.buffer.Buffer
import us.ihmc.aci.dspro.pcap.Body
import us.ihmc.aci.dspro.pcap.zdecompress

/**
 * Created by gbenincasa on 10/31/17.
 */
data class Metadata(private val buf: Buffer) : Body {
    val metadata: String

    init {
        val uncompressedStringLength = buf.readUnsignedInt()
        metadata = zdecompress(buf.readBytes(buf.readableBytes), uncompressedStringLength.toInt())
    }

    override fun toString(): String = metadata
}

enum class MetadataElement {

    Application_Metadata,
    Application_Metadata_Format,
    Message_ID,

    Refers_To,

    Referred_Data_Object_Id,
    Referred_Data_Instance_Id,

    External_Referred_Cached_Data_URL,

    ComputedVOI,

    Prev_Msg_ID,
    Node_Type,
    Data_Content,
    Classification,
    Data_Format,
    Left_Upper_Latitude,
    Right_Lower_Latitude,
    Left_Upper_Longitude,
    Right_Lower_Longitude,
    Description,
    Pedigree,
    Importance,
    Location,
    Receiver_Time_Stamp,
    Source,
    Source_Reliability,
    Source_Time_Stamp,
    Expiration_Time,
    Relevant_Missions,
    Target_ID,
    Target_Role,
    Target_Team,
    Track_ID,
    Track_Action
}
