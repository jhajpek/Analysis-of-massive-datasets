package hr.fer.zemris.avsp.simhash.hashing.impl;

import hr.fer.zemris.avsp.simhash.hashing.HashFunction;

import java.nio.charset.StandardCharsets;

import static org.apache.commons.codec.digest.DigestUtils.getSha1Digest;

public class SHA1 implements HashFunction {

    @Override
    public byte[] digest(String text) {
        return getSha1Digest().digest(text.getBytes(StandardCharsets.UTF_8));
    }

}
