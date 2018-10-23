package us.ihmc.aci.dspro.pcap

/**
 * Created by gbenincasa on 11/1/17.
 */
interface Message {

    /**
     * Check whether this packet contains a particular protocol. This will cause
     * the packet to examine all the containing packets to check whether they
     * are indeed the protocol the user asked for, in which case a message of the
     * requested protocol is returned.
     * The message itself will be returned otherwise.
     */
    fun getMessage(protocol: Protocol): Message
    fun getType(): Protocol
}