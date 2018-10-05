# DSPro PCAP
Kotlin/Java library to parse [DSPro](https://github.com/ihmc/nomads/wiki/DSPro-Overview) pcap traces.
Currently only NMS, DisService data messages and DSPro metadata messages are parsed completely.

## Getting Started
Import the library using JitPack [![](https://jitpack.io/v/ihmc/dspcap.svg)](https://jitpack.io/#ihmc/dspcap), or by dowloanding the jar manualy from [here](https://sharebox.ihmc.us/s/XSbW6MZ7UEZMy3i)

### Example: parse packet
```
byte[] udpPacketPayload = ...;
Message msg = NMSMessage(udpPacketPayload)
  .getMessage(Protocol.DisService)
  .getMessage(Protocol.DSPro);
System.out.println(msg);
```
Also look at the [demo](src/main/java/us/ihmc/aci/dspro/pcap/demo/JavaLibraryDemo.java)

## Build
Do a `gradlew.bat fatjar` from the `dspcap` directory.  This will create a single .jar file containing
all the dependencies in `build\libs\dspcap-all-1.0.jar`.
