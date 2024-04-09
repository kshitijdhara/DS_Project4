// Andrew ID - kdhara
// Name - Kshtij Dhara
package ds.project4;

public class Methods {

    public static String encode(String message, int shift) {
        return shiftChar(message, shift);
    }

    public static String decode(String message, int shift) {
        return shiftChar(message, -shift);  // Decode by shifting back by the same amount
    }

    private static String shiftChar(String message, int shift) {
        StringBuilder encryptedMessage = new StringBuilder();
        for (char character : message.toCharArray()) {
            if (Character.isAlphabetic(character)) {
                int newChar = shiftChar(character, shift);
                encryptedMessage.append((char) newChar);
            } else {
                encryptedMessage.append(character);
            }
        }
        return encryptedMessage.toString();
    }

    private static int shiftChar(char character, int shift) {
        int base = Character.isUpperCase(character) ? 'A' : 'a';
        int newChar = (character - base + shift) % 26;
        if (newChar < 0) {
            newChar += 26; // Correct negative shift for decoding
        }
        return newChar + base;
    }

    // Example usage
    public static void main(String[] args) {
        String message = "Hello, World!";
        int shift = 3;
        String encodedMessage = encode(message, shift);
        String decodedMessage = decode(encodedMessage, shift);

        System.out.println("Original message: " + message);
        System.out.println("Encoded message: " + encodedMessage);
        System.out.println("Decoded message: " + decodedMessage);
    }
}
class CreateUserResponse {
    private String error;
    private String username;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
class LoginResponse {
    private String error;
    private String username;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}