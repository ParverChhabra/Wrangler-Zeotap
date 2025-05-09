package io.cdap.wrangler.steps.aggregates;

import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.parser.GrammarBasedParser;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.parser.RecipeCompiler;
import io.cdap.wrangler.core.test.TestingRig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class AggregateStatsTest {

  private static ExecutorContext dummyContext() {
    return new ExecutorContext() {
      @Override public java.util.Map<String, String> getProperties() { return java.util.Collections.emptyMap(); }
      @Override public String getContextName() { return "test"; }
      @Override public String getNamespace() { return "default"; }
      @Override public Object getService(String app, String svc) { return null; }
      @Override public Object getMetrics() { return null; }
      @Override public Object getEnvironment() { return null; }
      @Override public Object getTransientStore() { return null; }
    };
  }

  @Test
  public void testAggregateStatsComputation() {
    AggregateStats directive = new AggregateStats();

    Arguments args = Arguments.of(java.util.Map.of(
      "sizeCol", "size",
      "timeCol", "time",
      "outputSizeCol", "total_mb",
      "outputTimeCol", "total_sec"
    ));
    directive.initialize(args);

    Row row1 = new Row("size", "10MB").add("time", "2s");
    Row row2 = new Row("size", "5MB").add("time", "3s");

    List<Row> result = directive.execute(Arrays.asList(row1, row2), dummyContext());

    Assertions.assertEquals(1, result.size());
    Row output = result.get(0);

    Assertions.assertEquals(15.0, (Double) output.getValue("total_mb"), 0.01);
    Assertions.assertEquals(5.0, (Double) output.getValue("total_sec"), 0.01);
  }

  @Test
  public void testHandlesMalformedInputs() {
    AggregateStats directive = new AggregateStats();

    Arguments args = Arguments.of(java.util.Map.of(
      "sizeCol", "size",
      "timeCol", "time",
      "outputSizeCol", "total_mb",
      "outputTimeCol", "total_sec"
    ));
    directive.initialize(args);

    Row valid = new Row("size", "10MB").add("time", "2s");
    Row malformed = new Row("size", "oops").add("time", "???");

    List<Row> result = directive.execute(Arrays.asList(valid, malformed), dummyContext());

    Assertions.assertEquals(1, result.size());
    Row output = result.get(0);

    Assertions.assertEquals(10.0, (Double) output.getValue("total_mb"), 0.01);
    Assertions.assertEquals(2.0, (Double) output.getValue("total_sec"), 0.01);
  }

  @Test
  public void testRecipeWithTestingRig() throws Exception {
    String[] recipe = new String[] {
      "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
    };

    Row row1 = new Row("data_transfer_size", "10MB").add("response_time", "2s");
    Row row2 = new Row("data_transfer_size", "5MB").add("response_time", "3s");

    List<Row> input = Arrays.asList(row1, row2);
    List<Row> result = TestingRig.execute(recipe, input);

    Assertions.assertEquals(1, result.size());
    Row output = result.get(0);

    Assertions.assertEquals(15.0, (Double) output.getValue("total_size_mb"), 0.01);
    Assertions.assertEquals(5.0, (Double) output.getValue("total_time_sec"), 0.01);
  }

  @Test
  public void testByteSizeParsing() {
    Assertions.assertEquals(10240, ByteSize.parse("10KB"));
    Assertions.assertEquals(1572864, ByteSize.parse("1.5MB"));
    Assertions.assertEquals(5, ByteSize.parse("5B"));
  }

  @Test
  public void testTimeDurationParsing() {
    Assertions.assertEquals(5_000_000, TimeDuration.parse("5ms"));
    Assertions.assertEquals(2_100_000_000L, TimeDuration.parse("2.1s"));
    Assertions.assertEquals(2_000_000, TimeDuration.parse("2ms"));
  }

  @Test
  public void testParserValidAggregateStatsSyntax() throws Exception {
    String recipe = "aggregate-stats :size :duration total_mb total_sec";
    GrammarBasedParser parser = new GrammarBasedParser(recipe);
    Assertions.assertDoesNotThrow(parser::parse);
  }

  @Test
  public void testParserRejectsInvalidSyntax() {
    String invalidRecipe = "aggregate-stats size duration total_mb total_sec"; // missing ':'
    GrammarBasedParser parser = new GrammarBasedParser(invalidRecipe);
    Assertions.assertThrows(Exception.class, parser::parse);
  }

  @Test
  public void testRecipeCompilerParsesAggregateStats() throws Exception {
    String recipe = "aggregate-stats :bytes :latency sum_mb sum_sec";
    Assertions.assertDoesNotThrow(() -> RecipeCompiler.compile(recipe));
  }

  @Test
  public void testRecipeCompilerRejectsInvalidDirective() {
    String recipe = "aggregate-stats :bytes latency sum_mb sum_sec"; // missing ':' before latency
    Assertions.assertThrows(Exception.class, () -> RecipeCompiler.compile(recipe));
  }
}
