package io.cdap.wrangler.steps.aggregates;

import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Executor;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;

import java.util.Collections;
import java.util.List;

public class AggregateStats implements Executor<List<Row>, List<Row>> {
  private String sizeCol;
  private String timeCol;
  private String outputSizeCol;
  private String outputTimeCol;

  @Override
  public void initialize(Arguments args) {
    this.sizeCol = args.value("sizeCol");
    this.timeCol = args.value("timeCol");
    this.outputSizeCol = args.value("outputSizeCol");
    this.outputTimeCol = args.value("outputTimeCol");
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) {
    long totalBytes = 0;
    long totalNanos = 0;

    for (Row row : rows) {
      Object sVal = row.getValue(sizeCol);
      Object tVal = row.getValue(timeCol);

      if (sVal == null || tVal == null) continue;

      try {
        ByteSize bs = new ByteSize(sVal.toString());
        TimeDuration td = new TimeDuration(tVal.toString());
        totalBytes += bs.getBytes();
        totalNanos += td.getNanoseconds();
      } catch (Exception e) {
        // skip malformed values
      }
    }

    double mb = totalBytes / (1024.0 * 1024);
    double seconds = totalNanos / 1_000_000_000.0;

    Row result = new Row();
    result.add(outputSizeCol, mb);
    result.add(outputTimeCol, seconds);
    return Collections.singletonList(result);
  }

  @Override
  public void destroy() {
    // no cleanup necessary
  }
}
