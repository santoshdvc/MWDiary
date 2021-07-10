package com.masterwarchief.diary;

import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.util.Base64Utils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptDecrypt {
    private static String ALGORITHM = "AES";


    public String encrypt(String valueToEnc, String key_string) throws Exception {
        String keystr=stringlength(key_string);
        byte[] keyValue= keystr.getBytes();
        Key key = generateKey(keyValue);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(valueToEnc.getBytes());
        String encryptedValue = Base64.encodeToString(encValue,Base64.DEFAULT);
        return encryptedValue;
    }

    public String decrypt(String encryptedValue, String key_string) throws Exception {
        String keystr=stringlength(key_string);
        byte[] keyValue= keystr.getBytes();
        Key key = generateKey(keyValue);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue =  Base64.decode(encryptedValue, Base64.DEFAULT);

        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;

    }

    private static Key generateKey(byte[] keyValue) throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }

    private static String stringlength(String str){
        if(str.length()<32){
            int n=str.length();
            for(int i=0; i<(32-n);i++){
                str=str+"s";
            }
        }
        return str;
    }
}
