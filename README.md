# DSPro PCAP
Java Library to parse DSPro pcap traces.

## Example: parse packet
```
byte[] udpPacketPayload = ...;
Message msg = NMSMessage(udpPacketPayload)
  .getMessage(Protocol.DisService)
  .getMessage(Protocol.DSPro);
System.out.println(msg);
```
