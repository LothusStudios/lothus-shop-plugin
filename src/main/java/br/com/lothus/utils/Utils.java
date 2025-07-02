package br.com.lothus.utils;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

  public static String dateFormat(String dateString) {
    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateString);

    String formattedDate = offsetDateTime.format(outputFormatter);

    return formattedDate;
  }

  public static String generateBuildCode(int bytes) {
    byte[] randomBytes = generateRandomBytes(3);

    return bytesToHex(randomBytes);
  }

  public static byte[] generateRandomBytes(int numBytes) {
    SecureRandom secureRandom = new SecureRandom();
    byte[] randomBytes = new byte[numBytes];
    secureRandom.nextBytes(randomBytes);
    return randomBytes;
  }

  public static String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder(2 * bytes.length);
    for (byte b : bytes) {
      String hex = Integer.toHexString(0xFF & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
