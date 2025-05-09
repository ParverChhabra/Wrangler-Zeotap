package io.cdap.wrangler.api.parser;
import com.google.gson.JsonPrimitive;

public class ByteSize implements Token {
    private final long bytes;
    private final String original;

    public ByteSize(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Byte size string cannot be null or empty");
        }
        this.original = value.trim().toUpperCase();
        double number;
        String unit;
        int i = 0;
        while (i < original.length() && (Character.isDigit(original.charAt(i)) || original.charAt(i) == '.')) i++;
        if (i == 0 || i == original.length()) {
            throw new IllegalArgumentException("Invalid byte size format: " + value);
        }
        try {
            number = Double.parseDouble(original.substring(0, i));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric part in byte size: " + value);
        }
        unit = original.substring(i).replaceAll("\\s+", "");

        switch (unit) {
            case "B": this.bytes = (long)(number); break;
            case "KB": case "K": this.bytes = (long)(number * 1024); break;
            case "MB": case "M": this.bytes = (long)(number * 1024 * 1024); break;
            case "GB": case "G": this.bytes = (long)(number * 1024 * 1024 * 1024); break;
            case "TB": case "T": this.bytes = (long)(number * 1024L * 1024 * 1024 * 1024); break;
            default: throw new IllegalArgumentException("Unknown or unsupported byte unit: " + unit);
        }
        if (bytes < 0) {
            throw new IllegalArgumentException("Byte size cannot be negative: " + value);
        }
    }

    public long getBytes() {
        return bytes;
    }

    @Override
    public TokenType type() {
        return TokenType.BYTE_SIZE;
    }

    @Override
    public Object value() {
        return bytes;
    }

    @Override
    public JsonPrimitive toJson() {
        return new JsonPrimitive(bytes);
    }

    @Override
    public String toString() {
        return original;
    }
}

