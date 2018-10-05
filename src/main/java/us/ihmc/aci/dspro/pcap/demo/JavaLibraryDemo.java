package us.ihmc.aci.dspro.pcap.demo;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.packet.Packet;
import io.pkts.packet.UDPPacket;
import us.ihmc.aci.dspro.pcap.Message;
import us.ihmc.aci.dspro.pcap.NMSMessage;
import us.ihmc.aci.dspro.pcap.Protocol;

import java.io.FileInputStream;
import java.io.IOException;

class JavaLibraryDemo
{
    private byte[] udpPacketPayload;

    private JavaLibraryDemo(byte[] udpPacketPayload) {
        this.udpPacketPayload = udpPacketPayload;
    }

    private void parse() {
        boolean hasChecksum = true; // Set this to false for traces prior to September
                                    // 2017 (set it to false for AB17 traces)
        Message msg = new NMSMessage(udpPacketPayload, hasChecksum)
                .getMessage(Protocol.DisService)
                .getMessage(Protocol.DSPro);
        System.out.println(msg);
    }

    public static void main(String[] args) {
        if(args.length <= 0) {
            System.err.println("Usage: java JavaLibraryDemo <capture.pcap>");
            System.exit(1);
        }
        try (FileInputStream fis = new FileInputStream(args[0])) {
            final Pcap pcap = Pcap.openStream(fis);
            pcap.loop(new PacketHandler() {
                @Override
                public boolean nextPacket(final Packet packet) throws IOException {
                    if (packet.hasProtocol(io.pkts.protocol.Protocol.UDP)) {
                        UDPPacket udp = (UDPPacket) packet.getPacket(io.pkts.protocol.Protocol.UDP);
                        if ((udp.getDestinationPort() == 6669) && (!udp.getPayload().isEmpty())) {
                            new JavaLibraryDemo(udp.getPayload().getArray()).parse();
                        }
                    }
                    return true;
                }
            });
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
