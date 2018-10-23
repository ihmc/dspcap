package us.ihmc.aci.dspro.pcap

import io.pkts.Pcap
import io.pkts.packet.UDPPacket
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe

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

