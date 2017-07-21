package com.suntek.ibmsapp.component.http;



import com.suntek.ibmsapp.component.core.BaseComponent;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encryptor
 *
 * @author kutzhang@gmail.com
 */
public class Encryptor extends BaseComponent
{
    static
    {
        // add cipher algorithm provider
        Security.addProvider(new BouncyCastleProvider());
    }

    // iv
    private static final AlgorithmParameterSpec IV_SPEC = new IvParameterSpec(
            new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    );

    // key algorithm
    private static final String KEY_ALGORITHM = "AES";

    // cipher algorithm provider
    private static final String CIPHER_ALGORITHM_PROVIDER = "BC";

    // chpher algorithm
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";

    /**
     * encode
     *
     * @param data data
     * @param key  key
     * @return encrypted data
     */
    public byte[] encode(byte[] data, String key)
    {
        byte[] keyBytes = generateKey(key);

        Cipher cipher;

        try
        {
            SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            cipher = Cipher.getInstance(CIPHER_ALGORITHM, CIPHER_ALGORITHM_PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, IV_SPEC);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        byte[] encrypted;
        try
        {
            encrypted = cipher.doFinal(data);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return encrypted;
    }


    /**
     * decode
     *
     * @param encryptedData encrypted data
     * @param key           key
     * @return decrypted data
     */
    public byte[] decode(byte[] encryptedData, String key)
    {
        byte[] keyBytes = generateKey(key);

        Cipher cipher;
        try
        {
            SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            cipher = Cipher.getInstance(CIPHER_ALGORITHM, CIPHER_ALGORITHM_PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, IV_SPEC);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        byte[] decrypted;
        try
        {
            decrypted = cipher.doFinal(encryptedData);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return decrypted;
    }

    /**
     * generate key
     *
     * @param password password
     * @return aes key
     */
    private static byte[] generateKey(String password) throws RuntimeException
    {
        byte[] passwordBytes;
        try
        {
            passwordBytes = password.getBytes("utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }

        int length = passwordBytes.length > 32 ? 32 : passwordBytes.length;
        byte[] result = new byte[32];
        System.arraycopy(passwordBytes, 0, result, 0, length);

        return result;
    }
}
