package us.ihmc.aci.dspro.sqlite

import io.pkts.buffer.InputStreamBuffer
import io.reactivex.ObservableEmitter
import kotliquery.Connection
import kotliquery.Session
import kotliquery.queryOf
import us.ihmc.aci.dspro.pcap.DSProDataReader
import us.ihmc.aci.dspro.pcap.DisServiceMessage
import us.ihmc.aci.dspro.pcap.Message
import us.ihmc.aci.dspro.pcap.disservice.Data
import us.ihmc.aci.dspro.pcap.disservice.MessageInfo
import java.io.ByteArrayInputStream
import java.sql.DriverManager

class SqliteReader(filename: String)  : DSProDataReader(filename) {

    private val conn = DriverManager.getConnection("jdbc:sqlite:$filename")
    private val session = Session(Connection(conn), false)

    override fun subscribe(emitter: ObservableEmitter<Message>) {
        val timestamp = "arrivalTimeStamp"
        val data = "data"
        val query = """
            SELECT groupName, senderNodeId, messageSeqId, chunkId, objectId, instanceId,
                   totalMsgLength, fragmentLength, totalNumberOfChunks, $data
            FROM DisServiceDataCache
            ORDER BY $timestamp
        """
        val results = queryOf(query).map {

            val objectId = it.stringOrNull(5)
            val instanceId = it.stringOrNull(6)
            val msgInfo = MessageInfo(it.string(1), it.string(2), it.long(3), it.short(4),
                    objectId ?: "", instanceId ?: "", it.long(7), it.long(8),
                    it.short(9))
            val buf = it.bytes(10)

            Data(msgInfo, InputStreamBuffer(ByteArrayInputStream(buf)))

        }.asList

        session.run(results).forEach {
            try {
                emitter.onNext(DisServiceMessage.getDisServiceDataMessage(it))
            }
            catch (ex: Exception) {
                emitter.onError(ex)
            }
        }
        emitter.onComplete()
    }
}

