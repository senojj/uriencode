package senojj.uriencode;

import java.io.CharArrayWriter;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Objects;

public class UriEncoder {
    static final char[] upperhex = "0123456789ABCDEF".toCharArray();
    static BitSet unreservedCharacters;

    static {
        unreservedCharacters = new BitSet(256);
        int i;
        for (i = 'a'; i <= 'z'; i++) {
            unreservedCharacters.set(i);
        }
        for (i = 'A'; i <= 'Z'; i++) {
            unreservedCharacters.set(i);
        }
        for (i = '0'; i <= '9'; i++) {
            unreservedCharacters.set(i);
        }
        unreservedCharacters.set('-');
        unreservedCharacters.set('_');
        unreservedCharacters.set('.');
        unreservedCharacters.set('*');
        unreservedCharacters.set('~');
    }

    static void toHex(StringBuilder out, byte b) {
        char ch = upperhex[(b >> 4) & 0xF];
        out.append(ch);
        ch = upperhex[b & 0xF];
        out.append(ch);
    }

    public static String encode(String s, Charset charset) {
        Objects.requireNonNull(charset, "charset");

        StringBuilder out = new StringBuilder(s.length());
        CharArrayWriter charArrayWriter = new CharArrayWriter();

        for (int i = 0; i < s.length(); ) {
            int c = s.charAt(i);

            if (unreservedCharacters.get(c)) {
                out.append((char) c);
                i++;
                continue;
            }

            do {
                charArrayWriter.write(c);
                i++;
            } while (i < s.length() && !unreservedCharacters.get((c = s.charAt(i))));

            charArrayWriter.flush();
            String str = charArrayWriter.toString();
            byte[] ba = str.getBytes(charset);

            for (byte b : ba) {
                out.append('%');
                toHex(out, b);
            }
            charArrayWriter.reset();
        }
        return out.toString();
    }
}

