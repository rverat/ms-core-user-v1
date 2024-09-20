package com.thedevlair.user.security.util;

public class EncryptionUtility {

    // Private Key Format
    public static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
    public static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";
    // Public Key Format
    public static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    public static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";
    // Error Message
    public static final String INVALID_PUBLIC_KEY = "Invalid Public Key!";
    public static final String INVALID_PRIVATE_KEY = "Invalid Private Key!";
    public static final String UTF_8 = "UTF-8";
    public static final String RSA = "RSA";
    public static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoNOuF6aLtTf1LkexS4aN\n" +
            "CBUM4/OxjwmSGoNvKB/rZGBmLt/OnLAr4u7Fs+R3oyg/JDkbYAcH3CdHxFZ73BLx\n" +
            "f5gcPc2dMDfRq5llEf7OI0DykEKHH03sTILMwy6z7KC1MnJzrStPr2Zsz5bDi+AH\n" +
            "0D1TWEqIlYIYr8JsUEFEYK/FlKyeiTiaviIMTtSGjdGy8oZ5aY0FPHaSm9cmTJL6\n" +
            "u19kxOrO1wh699vQXoKtV4hFsg3ilTxu5z2217WkN7/KB/otxkZiZ2rYP7r0NjDg\n" +
            "AesaeNnyUYs1H574N+gKt3A0NF+96HxWzzeTQOUS3iC3C//wLU3wQCSF5ccCWpRw\n" +
            "9QIDAQAB\n" +
            "-----END PUBLIC KEY-----";
    public static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCg064Xpou1N/Uu\n" +
            "R7FLho0IFQzj87GPCZIag28oH+tkYGYu386csCvi7sWz5HejKD8kORtgBwfcJ0fE\n" +
            "VnvcEvF/mBw9zZ0wN9GrmWUR/s4jQPKQQocfTexMgszDLrPsoLUycnOtK0+vZmzP\n" +
            "lsOL4AfQPVNYSoiVghivwmxQQURgr8WUrJ6JOJq+IgxO1IaN0bLyhnlpjQU8dpKb\n" +
            "1yZMkvq7X2TE6s7XCHr329Begq1XiEWyDeKVPG7nPbbXtaQ3v8oH+i3GRmJnatg/\n" +
            "uvQ2MOAB6xp42fJRizUfnvg36Aq3cDQ0X73ofFbPN5NA5RLeILcL//AtTfBAJIXl\n" +
            "xwJalHD1AgMBAAECggEAAJ6gKpYpLqMPDkUXcUeM7o2KNaygMAFk7zY3bOumkAXx\n" +
            "Dtgs5xykpqSt3VIRhe7nQ1/284lDZf0+wQpmQsRN6uRuo2xR4Tgt1h/qrEWTjL44\n" +
            "0QZ4l83IjcjpOg1Jgy1hVVbsjCvBDFd0Lt6RVU4hKZO66PmsigrsxmyU10BRH6A8\n" +
            "Lc9zKSG+V6r1Iu9QJ9C+JH3aFywEms0tVPQKV7v0i7nCe4Z8BdXw3y4A+WwLmjzc\n" +
            "iROm/adAhmTLd3eV8CSUsDASOqna2SFcjQSp6ku6ezbho8wpwH7bzkPNj+2ppn2P\n" +
            "sS17lTaFZm9AM1yxvElDijj3PpPLYbF24DBzGq7yMQKBgQDOobfhHbKxibIMtNTL\n" +
            "Gs9uI83qtyLr+/L54WnsNqifcGboAxe5QD5KxXuxtL/2751JWynXtUCU6WKj87je\n" +
            "r6L+nr14HgoJXouA3mngGXCKd1J5ueGwnSCHvDIj5DgnH5IBhzaUzq2kwJvydb1O\n" +
            "Ltn5mtS6kx904Ylz5enEfRDAyQKBgQDHQGLF9H3Tfyi4O8LPFtnByCLt+cgbdnhP\n" +
            "F1MSxgZ1+g8Wkzhr8xaGQLIWHtd4x8Os0F0hYH95BSKxS1khDexM6bwIjkwNApOf\n" +
            "/j9nSjq6/EmxzNtPvOwlZ+CVvWhU/Yvj3CDJF6yd0yTehlG+nw4j2xyKuRCdWK3B\n" +
            "v6k8t3GQzQKBgHIOZCBXsz46YlSPMHJX/y2th8lKg3U9IlZ57WzckuFHOxZQNrUj\n" +
            "1/6P2YIy4k9c3jYSoxsVSmuoE7GqI16QFmlzl+QIuMOJM3nTyx9PC4by7EySpRhG\n" +
            "8yDZadfT7obx7zb2utMS7lhllO8VvIO+asVTiJ7f/NoYcDXQL+QsoKW5AoGBALnM\n" +
            "WE6vRgXKNXZWg7oIXNZOggPXP7ocJuPZpCWFctDgdOFgNuSysPZPrcDi0ZpW7Shg\n" +
            "w/czvKplF4yr7ljcWtT9v9Fan6AiHva6ZvUySsHCMBYUKQ2Z5bqbutjm3jsikyfP\n" +
            "ROM/qD8jfOXj6qYOO2lEEMFrAQs4fg6FYpKznLqhAoGABYZzr0tQmrFOY+zZSMFx\n" +
            "dIMRNwUpeOx/RYDLTwCe/YmisGvqoQN29SHqEAnTWeH0Xl2NXivocu2s3o+wUwp5\n" +
            "qS3fDwQvFMTNmSZX7EGYP8Um9tEOYHrdT+YDS2npI+1Wqw1tvt5XtfmcK65aRGDE\n" +
            "wbumGpAUvbGvAqvTv4l16I0=\n" +
            "-----END PRIVATE KEY-----";

    private EncryptionUtility() {
    }

}