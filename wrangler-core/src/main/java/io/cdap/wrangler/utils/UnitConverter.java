package io.cdap.wrangler.utils;

public class UnitConverter {
    public static double bytesTo(long bytes, String unit) {
        switch (unit.toLowerCase()) {
            case "kb": return bytes / 1024.0;
            case "mb": return bytes / (1024.0 * 1024);
            case "gb": return bytes / (1024.0 * 1024 * 1024);
            default: return bytes;
        }
    }

    public static double nanosTo(long nanos, String unit) {
        switch (unit.toLowerCase()) {
            case "ms": return nanos / 1_000_000.0;
            case "sec": return nanos / 1_000_000_000.0;
            default: return nanos;
        }
    }
}