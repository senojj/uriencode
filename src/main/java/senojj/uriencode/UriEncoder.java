package senojj.uriencode;

import java.io.CharArrayWriter;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Objects;

public class UriEncoder {
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

    static boolean isHighSurrogate(int c) {
        return (c >= 0xD800 && c <= 0xDBFF);
    }

    static boolean isLowSurrogate(int c) {
        return (c >= 0xDC00 && c <= 0xDFFF);
    }

    static char toChar(int digit) {
        if (digit < 10) {
            return (char) ('0' + digit);
        }
        return (char) ('A' - 10 + digit);
    }

    static void toHex(StringBuilder out, byte b) {
        char ch = toChar((b >> 4) & 0xF);
        out.append(ch);
        ch = toChar(b & 0xF);
        out.append(ch);
    }

    public static String encode(String s, Charset charset) {
        Objects.requireNonNull(charset, "charset");

        StringBuilder out = new StringBuilder(s.length());
        CharArrayWriter charArrayWriter = new CharArrayWriter();

        for (int i = 0; i < s.length(); ) {
            int c = s.charAt(i);

            if (unreservedCharacters.get(c)) {
                out.append((char)c);
                i++;
                continue;
            }

            do {
                charArrayWriter.write(c);

                if (isHighSurrogate(c) && (i + 1) < s.length()) {
                    int d = s.charAt(i + 1);

                    if (isLowSurrogate(d)) {
                        charArrayWriter.write(d);
                        i++;
                    }
                }
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

