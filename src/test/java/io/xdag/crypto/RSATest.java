/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2030 The XdagJ Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.xdag.crypto;

import io.xdag.utils.RSAUtils;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class RSATest {

    @Test
    public void test() throws Exception {
        Map<String, String> keyPairMap = RSAUtils.createKeys(1024);
        System.out.println("-----public key----\n" + keyPairMap.get("publicKey"));
        System.out.println("-----private key----\n" + keyPairMap.get("privateKey"));

        String data = "abc122";

        // 1.use public key encrypt
        String encode = RSAUtils.publicEncrypt(data, RSAUtils.getPublicKey(keyPairMap.get("publicKey")));

        System.out.println("-----encrypt result----\n" + encode);
        // 1.use private key decrypt
        String decodeResult = RSAUtils.privateDecrypt(encode, RSAUtils.getPrivateKey(keyPairMap.get("privateKey")));
        System.out.println("-----decrypt result----\n" + decodeResult);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
        Map<String, String> keyPairMap = RSAUtils.createKeys(1024);
        RSAPublicKey pub = RSAUtils.getPublicKey(keyPairMap.get("publicKey"));

        System.out.println("getModulus length:" + pub.getModulus().bitLength() + " bits");
    }
}
