package us.ihmc.aci.dspro.pcap

import io.pkts.Pcap
import io.pkts.buffer.Buffer
import io.pkts.buffer.InputStreamBuffer
import io.pkts.packet.UDPPacket
import kotliquery.*
import java.io.ByteArrayInputStream
import java.io.File
import java.sql.DriverManager

abstract class DSProDataReader(protected val filename: String) {
    
    abstract fun parse()
}

class PcapReader(filename: String) : DSProDataReader(filename) {
    
    override fun parse() {
        Pcap.openStream(filename).loop { packet ->
            if (packet.hasProtocol(io.pkts.protocol.Protocol.UDP)) {
                var udp = packet.getPacket(io.pkts.protocol.Protocol.UDP) as UDPPacket
                if (udp.destinationPort == 6669 && !udp.payload.isEmpty) {
                    var msg = NMSMessage(udp.payload)
                            .getMessage(Protocol.DisService)
                            .getMessage(Protocol.DSPro)
                    println("$msg")
                }
            }
            true
        }
    }
}

class SqliteReader(filename: String)  : DSProDataReader(filename){
    private val conn = DriverManager.getConnection("jdbc:sqlite:$filename")
    private val session = Session(Connection(conn), false)

    data class Message(val sourceTimestamp: Long, val data: Buffer)

    override fun parse() {
        val timestamp = "arrivalTimeStamp"
        val data = "data"
        val query = """
            SELECT arrivalTimeStamp, $data
            FROM DisServiceDataCache
            ORDER BY $timestamp
        """
        val results = queryOf(query).map {Message(
                it.long(1),
                InputStreamBuffer(ByteArrayInputStream(it.bytes(2)))
            )
        }.asList
        session.run(results).forEach {
            try {
                val msg = DisServiceMessage(it.data).getMessage(Protocol.DSPro)
                println(msg)
            }
            catch (ex: Exception) {
                println(ex.message)
            }
        }
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar <capture.pcap>")
    }
    for (filename in args) {
        val parser= when(File(filename).extension.toLowerCase()) {
            "sqlite" -> SqliteReader(filename)
            else -> PcapReader(filename)
        }
        println("Reading, $filename")
        parser.parse()
    }
}
