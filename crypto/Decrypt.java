import java.util.ArrayList;
import java.util.List;


public class Decrypt {


    public static final int ALPHABETSIZE = Byte.MAX_VALUE - Byte.MIN_VALUE + 1 ; //256
    public static final int APOSITION = 97 + ALPHABETSIZE/2;

    //source : https://en.wikipedia.org/wiki/Letter_frequency
    public static final double[] ENGLISHFREQUENCIES = {0.08497,0.01492,0.02202,0.04253,0.11162,0.02228,0.02015,0.06094,0.07546,0.00153,0.01292,0.04025,0.02406,0.06749,0.07507,0.01929,0.00095,0.07587,0.06327,0.09356,0.02758,0.00978,0.0256,0.0015,0.01994,0.00077};

    /**
     * Method to break a string encoded with different types of cryptosystems
     * @param type the integer representing the method to break : 0 = Caesar, 1 = Vigenere, 2 = XOR
     * @return the decoded string or the original encoded message if type is not in the list above.
     */
    public static String breakCipher(String cipher, int type) {
        //TODO : COMPLETE THIS METHOD
        assert(cipher != null && !cipher.isBlank());
        assert(type >= 0 && type <= 2);
        byte[] cipherByteArray = Helper.stringToBytes(cipher);
        byte resultByte = 0;
        byte[] resultByteArray = null;
        String finalResult ="";
        switch(type) {
            case 0:
                resultByte = caesarWithFrequencies(cipherByteArray);
                byte[] decryptedCaesar = Encrypt.caesar(cipherByteArray, resultByte);
                finalResult = Helper.bytesToString(decryptedCaesar);
                break;
            case 1:
                resultByteArray = vigenereWithFrequencies(cipherByteArray);
                finalResult = Helper.bytesToString(resultByteArray);
                break;
            case 2:
                byte[][] xorResult2D = xorBruteForce(cipherByteArray);
                finalResult = arrayToString(xorResult2D);
                break;
            default:
                return cipher;
        }
        return finalResult; //TODO: to be modified
    }


    /**
     * Converts a 2D byte array to a String
     * @param bruteForceResult a 2D byte array containing the result of a brute force method
     */
    public static String arrayToString(byte[][] bruteForceResult) {
        //TODO : COMPLETE THIS METHOD
        String result = "";

        for (int i = 0; i < bruteForceResult.length; ++i) {
            result += Helper.bytesToString(bruteForceResult[i]) + System.lineSeparator();
        }

        return result; //TODO: to be modified
    }


    //-----------------------Caesar-------------------------

    /**
     *  Method to decode a byte array  encoded using the Caesar scheme
     * This is done by the brute force generation of all the possible options
     * @param cipher the byte array representing the encoded text
     * @return a 2D byte array containing all the possibilities
     */
    public static byte[][] caesarBruteForce(byte[] cipher) {
        //TODO : COMPLETE THIS METHOD

        byte[][] result = new byte[256][cipher.length];

        for (int i = -128; i <= 127; ++i) {
            result[i+128] = Encrypt.caesar(cipher, (byte) i).clone();
        }

        return result; //TODO: to be modified
    }


    /**
     * Method that finds the key to decode a Caesar encoding by comparing frequencies
     * @param cipherText the byte array representing the encoded text
     * @return the encoding key
     */
    public static byte caesarWithFrequencies(byte[] cipherText) {
        //TODO : COMPLETE THIS METHOD

        float[] charFrequencies = computeFrequencies(cipherText);

        byte key = caesarFindKey(charFrequencies);

        return key; //TODO: to be modified
    }

    /**
     * Method that computes the frequencies of letters inside a byte array corresponding to a String
     * @param cipherText the byte array
     * @return the character frequencies as an array of float
     */
    public static float[] computeFrequencies(byte[] cipherText) {
        //TODO : COMPLETE THIS METHOD

        float[] frequencies = new float[256];

        for (int i = 0; i < cipherText.length; ++i) {
            if (cipherText[i] != 32) {
                if (cipherText[i] >= 0) {
                    frequencies[cipherText[i]] += 1;
                }
                else {
                    frequencies[cipherText[i] + 256] += 1;
                }
            }
        }

        for (int j = 0; j < frequencies.length; ++j) {
            frequencies[j] /= cipherText.length;
        }

        return frequencies; //TODO: to be modified
    }


    /**
     * Method that finds the key used by a  Caesar encoding from an array of character frequencies
     * @param charFrequencies the array of character frequencies
     * @return the key
     */
    public static byte caesarFindKey(float[] charFrequencies) {
        //TODO : COMPLETE THIS METHOD

        float[] produit = new float[256];
        int k = 0;
        int l = 0;
        int m = 0;


        for (int i = 0; i <= 255; ++i) {
            if (i < 230) {
                k = l;
                for (int j = 0; j <= 25; ++j) {
                    produit[i] += ENGLISHFREQUENCIES[j] * charFrequencies[k];
                    ++k;
                }
                ++l;
            }
            if (i >= 230) {
                l = 230;
                k = l;
                for (int j = 0; j <= 25; ++j) {
                    if (k <= 255) {
                        produit[i] += ENGLISHFREQUENCIES[j] * charFrequencies[k];
                        ++k;
                    }
                    if (k > 255) {
                        produit[i] += ENGLISHFREQUENCIES[j] * charFrequencies[m];
                        ++m;
                    }
                }
                ++l;

            }
        }

        float tmpMax = 0;
        int rang = 0;

        for (int i = 0; i <= 255; ++i) {
            if (produit[i] >= tmpMax) {
                tmpMax = produit[i];
                rang = i;

            }
        }

        byte aByte = 97;

        byte rangByte = (byte) rang;

        byte key = (byte) -(rangByte - aByte);

        return key; //TODO: to be modified
    }



    //-----------------------XOR-------------------------

    /**
     * Method to decode a byte array encoded using a XOR
     * This is done by the brute force generation of all the possible options
     * @param cipher the byte array representing the encoded text
     * @return the array of possibilities for the clear text
     */
    public static byte[][] xorBruteForce(byte[] cipher) {
        //TODO : COMPLETE THIS METHOD

        byte[][] result = new byte[256][cipher.length];

        for (int i = -128; i <= 127; ++i) {
            result[i+128] = Encrypt.xor(cipher, (byte) i).clone();
        }

        return result; //TODO: to be modified
    }



    //-----------------------Vigenere-------------------------
    // Algorithm : see  https://www.youtube.com/watch?v=LaWp_Kq0cKs
    /**
     * Method to decode a byte array encoded following the Vigenere pattern, but in a clever way,
     * saving up on large amounts of computations
     * @param cipher the byte array representing the encoded text
     * @return the byte encoding of the clear text
     */
    public static byte[] vigenereWithFrequencies(byte[] cipher) {
        List<Byte> cipherCleaned = removeSpaces(cipher);
        int keyLength = vigenereFindKeyLength(cipherCleaned);
        if (keyLength == 0) { //on peut supposer que le texte n'a pas ete crypte avec une cle 0
            return cipher;
        } else {
            byte[] key = vigenereFindKey(cipherCleaned,keyLength);
            for(int i =0 ; i < key.length ; ++i) {
                key[i] = (byte) (key[i]);
            }
            byte[] cipherStatic = new byte[cipherCleaned.size()];
            for(int i = 0 ; i < cipherCleaned.size();++i) {
                cipherStatic[i] = cipherCleaned.get(i);
            }
            String resultVigenere = Encrypt.encrypt(Helper.bytesToString(cipherStatic), Helper.bytesToString(key), 1);
            return Helper.stringToBytes(resultVigenere); //on decrypte ici pour nous pas avoir a reutiliser removeSpaces dans breakCipher
        }


    }



    /**
     * Helper Method used to remove the space character in a byte array for the clever Vigenere decoding
     * @param array the array to clean
     * @return a List of bytes without spaces
     */
    public static List<Byte> removeSpaces(byte[] array){
        //TODO : COMPLETE THIS METHOD

        List<Byte> withoutSpace = new ArrayList<Byte>();

        for (int i = 0; i < array.length; ++i) {
            if (array[i] != 32) {
                withoutSpace.add(array[i]);
            }
        }

        return withoutSpace;

    }


    /**
     * Method that computes the key length for a Vigenere cipher text.
     * @param cipher the byte array representing the encoded text without space
     * @return the length of the key
     */
    public static int vigenereFindKeyLength(List<Byte> cipher) {
        List<Integer> coincidences = vigenereCoincidences(cipher);
        int[] coincidencesTable = new int[coincidences.size()];
        for(int i =0; i < coincidencesTable.length;++i) {
            coincidencesTable[i] = coincidences.get(i);
        }
        List<Integer> maxLocalList = maxLocal(coincidencesTable);
        int keyLength = getKeyLength(maxLocalList);

        return keyLength; //TODO: to be modified
    }

    public static List<Integer> vigenereCoincidences(List<Byte> cipher){
        List<Byte> cipherCopy = new ArrayList<Byte>();
        List<Integer> coincidences = new ArrayList<Integer>();
        int k=0;
        for (int i=1;i < cipher.size(); ++i) {
            coincidences.add(0);
            cipherCopy.clear();
            k=i;
            while(k>0) {
                cipherCopy.add(null);
                --k;
            }
            for (int j = i; j < cipher.size()+i;++j) {
                cipherCopy.add(cipher.get(j-i));
                if((cipherCopy.get(j-i) != null) && (cipherCopy.get(j-i).equals(cipher.get(j-i)))) {
                    coincidences.set(i-1, coincidences.get(i-1)+1);
                }
            }
        }
        return coincidences;
    }

    public static List<Integer> maxLocal(int coincidences[]) {

        List<Integer> maxLocal = new ArrayList<Integer>();

        int moitie = (int) Math.ceil(coincidences.length / 2);

        if (coincidences[0] > coincidences[1] && coincidences[0] > coincidences[2]) {
            maxLocal.add(0);
        }

        if (coincidences[1] > coincidences[2] && coincidences[1] > coincidences[3] && coincidences[1] > coincidences[0]) {
            maxLocal.add(1);
        }


        for (int i = 2; i < moitie - 1; ++i) {
            if (coincidences[i] > coincidences[i+1] && coincidences[i] > coincidences[i+2] && coincidences[i] > coincidences[i-1] && coincidences[i] > coincidences[i-2]) {
                maxLocal.add(i);
            }
        }

        if (coincidences[moitie - 1] > coincidences[moitie - 2] && coincidences[moitie - 1] > coincidences[moitie - 3] && coincidences[moitie - 1] > coincidences[moitie]) {
            maxLocal.add(moitie - 1);
        }

        if (coincidences[moitie] > coincidences[moitie - 1] && coincidences[moitie] > coincidences[moitie - 2]) {
            maxLocal.add(moitie);
        }

        return maxLocal;
    }

    public static int getKeyLength(List<Integer> maxLocal) {

        int keyLength = 0;
        int val = 0;

        List<Integer> tab = new ArrayList<Integer>();

        for (int i = 0; i < maxLocal.size() - 1; ++i) {
            val = maxLocal.get(i+1) - maxLocal.get(i);
            tab.add(val);

        }

        int[] compteur = new int[maxLocal.size()];
        int frequence = 0;

        for (int i = 0; i < tab.size(); ++i) {
            for (int j = 0; j < tab.size(); ++j) {
                if (tab.get(i).equals(tab.get(j))) {
                    //etant donne que les arguments sont correctement fournis, la cle sera forcement de taille >0
                    compteur[i] += 1;
                }
            }
            if (compteur[i] >= frequence) {
                frequence = compteur[i];
                keyLength = tab.get(i);
            }
        }

        return keyLength;
    }



    /**
     * Takes the cipher without space, and the key length, and uses the dot product with the English language frequencies
     * to compute the shifting for each letter of the key
     * @param cipher the byte array representing the encoded text without space
     * @param keyLength the length of the key we want to find
     * @return the inverse key to decode the Vigenere cipher text
     */


    public static byte[] vigenereFindKey(List<Byte> cipher, int keyLength) {
        byte[] keyTable = new byte[keyLength];
        byte[] cipherStatic = new byte[cipher.size()];
        for(int i =0; i < cipher.size() ; ++i) {
            cipherStatic[i] = cipher.get(i);
        }
        String cipherString = Helper.bytesToString(cipherStatic);


        for(int i =0; i<keyLength; ++i) {
            String keyPart = "";
            for(int j =i; j < cipher.size() ; j = j+keyLength) {
                keyPart += cipherString.charAt(j);
            }
            byte key = Decrypt.caesarWithFrequencies(Helper.stringToBytes(keyPart));
            keyTable[i] = key;




        }
        return keyTable; //TODO: to be modified

    }




    //-----------------------Basic CBC-------------------------

    /**
     * Method used to decode a String encoded following the CBC pattern
     * @param cipher the byte array representing the encoded text
     * @param iv the pad of size BLOCKSIZE we use to start the chain encoding
     * @return the clear text
     */
    public static byte[] decryptCBC(byte[] cipher, byte[] iv) {
        final int BLOCKSIZE = iv.length;
        byte[] copyCipher = new byte[cipher.length];
        for(int i = 0; i < cipher.length ; ++i) {
            if(i % BLOCKSIZE == 0 && i!=0) {
                for(int j = i-BLOCKSIZE; j < i ; ++j) {
                    iv[j%BLOCKSIZE] = cipher[j];
                }
            }
            copyCipher[i] = (byte)(cipher[i] ^ iv[i%BLOCKSIZE]);
        }
        return copyCipher; //TODO: to be modified
    }

}