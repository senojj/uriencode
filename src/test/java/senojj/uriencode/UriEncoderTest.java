package senojj.uriencode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UriEncoderTest {
    static List<String> unreservedCharacters;
    static Map<String, String> someReservedCharacters;

    static {
        unreservedCharacters = new ArrayList<>();
        for (int i = 'a'; i <= 'z'; i++) {
            unreservedCharacters.add(Character.toString((char) i));
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            unreservedCharacters.add(Character.toString((char) i));
        }
        for (int i = '0'; i <= '9'; i++) {
            unreservedCharacters.add(Character.toString((char) i));
        }
        unreservedCharacters.add("-");
        unreservedCharacters.add("_");
        unreservedCharacters.add(".");
        unreservedCharacters.add("*");
        unreservedCharacters.add("~");

        someReservedCharacters = new HashMap<>();
        someReservedCharacters.put("+", "%2B");
        someReservedCharacters.put("=", "%3D");
        someReservedCharacters.put("?", "%3F");
        someReservedCharacters.put("{", "%7B");
        someReservedCharacters.put("@", "%40");
        someReservedCharacters.put("&", "%26");
        someReservedCharacters.put("©", "%C2%A9");
        someReservedCharacters.put("(", "%28");
        someReservedCharacters.put("®", "%C2%AE");
    }

    @Test
    void doesNotEncodeUnreservedCharacters() {
        for (String character : unreservedCharacters) {
            assertEquals(character, UriEncoder.encode(character, StandardCharsets.UTF_8));
        }
    }

    @Test
    void encodesReservedCharacters() {
        for (Map.Entry<String, String> entry : someReservedCharacters.entrySet()) {
            assertEquals(entry.getValue(), UriEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
        }
    }

    @Test
    void encodesMixedCharacters() {
        String subject = "2=1+1";
        assertEquals("2%3D1%2B1", UriEncoder.encode("2=1+1", StandardCharsets.UTF_8));
    }
}
