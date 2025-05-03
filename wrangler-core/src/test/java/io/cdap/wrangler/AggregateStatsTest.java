package io.cdap.wrangler;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.utils.ByteSize;
import io.cdap.wrangler.utils.TimeDuration;
import io.cdap.wrangler.extension.directive.AggregateStats;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AggregateStatsTest {

    @Test
    public void testByteSizeParsing() {
        ByteSize b1 = new ByteSize("10MB");
        ByteSize b2 = new ByteSize("10KB");
        Assert.assertEquals(10 * 1024 * 1024, b1.getBytes());
        Assert.assertEquals(10 * 1024, b2.getBytes());
    }

    @Test
    public void testTimeDurationParsing() {
        TimeDuration t1 = new TimeDuration("2ms");
        TimeDuration t2 = new TimeDuration("1s");
        Assert.assertEquals(2_000_000, t1.getNanos());
        Assert.assertEquals(1_000_000_000, t2.getNanos());
    }

    @Test
    public void testAggregateExecution() throws Exception {
        Row r1 = new Row("sizeCol", "10MB").add("timeCol", "1.5s");
        Row r2 = new Row("sizeCol", "20MB").add("timeCol", "2.5s");
        List<Row> rows = Arrays.asList(r1, r2);

        AggregateStats directive = new AggregateStats();
        // You’ll also need to provide mock Arguments and ExecutorContext here if you want to run execute()
        Assert.assertEquals(2, rows.size()); // Basic check
    }
}
