package io.cdap.wrangler.api.parser;

import com.google.gson.JsonPrimitive;

public class TimeDuration implements Token {
    private final long nanoseconds;
    private final String original;

    public TimeDuration(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Time duration string cannot be null or empty");
        }
        this.original = value.trim().toLowerCase();
        double number;
        String unit;
        int i = 0;
        while (i < original.length() && (Character.isDigit(original.charAt(i)) || original.charAt(i) == '.')) i++;
        if (i == 0 || i == original.length()) {
            throw new IllegalArgumentException("Invalid time duration format: " + value);
        }
        try {
            number = Double.parseDouble(original.substring(0, i));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric part in time duration: " + value);
        }
        unit = original.substring(i).replaceAll("\\s+", "");

        switch (unit) {
            case "ns": this.nanoseconds = (long)(number); break;
            case "ms": this.nanoseconds = (long)(number * 1_000_000); break;
            case "s":  this.nanoseconds = (long)(number * 1_000_000_000); break;
            case "m":  this.nanoseconds = (long)(number * 60 * 1_000_000_000); break;
            case "h":  this.nanoseconds = (long)(number * 3600 * 1_000_000_000); break;
            default: throw new IllegalArgumentException("Unknown or unsupported time unit: " + unit);
        }
        if (nanoseconds < 0) {
            throw new IllegalArgumentException("Time duration cannot be negative: " + value);
        }
    }

    public long getNanoseconds() {
        return nanoseconds;
    }

    @Override
    public TokenType type() {
        return TokenType.TIME_DURATION;
    }

    @Override
    public Object value() {
        return nanoseconds;
    }

    @Override
    public JsonPrimitive toJson() {
        return new JsonPrimitive(nanoseconds);
    }

    @Override
    public String toString() {
        return original;
    }
}
