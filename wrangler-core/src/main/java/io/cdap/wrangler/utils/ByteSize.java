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


 package io.cdap.wrangler.utils;

 import com.google.gson.JsonElement;
 import com.google.gson.JsonPrimitive;
 
 /**
  * A token representing a byte size value like "10KB", "1.5MB", etc.
  * Provides methods to get the size in bytes.
  */
 
 public class ByteSize implements Token {
   private final String raw;
   private final long bytes;
 
   public ByteSize(String value) {
     this.raw = value;
     this.bytes = parseToBytes(value);
   }
 
   private long parseToBytes(String input) {
     if (input == null || input.isEmpty()) {
       throw new IllegalArgumentException("Byte size value cannot be null or empty.");
     }
 
     String normalized = input.trim().toUpperCase();
     double number;
     long multiplier;
 
     if (normalized.endsWith("KB")) {
       number = parseNumber(normalized, "KB");
       multiplier = 1024L;
     } else if (normalized.endsWith("MB")) {
       number = parseNumber(normalized, "MB");
       multiplier = 1024L * 1024;
     } else if (normalized.endsWith("GB")) {
       number = parseNumber(normalized, "GB");
       multiplier = 1024L * 1024 * 1024;
     } else if (normalized.endsWith("TB")) {
       number = parseNumber(normalized, "TB");
       multiplier = 1024L * 1024 * 1024 * 1024;
     } else {
       throw new IllegalArgumentException("Unsupported byte unit in value: " + input);
     }
 
     return (long) (number * multiplier);
   }
 
   private double parseNumber(String input, String unit) {
     try {
       return Double.parseDouble(input.substring(0, input.length() - unit.length()));
     } catch (NumberFormatException e) {
       throw new IllegalArgumentException("Invalid numeric part in byte size: " + input);
     }
   }
 
   public long getBytes() {
     return bytes;
   }
 
   @Override
   public Object value() {
     return raw;
   }
 
   @Override
   public TokenType type() {
     return TokenType.BYTE_SIZE;
   }
 
   @Override
   public JsonElement toJson() {
     return new JsonPrimitive(raw);
   }
 }
 