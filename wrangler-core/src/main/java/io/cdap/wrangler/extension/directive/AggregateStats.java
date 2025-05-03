package io.cdap.wrangler.extension.directive;

import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.*;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.utils.ByteSize;
import io.cdap.wrangler.utils.TimeDuration;
import io.cdap.wrangler.utils.UnitConverter;


import java.util.*;

/**
 * Directive to aggregate byte sizes and time durations across rows.
 */
public class AggregateStats implements Directive {
    private String sizeCol;
    private String timeCol;
    private String targetSizeCol;
    private String targetTimeCol;
    private String byteUnit = "MB";
    private String timeUnit = "sec";
    private String aggType = "total";

    private long totalBytes = 0;
    private long totalTimeNanos = 0;
    private int rowCount = 0;

    @Override
public UsageDefinition define() {
    return UsageDefinition.builder("aggregate-stats")
        .define("sizeCol", ColumnType.STRING, "Column with byte sizes")
        .define("timeCol", ColumnType.STRING, "Column with time durations")
        .define("targetSizeCol", ColumnType.STRING, "Output total size column")
        .define("targetTimeCol", ColumnType.STRING, "Output total or avg time column")
        .defineOptional("byteUnit", ColumnType.STRING, "Unit for size output (MB, GB)")
        .defineOptional("timeUnit", ColumnType.STRING, "Unit for time output (sec, min)")
        .defineOptional("aggType", ColumnType.STRING, "total or avg")
        .build();
}


    @Override
    public void initialize(Arguments args) throws DirectiveParseException {
        sizeCol = args.value("sizeCol");
        timeCol = args.value("timeCol");
        targetSizeCol = args.value("targetSizeCol");
        targetTimeCol = args.value("targetTimeCol");

        if (args.contains("byteUnit")) {
            byteUnit = args.value("byteUnit");
        }
        if (args.contains("timeUnit")) {
            timeUnit = args.value("timeUnit");
        }
        if (args.contains("aggType")) {
            aggType = args.value("aggType");
        }
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext ctx) throws DirectiveExecutionException {
        for (Row row : rows) {
            Object sizeValue = row.getValue(sizeCol);
            Object timeValue = row.getValue(timeCol);

            if (sizeValue != null && timeValue != null) {
                totalBytes += new ByteSize(sizeValue.toString()).getBytes();
                totalTimeNanos += new TimeDuration(timeValue.toString()).getNanos();
                rowCount++;
            }
        }

        double finalSize = UnitConverter.bytesTo(totalBytes, byteUnit);
        double finalTime = UnitConverter.nanosTo(totalTimeNanos, timeUnit);

        if ("avg".equalsIgnoreCase(aggType) && rowCount > 0) {
            finalTime /= rowCount;
        }

        Row output = new Row();
        output.add(targetSizeCol, finalSize);
        output.add(targetTimeCol, finalTime);
        return Collections.singletonList(output);
    }

    @Override
    public void destroy() {
        // No cleanup necessary
    }
}
