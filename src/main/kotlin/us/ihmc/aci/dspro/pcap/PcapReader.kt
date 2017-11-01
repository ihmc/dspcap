package us.ihmc.aci.dspro.pcap

import io.pkts.Pcap
import io.pkts.packet.UDPPacket

class PcapReader(private val filename : String) {
  fun parse() {
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

fun main(args : Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar <capture.pcap>")
    }
    else {
        var filename = args[0]
        println("Reading, $filename");
        PcapReader(filename).parse()
    }
}
