
# Data Prep – Wrangler Extension

![cm-available](https://cdap-users.herokuapp.com/assets/cm-available.svg)
![cdap-transform](https://cdap-users.herokuapp.com/assets/cdap-transform.svg)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This project extends CDAP Wrangler by introducing a new custom directive: `aggregate-stats`.

---

## 🚀 New Directive: `aggregate-stats`

**Syntax:**
```wrangler
aggregate-stats :<size_column> :<time_column> <output_size_col> <output_time_col>
```

**Example:**
```wrangler
aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec
```

### 🧠 Functionality

- Parses string-based byte values (e.g., `10KB`, `1.5MB`) using `ByteSize`
- Parses time durations (e.g., `5ms`, `2s`) using `TimeDuration`
- Converts and outputs:
  - Total size in megabytes (MB)
  - Total time in seconds

---

## ✅ Test Coverage

### Unit Tests
- Core directive logic (normal + malformed inputs)
- `ByteSize` and `TimeDuration` parsing

### Grammar and Compilation
- Valid/invalid syntax parsing via `GrammarBasedParser`
- Compilation via `RecipeCompiler`

### Integration
- End-to-end execution using `TestingRig`

---

## 📄 Example Input

| data_transfer_size | response_time |
|--------------------|---------------|
| 10MB               | 2s            |
| 5MB                | 3s            |

### Output:

| total_size_mb | total_time_sec |
|----------------|----------------|
| 15.0           | 5.0            |

---

## 🧩 Files Implemented

- `AggregateStats.java` – Custom directive
- `ByteSize.java` & `TimeDuration.java` – Parser utility classes
- `AggregateStatsTest.java` – Comprehensive test suite
- Grammar additions to `Directives.g4`

---

## 🔧 Resources & Contribution

For learning more about how to write custom directives in CDAP:

- [Custom Directive Documentation](wrangler-docs/custom-directive.md)
- [Directive Grammar Info](wrangler-docs/grammar/grammar-info.md)
- [Token Types Supported](../api/src/main/java/io/cdap/wrangler/api/parser/TokenType.java)

---

## 🛡 License

Licensed under the Apache License, Version 2.0. Full text: [LICENSE](http://www.apache.org/licenses/LICENSE-2.0)
