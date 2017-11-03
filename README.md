# DSPro PCAP
Kotlin/Java library to parse [DSPro](https://github.com/ihmc/nomads/wiki/DSPro-Overview) pcap traces.
Currently only NMS, DisService data messages and DSPro metadata messages are parsed completely.

## Build
Do a `gradlew.bat fatjar` from the `dspcap` directory.  This will create a single .jar file containing
all the dependencies in `build\libs\dspcap-all-1.0.jar`.

## Example: parse packet
```
byte[] udpPacketPayload = ...;
Message msg = NMSMessage(udpPacketPayload)
  .getMessage(Protocol.DisService)
  .getMessage(Protocol.DSPro);
System.out.println(msg);
```
Also look at the [demo](src/main/java/us/ihmc/aci/dspro/pcap/demo/JavaLibraryDemo.java)
