package us.ihmc.aci.dspro.pcap.dspro

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import io.pkts.buffer.Buffer
import org.slf4j.LoggerFactory
import us.ihmc.aci.dspro.pcap.Body
import us.ihmc.aci.dspro.pcap.zdecompress
import java.util.*

/**
 * Created by gbenincasa on 10/31/17.
 */
data class Metadata(private val buf: Buffer) : Body {
    val metadata: JsonObject

    companion object {
        val LOGGER = LoggerFactory.getLogger(javaClass)
    }

    init {
        val uncompressedStringLength = buf.readUnsignedInt()
        val sMetadata = zdecompress(buf.readBytes(buf.readableBytes), uncompressedStringLength.toInt())
        metadata = Parser().parse(StringBuilder(sMetadata)) as JsonObject
        if(metadata["Application_Metadata_Format"].toString().toLowerCase().endsWith("base64")) {
            try {
                val sAppMetadata = String(Base64.getDecoder()
                        .decode(metadata["Application_Metadata"].toString()), Charsets.UTF_8)
                metadata["Application_Metadata"] = Parser().parse(StringBuilder(sAppMetadata)) as JsonObject
            }
            catch(e: Exception) {
                LOGGER.warn("${e.message} ${metadata["Application_Metadata"].toString()}")
            }
        }
    }

    override fun toString() = metadata.toJsonString(prettyPrint = true)
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
