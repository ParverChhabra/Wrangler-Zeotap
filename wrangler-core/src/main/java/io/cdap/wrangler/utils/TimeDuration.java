package io.cdap.wrangler.utils;

public class TimeDuration {
    public static long parse(String input) {
        input = input.toLowerCase().trim();
        if (input.endsWith("ms")) return (long)(Double.parseDouble(input.replace("ms", "")) * 1_000_000);
        if (input.endsWith("s")) return (long)(Double.parseDouble(input.replace("s", "")) * 1_000_000_000);
        return Long.parseLong(input);
    }
}