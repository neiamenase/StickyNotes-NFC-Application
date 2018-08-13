package com.nfc.staticClass;

public final class Util {
        private final static char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7',
                        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

        private Util() {
        }

        public static byte[] toBytes(int a) {
                return new byte[] { (byte) (0x000000ff & (a >>> 24)),
                                (byte) (0x000000ff & (a >>> 16)),
                                (byte) (0x000000ff & (a >>> 8)), (byte) (0x000000ff & (a)) };
        }

        public static int toInt(byte[] b, int s, int n) {
                int ret = 0;

                final int e = s + n;
                for (int i = s; i < e; ++i) {
                        ret <<= 8;
                        ret |= b[i] & 0xFF;
                }
                return ret;
        }

        public static int toIntR(byte[] b, int s, int n) {
                int ret = 0;

                for (int i = s; (i >= 0 && n > 0); --i, --n) {
                        ret <<= 8;
                        ret |= b[i] & 0xFF;
                }
                return ret;
        }

        public static int toInt(byte... b) {
                int ret = 0;
                for (final byte a : b) {
                        ret <<= 8;
                        ret |= a & 0xFF;
                }
                return ret;
        }

        public static String toHexString(byte[] d, int s, int n) {
                final char[] ret = new char[n * 2];
                final int e = s + n;

                int x = 0;
                for (int i = s; i < e; ++i) {
                        final byte v = d[i];
                        ret[x++] = HEX[0x0F & (v >> 4)];
                        ret[x++] = HEX[0x0F & v];
                }
                return new String(ret);
        }

        public static String toHexStringR(byte[] d, int s, int n) {
                final char[] ret = new char[n * 2];

                int x = 0;
                for (int i = s + n - 1; i >= s; --i) {
                        final byte v = d[i];
                        ret[x++] = HEX[0x0F & (v >> 4)];
                        ret[x++] = HEX[0x0F & v];
                }
                return new String(ret);
        }

        public static int parseInt(String txt, int radix, int def) {
                int ret;
                try {
                        ret = Integer.valueOf(txt, radix);
                } catch (Exception e) {
                        ret = def;
                }

                return ret;
        }
       
        public static String toAmountString(float value) {
                return String.format("%.2f", value);
        }
        
        static final String HEXES = "0123456789ABCDEF";
  	  public static String getHex( byte [] raw ) {
  	    if ( raw == null ) {
  	      return null;
  	    }
  	    final StringBuilder hex = new StringBuilder( 2 * raw.length );
  	    for ( final byte b : raw ) {
  	      hex.append(HEXES.charAt((b & 0xF0) >> 4))
  	         .append(HEXES.charAt((b & 0x0F)));
  	    }
  	    return hex.toString();
  	  }
  	
  	  public int convertirOctetEnEntier(byte[] b){    
  		    int MASK = 0xFF;
  		    int result = 0;   
  		        result = b[0] & MASK;
  		        result = result + ((b[1] & MASK) << 8);
  		        result = result + ((b[2] & MASK) << 16);
  		        result = result + ((b[3] & MASK) << 24);            
  		    return result;
  		}
}
