@echo off

set dbg="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8989"
java %dbg% -jar ..\build\libs\dspcap-all-1.0.jar ..\test\resources\pcap\*.pcap
