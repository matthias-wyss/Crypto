import java.io.*;
import java.nio.charset.StandardCharsets;

public class Helper {
    public static final String SEP = File.separator;
    public static final String RES_PATH_HEADER = "res" + SEP;

    /**
     * Method to clean a string, only keeping lower case letters, and spaces.
     * @param s the string to clean
     * @return the string cleaned
     */
    public static String cleanString(String s) {
        String cleaned = s.toLowerCase();
        cleaned = cleaned.replaceAll("[:,;.]", " ");
        cleaned = cleaned.replaceAll("[^a-z ]", ""); //97-122,32

        assert (cleaned.matches("[a-z ]*"));
        return cleaned;
    }


    /**
     * Method turning a String into a byte array
     * @param message the String
     * @return a byte array corresponding to the String
     */
    public static byte[] stringToBytes(String message) {
        return message.getBytes(StandardCharsets.ISO_8859_1);
    }


    /**
     * Method turning a byte array into a String
     * @param numbers the byte array
     * @return a String corresponding to the byte array
     */
    public static String bytesToString(byte[] numbers) {
        return new String(numbers, StandardCharsets.ISO_8859_1);
    }





    public static void writeStringToFile(String text, String name) {
        try (OutputStream outputStream = new FileOutputStream(RES_PATH_HEADER + name);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {

            writer.write(text);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    /**
     * Method to read a file into a string
     * @param fileName the name of the file
     */
    public static String readStringFromFile(String fileName) {
        String string= "";
        File file = new File(RES_PATH_HEADER + fileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String temp;
            boolean checkFirst = false;
            while((temp = br.readLine())!= null) {
                if(checkFirst) {
                    string += (" " + temp);
                }else {
                    string += temp;
                    checkFirst = true;
                }
            }

            br.close();

        }catch (IOException e) {
            e.printStackTrace();
        }

        return string;

    }


}
