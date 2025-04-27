package com.github.justadeni.invite.suggestions;

public class Char3 {

    private final char a;
    private final char b;
    private final char c;

    public Char3(String string) {
        a = string.charAt(0);
        b = string.charAt(1);
        c = string.charAt(2);
    }

    public boolean isPrefix(String string) {
        return string.charAt(0) == a && string.charAt(1) == b && string.charAt(2) == c;
    }

    public String addSuffix(String suffix) {
        int length = suffix.length();
        char[] chars = new char[length + 3];
        chars[0] = a;
        chars[1] = b;
        chars[2] = c;
        System.arraycopy(suffix.toCharArray(), 0, chars, 3, length);
        return new String(chars);
    }

    public String addPrefixAndSuffix(String prefix, String suffix) {
        int prefixLength = prefix.length();
        int suffixLength = suffix.length();
        char[] chars = new char[prefixLength + suffixLength + 3];
        System.arraycopy(prefix.toCharArray(), 0, chars, 0, prefixLength);
        System.arraycopy(suffix.toCharArray(), 0, chars, prefixLength + 3, suffixLength);
        chars[prefixLength] = a;
        chars[prefixLength + 1] = b;
        chars[prefixLength + 2] = c;
        return new String(chars);
    }

    // Note to self: this kind of defeats the purpose to try not to use it
    @Override
    public String toString() {
        return new String(new char[] { a, b, c });
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Char3 char3)) return false;
        return a == char3.a && b == char3.b && c == char3.c;
    }

    @Override
    public int hashCode() {
        return (a << 16) | (b << 8) | c;
    }
}
