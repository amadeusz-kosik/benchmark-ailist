# Augmented Interval List implementations benchmark
This code benchmarks different implementations of AIList algorithm used to compute overlap join over interval 
data. It is a part of effort of [Interwalled](https://github.com/amadeusz-kosik/interwalled) library. It has also somewhat influenced the code decisions
(e.g. algorithms are reading from Array-based iterators to emulate Apache Spark's implementation for `mapPartitions`
method in RDDs).

[![Unit tests](https://github.com/amadeusz-kosik/benchmark-ailist/actions/workflows/code_quality.yml/badge.svg)](https://github.com/amadeusz-kosik/interwalled/actions/workflows/code_quality.yml)

## Setting up
1. Import the project into SBT.
2. Run `benchmarks/Jmh/run`

## Results 
Running the benchmark on Apple Studio M1 yielded following results:
```
[info] Benchmark                                         (databaseSourceSet)  Mode  Cnt    Score   Error  Units

[info] AIListBenchmark.benchmarkJavaAIListArray                  consecutive  avgt   10  470.450 ± 8.365  ms/op
[info] AIListBenchmark.benchmarkJavaAIListArray                       sparse  avgt   10  465.769 ± 6.001  ms/op
[info] AIListBenchmark.benchmarkJavaAIListArray                  overlapping  avgt   10  462.252 ± 1.091  ms/op
[info] AIListBenchmark.benchmarkJavaAIListArray                      lasting  avgt   10  463.027 ± 0.904  ms/op
[info] AIListBenchmark.benchmarkJavaAIListArray                     outliers  avgt   10  462.151 ± 0.727  ms/op

[info] AIListBenchmark.benchmarkJavaAIListQueue                  consecutive  avgt   10  470.423 ± 0.827  ms/op
[info] AIListBenchmark.benchmarkJavaAIListQueue                       sparse  avgt   10  470.664 ± 1.586  ms/op
[info] AIListBenchmark.benchmarkJavaAIListQueue                  overlapping  avgt   10  471.410 ± 2.888  ms/op
[info] AIListBenchmark.benchmarkJavaAIListQueue                      lasting  avgt   10  471.856 ± 3.873  ms/op
[info] AIListBenchmark.benchmarkJavaAIListQueue                     outliers  avgt   10  470.392 ± 0.772  ms/op

[info] AIListBenchmark.benchmarkScalaAIListArrayInPlace          consecutive  avgt   10   13.294 ± 0.069  ms/op
[info] AIListBenchmark.benchmarkScalaAIListArrayInPlace               sparse  avgt   10   13.009 ± 0.040  ms/op
[info] AIListBenchmark.benchmarkScalaAIListArrayInPlace          overlapping  avgt   10   12.979 ± 0.020  ms/op
[info] AIListBenchmark.benchmarkScalaAIListArrayInPlace              lasting  avgt   10   14.037 ± 0.023  ms/op
[info] AIListBenchmark.benchmarkScalaAIListArrayInPlace             outliers  avgt   10    5.392 ± 0.208  ms/op

[info] AIListBenchmark.benchmarkScalaAIListDeque                 consecutive  avgt   10    5.463 ± 0.079  ms/op
[info] AIListBenchmark.benchmarkScalaAIListDeque                      sparse  avgt   10    5.293 ± 0.103  ms/op
[info] AIListBenchmark.benchmarkScalaAIListDeque                 overlapping  avgt   10    5.263 ± 0.085  ms/op
[info] AIListBenchmark.benchmarkScalaAIListDeque                     lasting  avgt   10    6.396 ± 0.086  ms/op
[info] AIListBenchmark.benchmarkScalaAIListDeque                    outliers  avgt   10    5.523 ± 0.119  ms/op

[info] AIListBenchmark.benchmarkScalaReference                   consecutive  avgt   10  198.379 ± 0.909  ms/op
[info] AIListBenchmark.benchmarkScalaReference                        sparse  avgt   10  187.735 ± 0.971  ms/op
[info] AIListBenchmark.benchmarkScalaReference                   overlapping  avgt   10  188.841 ± 1.083  ms/op
[info] AIListBenchmark.benchmarkScalaReference                       lasting  avgt   10  188.231 ± 0.776  ms/op
[info] AIListBenchmark.benchmarkScalaReference                      outliers  avgt   10  188.361 ± 1.364  ms/op
```

## License
This work is licensed under <a href="https://creativecommons.org/licenses/by-nc-sa/4.0/">CC BY-NC-SA 4.0</a>. 
See [LICENSE](LICENSE) file for full license text.