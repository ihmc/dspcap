package us.ihmc.aci.dspro.pcap

import io.pkts.buffer.Buffer

/**
 * Created by gbenincasa on 10/31/17.
 */
interface Body

class Empty(private var buf: Buffer) : Body