@echo off

set dbg="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8989"
java %dbg% -jar ..\build\libs\dspcap-all-1.0.jar ..\src\test\resources\pcap\*.pcap
