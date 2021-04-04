public class Crypto {

    public static final String NEW_LINE = System.getProperty("line.separator");

    public static void main(String[] args) {

        String INPUT_MESSAGE = Helper.readStringFromFile("message.txt");
        String KEY = Helper.readStringFromFile("key.txt");
        String stringForGeneratingPad = Helper.readStringFromFile("intForGeneratingPad.txt");
        int INT_FOR_GENERATING_PAD = Integer.parseInt(stringForGeneratingPad);


        String messageClean = Helper.cleanString(INPUT_MESSAGE);
        byte[] messageBytes = Helper.stringToBytes(messageClean);
        byte[] keyBytes = Helper.stringToBytes(KEY);
        byte[] padForOneTimePad = Encrypt.generatePad(INPUT_MESSAGE.length());
        byte[] padForCBC = Encrypt.generatePad(INT_FOR_GENERATING_PAD);

        String results = "Original input sanitized : " + NEW_LINE + messageClean + NEW_LINE + NEW_LINE + NEW_LINE
                + "------Caesar------" + NEW_LINE + NEW_LINE + testCaesar(messageBytes, keyBytes[0])  + NEW_LINE
                + "------Vigenere------" + NEW_LINE + NEW_LINE + testVigenere(messageClean, KEY) + NEW_LINE
                + "------XOR------" + NEW_LINE + NEW_LINE + testXor(messageBytes, keyBytes[0]) + NEW_LINE
                + "------One Time Pad------" + NEW_LINE + NEW_LINE + testOneTimePad(messageBytes, padForOneTimePad) + NEW_LINE
                + "------CBC------" + NEW_LINE + NEW_LINE + testCbc(messageBytes, padForCBC);
        System.out.print(results);

        Helper.writeStringToFile(results, "results.txt");

    }


    //Run the Encoding and Decoding using the caesar pattern
    public static String testCaesar(byte[] string , byte key) {
        String stringSpaceEncoding = Helper.readStringFromFile("spaceEncoding.txt");
        boolean SPACE_ENCODING = false;
        if(stringSpaceEncoding.equals("true")) {
            SPACE_ENCODING = true;
        }
        //Encoding
        byte[] result = Encrypt.caesar(string, key, SPACE_ENCODING);
        String s = Helper.bytesToString(result);
        String results = "Key used : " + Helper.readStringFromFile("key.txt").charAt(0) + NEW_LINE + NEW_LINE + "Encoded : " + NEW_LINE + s + NEW_LINE + NEW_LINE;

        //Decoding with key
        String sD = Helper.bytesToString(Encrypt.caesar(result, (byte) (-key)));
        results = results + "Decoded knowing the key : " + NEW_LINE + sD + NEW_LINE + NEW_LINE;

        //Decoding without key
        byte[][] bruteForceResult = Decrypt.caesarBruteForce(result);
        String sDA = Decrypt.arrayToString(bruteForceResult);
        Helper.writeStringToFile(sDA, "bruteForceCaesar.txt");
        results = results + "Decoded with brute force : see bruteForceCeasar.txt and search the right line" + NEW_LINE + NEW_LINE;

        byte decodingKey = Decrypt.caesarWithFrequencies(result);
        String sFD = Helper.bytesToString(Encrypt.caesar(result, decodingKey));
        results = results + "Decoded with frequencies : " + NEW_LINE + sFD + NEW_LINE + NEW_LINE;

        return results;
    }

    //Run the Encoding and Decoding using the vigenere pattern
    public static String testVigenere(String string, String keyword) {
        String stringSpaceEncoding = Helper.readStringFromFile("spaceEncoding.txt");
        boolean SPACE_ENCODING = false;
        if(stringSpaceEncoding.equals("true")) {
            SPACE_ENCODING = true;
        }
        //Encoding
        String cipher = Encrypt.encrypt(Helper.readStringFromFile("message.txt"), Helper.readStringFromFile("key.txt"), Encrypt.VIGENERE);
        String results = "Key used : " + Helper.readStringFromFile("key.txt") + NEW_LINE + NEW_LINE + "Encoded : " + NEW_LINE + cipher + NEW_LINE + NEW_LINE;
        //Decoding with key
        byte[] decryptKey = new byte[Helper.stringToBytes(keyword).length];
        for(int i = 0; i < Helper.stringToBytes(keyword).length; ++i) {
            decryptKey[i] = ((byte) (-Helper.stringToBytes(keyword)[i]));
        }
        String decrypted = Encrypt.encrypt(cipher, Helper.bytesToString(decryptKey), Encrypt.VIGENERE);
        results = results + "Decoded knowing the key : " + NEW_LINE + decrypted + NEW_LINE + NEW_LINE;

        //Decoding without key
        String broken = Decrypt.breakCipher(cipher, 1);
        results = results + "Decoded with frequencies : " + NEW_LINE + broken + NEW_LINE + NEW_LINE;

        return results;
    }

    //Run the Encoding and Decoding using the xor pattern
    public static String testXor(byte[] string , byte key) {
        //Encoding
        byte[] result = Encrypt.xor(string, key);
        String s = Helper.bytesToString(result);
        String results = "Key used : " + Helper.readStringFromFile("key.txt").charAt(0) + NEW_LINE + NEW_LINE + "Encoded : " + NEW_LINE + s + NEW_LINE + NEW_LINE;

        //Decoding with key
        String sD = Helper.bytesToString(Encrypt.xor(result, key));
        results = results + "Decoded knowing the key : " + NEW_LINE + sD + NEW_LINE + NEW_LINE;

        //Decoding without key
        byte[][] bruteForceResult = Decrypt.xorBruteForce(result);
        String sDA = Decrypt.arrayToString(bruteForceResult);
        Helper.writeStringToFile(sDA, "bruteForceXor.txt");
        results = results + "Decoded with brute force : see bruteForceXor.txt and search the right line" + NEW_LINE + NEW_LINE;

        return results;
    }

    //Run the Encoding and Decoding using the one time pad pattern
    public static String testOneTimePad(byte[] string, byte[] pad) {
        //Encoding
        byte[] result = Encrypt.oneTimePad(string, pad);
        String s = Helper.bytesToString(result);
        String results = "Pad used : " + Helper.bytesToString(pad) + NEW_LINE + NEW_LINE + "Encoded : " + NEW_LINE + s + NEW_LINE + NEW_LINE;

        //Decoding with key
        String sD = Helper.bytesToString(Encrypt.oneTimePad(result, pad));
        results = results + "Decoded knowing the key : " + NEW_LINE + sD + NEW_LINE + NEW_LINE;

        return results;
    }

    //Run the Encoding and Decoding using the cbc pattern
    public static String testCbc(byte[] string, byte[] pad) {
        //Encoding
        byte[] result = Encrypt.cbc(string, pad);
        String s = Helper.bytesToString(result);
        String results = "Pad used : " + Helper.bytesToString(pad) + NEW_LINE + NEW_LINE + "Encoded : " + NEW_LINE + s + NEW_LINE + NEW_LINE;

        //Decoding with key
        String sD = Helper.bytesToString(Decrypt.decryptCBC(result, pad));
        results = results + "Decoded knowing the key : " + NEW_LINE + sD + NEW_LINE + NEW_LINE;

        return results;
    }


}
