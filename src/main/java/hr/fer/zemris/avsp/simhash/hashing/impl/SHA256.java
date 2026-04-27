package hr.fer.zemris.avsp.simhash.hashing.impl;

import hr.fer.zemris.avsp.simhash.hashing.HashFunction;

import java.nio.charset.StandardCharsets;

import static org.apache.commons.codec.digest.DigestUtils.getSha256Digest;

public class SHA256 implements HashFunction {

    @Override
    public byte[] digest(String text) {
        return getSha256Digest().digest(text.getBytes(StandardCharsets.UTF_8));
    }

}
