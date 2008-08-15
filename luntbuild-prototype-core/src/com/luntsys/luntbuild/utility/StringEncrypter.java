package com.luntsys.luntbuild.utility;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * String Encrypter/Decrypter that uses DES or DESEDE method for encryption
 *
 * @author lubosp
 *
 */
public class StringEncrypter {
    
    /** DESede name */
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    /** DES name */
    public static final String DES_ENCRYPTION_SCHEME = "DES";
    /** Default encryption key */
    public static final String DEFAULT_ENCRYPTION_KEY  = "123456789012345678901234567890";
    
    private KeySpec             keySpec;
    private SecretKeyFactory    keyFactory;
    private Cipher              cipher;
    
    private static final String UNICODE_FORMAT = "UTF8";

    /** Creates StringEncrypter with default key
     * @param encryptionScheme scheme
     * @throws EncryptionException if fails
     */
    public StringEncrypter( String encryptionScheme ) throws EncryptionException {
        this( encryptionScheme, DEFAULT_ENCRYPTION_KEY );
    }

    /** Creates StringEncrypter
     * @param encryptionScheme scheme
     * @param encryptionKey key
     * @throws EncryptionException if fails
     */
    public StringEncrypter( String encryptionScheme, String encryptionKey )
            throws EncryptionException {

        if ( encryptionKey == null )
                throw new IllegalArgumentException( "encryption key was null" );
        if ( encryptionKey.trim().length() < 24 )
                throw new IllegalArgumentException(
                        "encryption key was less than 24 characters" );

        try
        {
            byte[] keyAsBytes = encryptionKey.getBytes( UNICODE_FORMAT );

            if ( encryptionScheme.equals( DESEDE_ENCRYPTION_SCHEME) )
            {
                this.keySpec = new DESedeKeySpec( keyAsBytes );
            }
            else if ( encryptionScheme.equals( DES_ENCRYPTION_SCHEME ) )
            {
                this.keySpec = new DESKeySpec( keyAsBytes );
            }
            else
            {
                throw new IllegalArgumentException( "Encryption scheme not supported: "
                                                    + encryptionScheme );
            }

            this.keyFactory = SecretKeyFactory.getInstance( encryptionScheme );
            this.cipher = Cipher.getInstance( encryptionScheme );

        }
        catch (InvalidKeyException e)
        {
            throw new EncryptionException( e );
        }
        catch (UnsupportedEncodingException e)
        {
            throw new EncryptionException( e );
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new EncryptionException( e );
        }
        catch (NoSuchPaddingException e)
        {
            throw new EncryptionException( e );
        }

    }

    /** Return encrypted string
     * @param unencryptedString unencrypted string
     * @return encrypted string
     * @throws EncryptionException if fails
     */
    public String encrypt( String unencryptedString ) throws EncryptionException
    {
        if ( unencryptedString == null || unencryptedString.trim().length() == 0 )
                throw new IllegalArgumentException(
                        "unencrypted string was null or empty" );

        try
        {
            SecretKey key = this.keyFactory.generateSecret( this.keySpec );
            this.cipher.init( Cipher.ENCRYPT_MODE, key );
            byte[] cleartext = unencryptedString.getBytes( UNICODE_FORMAT );
            byte[] ciphertext = this.cipher.doFinal( cleartext );

            BASE64Encoder base64encoder = new BASE64Encoder();
            return base64encoder.encode( ciphertext );
        }
        catch (Exception e)
        {
            throw new EncryptionException( e );
        }
    }

    /** Return decrypted string
     * @param encryptedString encrypted string
     * @return decrypted string
     * @throws EncryptionException if fails
     */
    public String decrypt( String encryptedString ) throws EncryptionException
    {
        if ( encryptedString == null || encryptedString.trim().length() <= 0 )
                throw new IllegalArgumentException( "encrypted string was null or empty" );

        try
        {
            SecretKey key = this.keyFactory.generateSecret( this.keySpec );
            this.cipher.init( Cipher.DECRYPT_MODE, key );
            BASE64Decoder base64decoder = new BASE64Decoder();
            byte[] cleartext = base64decoder.decodeBuffer( encryptedString );
            byte[] ciphertext = this.cipher.doFinal( cleartext );

            return bytes2String( ciphertext );
        }
        catch (Exception e)
        {
            throw new EncryptionException( e );
        }
    }

    private static String bytes2String( byte[] bytes )
    {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++)
        {
            stringBuffer.append( (char) bytes[i] );
        }
        return stringBuffer.toString();
    }

    /**
     * EncryptionException
     *
     * @author lubosp
     *
     */
    public static class EncryptionException extends Exception
    {
        /**
         * Creates EncryptionException
         *
         * @param t throwable
         *
         */
        public EncryptionException( Throwable t )
        {
            super( t );
        }
    }
}