package com.nfc.staticClass;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;


import android.content.res.Resources;
import android.nfc.tech.NfcF;

import android.util.Log;

public final class OctopusCard {
    public static final byte[] EMPTY = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        (byte) 0xFF, (byte) 0xFF };
        private static final int SYS_SZT = 0x8005;
        private static final int SRV_SZT = 0x0118;
        private static final int SYS_OCTOPUS = 0x8008;
        private static final int SRV_OCTOPUS = 0x0117;
        


        public static String load(NfcF tech) {
        	
        	
        	
        	
             byte[] mdata;
             byte[] idm;//
             byte[] pmm;//
             byte[] code;
             byte[] mbytes;
             final int system = toInt(tech.getSystemCode()); 
        		
        		//final FeliCa.Tag tag = new FeliCa.Tag(tech);
                
                /*--------------------------------------------------------------*/
                // check card system
                /*--------------------------------------------------------------*/
                
                
                //final int system = toInt(tech.getSystemCode()); // for veiwing type
                
                
                if (system == SYS_OCTOPUS){
                	
                	code = new byte[] { (byte) (SRV_OCTOPUS & 0xFF), (byte) (SRV_OCTOPUS >> 8) };
                	
                }else if (system == SYS_SZT){
                	
                	code = new byte[] { (byte) (SRV_SZT & 0xFF), (byte) (SRV_SZT >> 8) };
                	
                }else{
                        return null;
                }
                
                try {
                	
                        tech.connect();    //Connect
                        
                } catch (Exception e) {
                	
                	Log.v("Connection problem", "NOT Connected");
                	
                }
                
               // service.idm = new AssistC.IDm(tech.getTag().getId()); // in the data

                idm = tech.getTag().getId();
                pmm = tech.getManufacturer();
                mdata = new byte[] { (byte) (SRV_OCTOPUS & 0xFF), (byte) (SRV_OCTOPUS >> 8)};

                
                /*--------------------------------------------------------------*/
                // read service data without encryption
                /*--------------------------------------------------------------*/
                
                final float[] data = new float[] { 0, 0, 0 };
                final int N = data.length; // N =3
                
                int p = 0;
                for (byte i = 0; p < N; ++i) {
                	
                		// code (byte alreadly)
                	
                        try {
                        	
                        	byte thisdata[] = new byte[] {
                                    (byte) 0x01, (byte) code[0], (byte) code[1], (byte) 0x01,
                                    (byte) 0x80, i
                                    }; 
                        	int a = idm.length;
                        	int b = thisdata.length;
                        	int length = idm.length + thisdata.length + 2;
                        	
                        	ByteBuffer buff = ByteBuffer.allocate(length);
                            byte length2 = (byte) length;
                            
                            
                            
                            if (idm != null) {
                                    buff.put(length2).put(CMD_READ_WO_ENCRYPTION).put(idm).put(thisdata);
                            } else {
                                    buff.put(length2).put(CMD_READ_WO_ENCRYPTION).put(thisdata);
                            }
                            byte rsp[] = tech.transceive(buff.array());
                            byte target[];
                            
                            if(rsp == null || rsp.length < 12){ // RR's super + condition checking
                            	target = EMPTY ;
                            }else{
                            	target = rsp;
                            }
                            
                            //------------------------------------------------------//
                            
                            
                            
                            byte ncode;
                            byte[] nidm;
                            int nlength;
                            
                            
                            if (target != null && target.length >= 10) {
                                nlength = target[0] & 0xff;
                                ncode = target[1];
                                nidm = Arrays.copyOfRange(target, 2, 10);
                                mdata = target;
                            } else {
                                nlength = 0;
                                ncode = 0;
                                nidm = null;
                                mdata = new byte[] {};
                        }
                          

                            byte[] blockData;
                                                    
                            
                            if (!(mdata[10] == STA1_NORMAL) ){
                            	Log.v("=/=","!!Abnormal!!!");
                            	break;
                            }
                            if (mdata[10] == STA1_NORMAL && getBlockCount(mdata) > 0) {
                                    blockData = Arrays.copyOfRange(mdata, 13, mdata.length);
                            } else {
                                    blockData = EMPTY;
                            }  
                            
                            
                            // RR+R ends

                            
                            data[p++] = (Util.toInt(blockData, 0, 4) - 350) / 10.0f;
                        	
                            
                        	
                        		 /*
									k =  new ReadResponse(tech.transceive(new AssistC(bytes).Command(CMD_READ_WO_ENCRYPTION, service.idm, 
                        			tech, new byte[] {(byte) 0x01, (byte) bytes[0], (byte) bytes[1], (byte) 0x01,
                        			(byte) 0x80, i})));
                        		 */
                        	
							} catch (Exception e) {
								
								Log.v("***", ""+e.toString());
								
							}
                        
                        	
                       // if (!k.isOkey())
                         //       break;
                        
                        
                      //  data[p++] = (Util.toInt(k.getBlockData(), 0, 4) - 350) / 10.0f;
                        
                }

                
                
                
                try {
					tech.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                /*--------------------------------------------------------------*/
                // build result string
                /*--------------------------------------------------------------*/

                final String name = parseName(system);
               // final String info = parseInfo(idm, pmm);
                final String hist = parseLog(null);
                final String cash = parseBalance(data, p);

                return CardManager.buildResult(name, cash, hist);
        }

        private static String parseName(int system) {
                /*
				if (system == SYS_OCTOPUS)
                        return res.getString(R.string.name_octopuscard);
				if (system == SYS_SZT)
                        return res.getString(R.string.name_szt_f);
				return null;
				
				
                 */
        	if (system == SYS_OCTOPUS)
        		return "Octopus";
        	
        	return null;
        }

        

        public static int toInt(byte[] data) {
                return 0x0000FFFF & ((data[0] << 8) | (0x000000FF & data[1]));
        }
        
        
        private static String parseInfo(byte[] idm, byte[] pmm) {
                final StringBuilder r = new StringBuilder();
                r.append("").append(" ID ").append(":")
                                .append(idm.toString()).append("\n");
                r.append("").append(" PMm ").append(':')
                                .append(pmm.toString()).append("\n");

                return r.toString();
        }

        private static String parseBalance(float[] value, int count) {
                if (count < 1)
                        return null;

                final StringBuilder r = new StringBuilder();
              //  final String s = res.getString(R.string.lab_balance);
               // final String c = res.getString(R.string.lab_cur_hkd);

                r.append("Octopus Balance").append(": ").append(" $");

                for (int i = 0; i < count; ++i)
                        r.append(Util.toAmountString(value[i])).append(' ');

                return r.toString();
        }

        private static String parseLog(byte[] data) {
                return null;
        }
        
        
        public static int getBlockCount(byte[] data) {
            return (data.length > 12) ? (0xFF & data[12]) : 0;
    }
        
		
        // polling
        public static final byte CMD_POLLING = 0x00;
        public static final byte RSP_POLLING = 0x01;

        // request service
        public static final byte CMD_REQUEST_SERVICE = 0x02;
        public static final byte RSP_REQUEST_SERVICE = 0x03;

        // request RESPONSE
        public static final byte CMD_REQUEST_RESPONSE = 0x04;
        public static final byte RSP_REQUEST_RESPONSE = 0x05;

        // read without encryption
        public static final byte CMD_READ_WO_ENCRYPTION = 0x06;
        public static final byte RSP_READ_WO_ENCRYPTION = 0x07;

        // write without encryption
        public static final byte CMD_WRITE_WO_ENCRYPTION = 0x08;
        public static final byte RSP_WRITE_WO_ENCRYPTION = 0x09;

        // search service code
        public static final byte CMD_SEARCH_SERVICECODE = 0x0a;
        public static final byte RSP_SEARCH_SERVICECODE = 0x0b;

        // request system code
        public static final byte CMD_REQUEST_SYSTEMCODE = 0x0c;
        public static final byte RSP_REQUEST_SYSTEMCODE = 0x0d;

        // authentication 1
        public static final byte CMD_AUTHENTICATION1 = 0x10;
        public static final byte RSP_AUTHENTICATION1 = 0x11;

        // authentication 2
        public static final byte CMD_AUTHENTICATION2 = 0x12;
        public static final byte RSP_AUTHENTICATION2 = 0x13;

        // read
        public static final byte CMD_READ = 0x14;
        public static final byte RSP_READ = 0x15;

        // write
        public static final byte CMD_WRITE = 0x16;
        public static final byte RSP_WRITE = 0x17;

        public static final int SYS_ANY = 0xffff;
        public static final int SYS_FELICA_LITE = 0x88b4;
        public static final int SYS_COMMON = 0xfe00;

        public static final int SRV_FELICA_LITE_READONLY = 0x0b00;
        public static final int SRV_FELICA_LITE_READWRITE = 0x0900;

        public static final int STA1_NORMAL = 0x00;
        public static final int STA1_ERROR = 0xff;

        public static final int STA2_NORMAL = 0x00;
        public static final int STA2_ERROR_LENGTH = 0x01;
        public static final int STA2_ERROR_FLOWN = 0x02;
        public static final int STA2_ERROR_MEMORY = 0x70;
        public static final int STA2_ERROR_WRITELIMIT = 0x71;
}
