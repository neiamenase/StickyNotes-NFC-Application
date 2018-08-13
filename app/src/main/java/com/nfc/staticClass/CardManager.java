package com.nfc.staticClass;


import android.content.IntentFilter;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Parcelable;

public final class CardManager {
        private static final String SP = "";

        public static String[][] TECHLISTS;
        public static IntentFilter[] FILTERS;

        static {
                try {
                        TECHLISTS = new String[][] { { IsoDep.class.getName() },
                                        { NfcV.class.getName() }, { NfcF.class.getName() }, };

                        FILTERS = new IntentFilter[] { new IntentFilter(
                                        NfcAdapter.ACTION_TECH_DISCOVERED, "*/*") };
                } catch (Exception e) {
                }
        }

        public static String buildResult(String n, String i, String x) {
                if (n == null)
                        return null;

                final StringBuilder s = new StringBuilder();

                s.append("Card Type: "+n).append("\n");

                if (i != null)
                        s.append(SP).append(i);

             

                if (x != null)
                        s.append(SP).append(x);

                return s.append("").toString();
        }
        public static String load(Parcelable parcelable) {
                final Tag tag = (Tag) parcelable;

                final IsoDep isodep = IsoDep.get(tag); // alternative
                final NfcF nfcf = NfcF.get(tag);
                final MifareClassic m = MifareClassic.get(tag);
                if(m != null){
                			
                }
                final MifareUltralight m1 = MifareUltralight.get(tag);
                final Ndef ndef = Ndef.get(tag);
                final NdefFormatable nf = NdefFormatable.get(tag);
                final NfcA nfca = NfcA.get(tag);
                final NfcB nfcb = NfcB.get(tag);
               // final NfcBarcode nb = NfcBarcode.get(tag);
                
                if (nfcf != null) { 
                        return OctopusCard.load(nfcf);
                }

                return null;
        }
}