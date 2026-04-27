package hr.fer.zemris.avsp.simhash.util;

import hr.fer.zemris.avsp.simhash.hashing.HashFunction;

import java.math.BigInteger;
import java.util.Objects;

public class Util {

    public static BigInteger generateSimHash(String text, HashFunction hashFunction) {
        Objects.requireNonNull(text, "Text argument of generateSimHash method can't be null.");
        Objects.requireNonNull(hashFunction, "Hash function argument of generateSimHash method can't be null.");

        if (text.isEmpty()) {
            return BigInteger.ZERO;
        }

        String[] words = text.split(" +");
        int bitLength = hashFunction.getHashBitLength();
        int byteLength = bitLength / 8;
        int[] digitSums = new int[bitLength];

        for (String word : words) {
            byte[] hash = hashFunction.digest(word);
            for (int i = 0; i < hash.length; i++) {
                for (int j = 0; j < 8; j++) {
                    int bitIndex = i * 8 + j;
                    if (bitIndex >= bitLength) {
                        break;
                    }
                    digitSums[bitIndex] += (hash[i] & (0x80 >> j)) != 0 ? 1 : -1;
                }
            }
        }

        byte[] result = new byte[byteLength];
        for (int i = 0; i < bitLength; i++) {
            if (digitSums[i] >= 0) {
                result[i / 8] |= (byte) (0x80 >> (i % 8));
            }
        }

        return new BigInteger(1, result);
    }

}
