package us.ihmc.aci.dspro.pcap;

/**
 * Created by gbenincasa on 11/1/17.
 */
public class ByteSwapper {

    /**
     * Byte swap a single short value.
     *
     * @param value  Value to byte swap.
     * @return       Byte swapped representation.
     */
    public static int swapUnsignedShort (int value)
    {
        int b1 = (value >>  0) & 0xff;
        int b2 = (value >>  8) & 0xff;
        int b3 = (value >> 16) & 0xff;
        int b4 = (value >> 24) & 0xff;

        int i = b4 << 24 | b3 << 16 | b1 << 8 | b2 << 0;
        return i;
    }
}
