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
    public static short swap (short value)
    {
        int b1 = value & 0xff;
        int b2 = (value >> 8) & 0xff;

        return (short) (b1 << 8 | b2 << 0);
    }

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



    /**
     * Byte swap a single int value.
     *
     * @param value  Value to byte swap.
     * @return       Byte swapped representation.
     */
    public static int swap (int value)
    {
        int b1 = (value >>  0) & 0xff;
        int b2 = (value >>  8) & 0xff;
        int b3 = (value >> 16) & 0xff;
        int b4 = (value >> 24) & 0xff;

        return b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0;
    }



    /**
     * Byte swap a single long value.
     *
     * @param value  Value to byte swap.
     * @return       Byte swapped representation.
     */
    public static long swap (long value)
    {
        long b1 = (value >>  0) & 0xff;
        long b2 = (value >>  8) & 0xff;
        long b3 = (value >> 16) & 0xff;
        long b4 = (value >> 24) & 0xff;
        long b5 = (value >> 32) & 0xff;
        long b6 = (value >> 40) & 0xff;
        long b7 = (value >> 48) & 0xff;
        long b8 = (value >> 56) & 0xff;

        return b1 << 56 | b2 << 48 | b3 << 40 | b4 << 32 |
                b5 << 24 | b6 << 16 | b7 <<  8 | b8 <<  0;
    }
}
