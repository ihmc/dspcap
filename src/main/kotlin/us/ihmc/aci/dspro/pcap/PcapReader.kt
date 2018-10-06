package us.ihmc.aci.dspro.pcap

import io.pkts.Pcap
import io.pkts.buffer.Buffer
import io.pkts.buffer.InputStreamBuffer
import io.pkts.packet.UDPPacket
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import kotliquery.*
import us.ihmc.aci.dspro.pcap.disservice.Data
import us.ihmc.aci.dspro.pcap.disservice.MessageInfo
import java.io.ByteArrayInputStream
import java.io.File
import java.sql.DriverManager

abstract class DSProDataReader(protected val filename: String) : ObservableOnSubscribe<Message>

class PcapReader(filename: String) : DSProDataReader(filename) {

    override fun subscribe(emitter: ObservableEmitter<Message>) {
        Pcap.openStream(filename).loop { packet ->
            if (packet.hasProtocol(io.pkts.protocol.Protocol.UDP)) {
                var udp = packet.getPacket(io.pkts.protocol.Protocol.UDP) as UDPPacket
                if (udp.destinationPort == 6669 && !udp.payload.isEmpty) {

                    try {
                        emitter.onNext(NMSMessage(udp.payload))
                    }
                    catch (ex: Exception) {
                        emitter.onError(ex)
                    }
                }
            }
            true
        }
        emitter.onComplete()
    }
}

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

internal fun getParser(filename: String): ObservableOnSubscribe<Message> {
    val extension = File(filename).extension.toLowerCase()
    return when (extension) {
        "sqlite" -> SqliteReader(filename)
        "pcap" -> PcapReader(filename)
        else -> throw NotImplementedError("Parser for $extension file not supported")
    }
}

fun getObserver(filename: String): Observable<Message> = Observable.create(getParser(filename))

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar <capture.pcap>")
    }
    var i = 0
    for (filename in args) {
        println("Reading, $filename")
        getObserver(filename)
                .map { it.getMessage(Protocol.DisService).getMessage(Protocol.DSPro) }
                .subscribe {
            println("${++i}\t$it")
        }
    }
}
