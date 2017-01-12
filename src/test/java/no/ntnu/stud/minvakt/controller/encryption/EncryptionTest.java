package no.ntnu.stud.minvakt.controller.encryption;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Audun on 11.01.2017.
 */
public class EncryptionTest {
    private Encryption encryption;

    @Before
    public void setUp() {
        encryption = new Encryption();
    }

    @Test
    public void passDecodingValid() throws Exception {
        Assert.assertTrue(encryption.passDecoding("password", "oQaZgG266KjDzEkGTgXYMQ==","2oUGF8AAgobU1E3rcAtyiw=="));
    }

    @Test
    public void passDecodingInvalid() throws Exception {
        Assert.assertFalse(encryption.passDecoding("invalid", "oQaZgG266KjDzEkGTgXYMQ==","2oUGF8AAgobU1E3rcAtyiw=="));
    }

    @Test
    public void passEncoding() throws Exception {
        String[] result = encryption.passEncoding("password");
        Assert.assertEquals(result.length, 2);
        Assert.assertEquals(24, result[0].length()); // Salt
        Assert.assertEquals(24, result[1].length()); // Hash
    }


    @Test
    public void passEncodingDecoding() throws Exception {
        String[] result = encryption.passEncoding("password");
        Assert.assertTrue(encryption.passDecoding("password", result[1], result[0]));
    }
}