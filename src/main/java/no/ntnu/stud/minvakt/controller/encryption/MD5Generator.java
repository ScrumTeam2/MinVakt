package no.ntnu.stud.minvakt.controller.encryption;

import java.math.BigInteger;
import java.security.MessageDigest;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
public class MD5Generator {
    /*public static void main(String[] args) {
        String abc ="testMd5";
        System.out.println(md5generate(abc));
    }*/

    /*Test
    @Test
    public void testPrintMessage() {
       System.out.println("Inside testPrintMessage()");
       String hashed = MD5Generator("hashthispass");
       assertEquals(32, hashed.size());
    }
     */
    public static String md5generate(String text){
        String hashMd5 = null;
        String hashtext = null;
        try
        {
            String plain = text;
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(plain.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            hashtext = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashtext.length() < 32 ){
                hashtext = "0"+hashtext;
            }
        } catch (Exception e1)
        {
            // TODO: handle exception
            System.out.println("Could not md5 hash password");
        }
        return hashtext;
    }
}