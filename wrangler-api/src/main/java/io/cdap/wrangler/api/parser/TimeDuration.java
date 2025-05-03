/*
 * Copyright © 2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */




package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * A token representing a time duration like "150ms", "2h", or "3.5seconds".
 * Provides conversion to nanoseconds.
 */

public class TimeDuration implements Token {
  private final String raw;
  private final long milliseconds;

  public TimeDuration(String value) {
    this.raw = value;
    this.milliseconds = parseToMillis(value);
  }

  private long parseToMillis(String input) {
    String normalized = input.trim().toLowerCase();
    double number;
    long multiplier;

    if (normalized.endsWith("ms")) {
      number = Double.parseDouble(normalized.replace("ms", ""));
      multiplier = 1L;
    } else if (normalized.matches(".*(s|sec|secs|second|seconds)$")) {
      number = Double.parseDouble(normalized.replaceAll("[a-zA-Z]", ""));
      multiplier = 1000L;
    } else if (normalized.matches(".*(min|mins|minute|minutes)$")) {
      number = Double.parseDouble(normalized.replaceAll("[a-zA-Z]", ""));
      multiplier = 60L * 1000;
    } else if (normalized.matches(".*(h|hr|hrs|hour|hours)$")) {
      number = Double.parseDouble(normalized.replaceAll("[a-zA-Z]", ""));
      multiplier = 60L * 60 * 1000;
    } else {
      throw new IllegalArgumentException("Unsupported time unit: " + input);
    }

    return (long) (number * multiplier);
  }

  public long getMilliseconds() {
    return milliseconds;
  }

  @Override
  public Object value() {
    return raw;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(raw);
  }
}
