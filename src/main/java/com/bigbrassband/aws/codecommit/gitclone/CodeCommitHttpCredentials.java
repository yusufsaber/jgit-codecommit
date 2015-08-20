package com.bigbrassband.aws.codecommit.gitclone;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CodeCommitHttpCredentials {
    final private static char[] hexArray = "0123456789abcdef".toCharArray();
    private final String userName;
    private final String password;

    public CodeCommitHttpCredentials(String accessKey, String secret, String httpUrl) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, MalformedURLException, CodeCommitHttpCredentialsException {

        URL url = new URL(httpUrl);
        String canonicalRequest = "GIT\n" +
                url.getPath() + "\n" +
                "\n" +
                "host:" + url.getHost() + "\n" +
                "\n" +
                "host\n";

        System.out.println(canonicalRequest);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(canonicalRequest.getBytes());

        String[] split = StringUtils.split(url.getHost(), ".");
        if (split.length < 3)
            throw new CodeCommitHttpCredentialsException("Can not detect region from " + httpUrl);

        String region = split[1];

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String dateStamp = dateFormat.format(now);
        String shortDateStamp = dateStamp.substring(0, 8);
        String service = "codecommit";

        String toSign = "AWS4-HMAC-SHA256\n" +
                dateStamp + "\n" +
                shortDateStamp + "/" + region + "/" + service + "/aws4_request\n"
                + bytesToHex(hash);

        byte[] signedRequest = sign(secret, shortDateStamp, region, service, toSign);

        this.userName = accessKey;
        this.password = dateStamp + "Z" + bytesToHex(signedRequest);

    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] HmacSHA256(String data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes("UTF8"));
    }

    private static byte[] sign(String key, String dateStamp, String regionName, String serviceName, String toSign) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {


        byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
        byte[] kDate = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion = HmacSHA256(regionName, kDate);
        byte[] kService = HmacSHA256(serviceName, kRegion);
        byte[] kSigning = HmacSHA256("aws4_request", kService);


        return HmacSHA256(toSign, kSigning);

    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public class CodeCommitHttpCredentialsException extends Exception {
        public CodeCommitHttpCredentialsException(String message) {
            super(message);
        }
    }
}
