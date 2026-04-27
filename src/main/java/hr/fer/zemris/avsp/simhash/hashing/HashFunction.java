package hr.fer.zemris.avsp.simhash.hashing;

public interface HashFunction {

    byte[] digest(String text);

    default int getHashBitLength() {
        return digest("Dummy text").length * 8;
    }

}
