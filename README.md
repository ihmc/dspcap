# DSPro PCAP
[![Release](https://jitpack.io/v/ihmc/Repo.svg)](https://jitpack.io/#ihmc/dspcap)
Kotlin/Java library to parse [DSPro](https://github.com/ihmc/nomads/wiki/DSPro-Overview) pcap traces.
Currently only NMS, DisService data messages and DSPro metadata messages are parsed completely.

## Getting Started
### Gradle
Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add the dependency
```
	dependencies {
	        compile 'com.github.ihmc:dspcap:v0.0.2-alpha'
	}
```

### Maven
Add the JitPack repository to your build file 
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
Add the dependency
```
	<dependency>
	    <groupId>com.github.ihmc</groupId>
	    <artifactId>dspcap</artifactId>
	    <version>v0.0.2-alpha</version>
	</dependency>
```
###
Alternatively, download the jar from [here](https://sharebox.ihmc.us/s/XSbW6MZ7UEZMy3i)

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
