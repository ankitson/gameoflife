package life

import java.util.concurrent.TimeUnit

import life.core.Board
import org.openjdk.jmh.annotations._

/**
  [info] Benchmark                Mode    Cnt   Score   Error  Units
  [info] Bench.neighboursBench  sample  85415  14.701 ± 0.321  us/op
  [info] Bench.setArray         sample  55441   0.084 ± 0.009  us/op
  [info] Bench.stepBoardBench   sample  73937  17.021 ± 0.379  us/op
  [info] Bench.stepCellBench    sample  90870   0.129 ± 0.008  us/op

  [info] Benchmark                Mode    Cnt   Score   Error  Units
  [info] Bench.neighboursBench  sample  92240  13.474 ± 0.120  us/op
  [info] Bench.setArray         sample  55586   0.093 ± 0.007  us/op
  [info] Bench.stepBoardBench   sample  77586  16.121 ± 0.290  us/op
  [info] Bench.stepCellBench    sample  95359   0.491 ± 0.034  us/op

  [info] Benchmark                Mode    Cnt    Score     Error   Units
  [info] Bench.neighboursBench   thrpt      5    0.065 ±   0.013  ops/us
  [info] Bench.setArray          thrpt      5  490.661 ± 138.300  ops/us
  [info] Bench.stepBoardBench    thrpt      5    0.032 ±   0.005  ops/us
  [info] Bench.stepCellBench     thrpt      5    1.267 ±   0.173  ops/us
  [info] Bench.neighboursBench  sample  86272   14.490 ±   0.326   us/op
  [info] Bench.setArray         sample  36018    0.124 ±   0.019   us/op
  [info] Bench.stepBoardBench   sample  66364   37.519 ±   0.988   us/op
  [info] Bench.stepCellBench    sample  59732    0.893 ±   0.047   us/op

  [info] Benchmark                Mode     Cnt       Score       Error   Units
  [info] Bench.neighboursBench   thrpt      15      62.134 ±    12.581  ops/ms
  [info] Bench.setArray          thrpt      15  495771.250 ± 17050.030  ops/ms
  [info] Bench.stepBoardBench    thrpt      15      34.497 ±     1.370  ops/ms
  [info] Bench.stepCellBench     thrpt      15    1269.801 ±    52.338  ops/ms
  [info] Bench.setArray         sample  168707      ≈ 10⁻⁴               ms/op

  [info] Benchmark               Mode  Cnt       Score       Error   Units
  [info] Bench.neighboursBench  thrpt   15      70.072 ±     4.689  ops/ms
  [info] Bench.setArray         thrpt   15  361851.310 ± 15388.100  ops/ms
  [info] Bench.stepBoardBench   thrpt   15      60.932 ±     2.460  ops/ms
  [info] Bench.stepCellBench    thrpt   15    2445.558 ±   146.324  ops/ms

  [info] Benchmark               Mode  Cnt       Score       Error   Units
  [info] Bench.neighboursBench  thrpt   15      71.227 ±     4.045  ops/ms
  [info] Bench.setArray         thrpt   15  285422.077 ± 10582.309  ops/ms
  [info] Bench.setBoardIdx      thrpt   15  365710.405 ±  9409.180  ops/ms
  [info] Bench.stepBoardBench   thrpt   15      62.946 ±     2.401  ops/ms
  [info] Bench.stepCellBench    thrpt   15    2402.431 ±   166.437  ops/ms

  (speedup by memoizing neighbour indexes)
  [info] Benchmark               Mode  Cnt       Score       Error   Units
  [info] Bench.compareBoardIdx  thrpt   15  205324.202 ± 49465.448  ops/ms
  [info] Bench.neighboursBench  thrpt   15   20121.666 ±   912.573  ops/ms
  [info] Bench.setArray         thrpt   15  588137.165 ± 18610.425  ops/ms
  [info] Bench.setBoardIdx      thrpt   15  444062.475 ±  3899.233  ops/ms
  [info] Bench.stepBoardBench   thrpt   15     418.195 ±     3.915  ops/ms
  [info] Bench.stepCellBench    thrpt   15   17440.708 ±   355.982  ops/ms

  (speedup by using 2d array instead of mutable map to memoize neighbours)
  [info] Benchmark             Mode  Cnt      Score      Error   Units
  [info] Bench.stepCellBench  thrpt   15  47474.047 ± 4534.626  ops/ms
  [info] Benchmark              Mode  Cnt      Score     Error   Units
  [info] Bench.stepBoardBench  thrpt   15    726.390 ±   5.042  ops/ms
  [info] Bench.stepCellBench   thrpt   15  37768.831 ± 392.765  ops/ms
  [info] Bench.neighboursBench thrpt   15    271244.579 ±   2451.076  ops/ms
  [info] Benchmark              Mode  Cnt         Score        Error  Units
  [info] Bench.stepBoardBench  thrpt   15      726,558.775 ±    6571.231  ops/s
  [info] Bench.stepCellBench   thrpt   15   37,414,962.430 ±  746148.292  ops/s (37M cells per sec!)
  [info] Bench.neighboursBench thrpt   15  268,707,348.584 ± 2761828.773  ops/s

  [info] Benchmark                 Mode  Cnt          Score         Error  Units
  [info] Bench.neighboursBench    thrpt   15  264033259.090 ± 8732833.035  ops/s
  [info] Bench.stepBoardBench10    avgt   15       5112.550 ±     629.471  ns/op
  [info] Bench.stepBoardBench100   avgt   15     609772.782 ±    7485.980  ns/op
  [info] Bench.stepBoardBench200   avgt   15    2982256.417 ±   55347.973  ns/op
  [info] Bench.stepBoardBench300   avgt   15    6727552.195 ±  120683.051  ns/op
  [info] Bench.stepBoardBench400   avgt   15   11475042.472 ±  135280.182  ns/op
  [info] Bench.stepCellBench       avgt   15         19.101 ±       0.150  ns/op


  [info] Benchmark                 Mode  Cnt           Score           Error  Units
  [info] Bench.stepBoardBench10    avgt   15        4721.124 ±        84.902  ns/op
  [info] Bench.stepBoardBench100   avgt   15      577198.836 ±     14571.000  ns/op
  [info] Bench.stepBoardBench200   avgt   15     3150383.700 ±     49929.827  ns/op
  [info] Bench.stepBoardBench400   avgt   15    12940451.838 ±    198338.619  ns/op
  [info] Bench.stepBoardBench800   avgt   15    61090041.658 ±  52187004.916  ns/op
  [info] Bench.stepBoardBench1600  avgt   15   186352211.656 ±   3716979.553  ns/op
  [info] Bench.stepBoardBench2000  avgt   15  1853235535.333 ± 284928833.118  ns/op

  [info] Benchmark             (boardSize)  Mode  Cnt     Score    Error  Units
  [info] Bench.stepBoardBench          10  avgt   15     5.180 ±  0.915  us/op
  [info] Bench.stepBoardBench          11  avgt   15     5.559 ±  0.046  us/op
  [info] Bench.stepBoardBench          12  avgt   15     6.952 ±  0.071  us/op
  [info] Bench.stepBoardBench          13  avgt   15     8.132 ±  0.303  us/op
  [info] Bench.stepBoardBench          14  avgt   15    14.519 ±  5.479  us/op
  [info] Bench.stepBoardBench          15  avgt   15    14.580 ±  3.441  us/op
  [info] Bench.stepBoardBench          16  avgt   15    11.836 ±  0.390  us/op
  [info] Bench.stepBoardBench          32  avgt   15    51.232 ±  0.795  us/op
  [info] Bench.stepBoardBench          40  avgt   15    77.333 ±  0.935  us/op
  [info] Bench.stepBoardBench          50  avgt   15   121.862 ±  1.616  us/op
  [info] Bench.stepBoardBench          64  avgt   15   197.212 ±  3.581  us/op
  [info] Bench.stepBoardBench          70  avgt   15   251.969 ±  4.188  us/op
  [info] Bench.stepBoardBench          80  avgt   15   357.502 ± 15.886  us/op
  [info] Bench.stepBoardBench          90  avgt   15   613.803 ± 10.903  us/op
  [info] Bench.stepBoardBench         128  avgt   15  1205.374 ± 12.606  us/op

  [info] Benchmark             (boardSize)  Mode  Cnt        Score        Error  Units
  [info] Bench.stepBoardBench          10  avgt   15        4.760 ±      0.277  us/op
  [info] Bench.stepBoardBench          20  avgt   15       18.402 ±      0.536  us/op
  [info] Bench.stepBoardBench          40  avgt   15       97.232 ±      2.449  us/op
  [info] Bench.stepBoardBench          80  avgt   15      452.500 ±     19.920  us/op
  [info] Bench.stepBoardBench         160  avgt   15     2033.330 ±     53.310  us/op
  [info] Bench.stepBoardBench         240  avgt   15     4178.972 ±     76.128  us/op
  [info] Bench.stepBoardBench         320  avgt   15     7502.670 ±    184.759  us/op
  [info] Bench.stepBoardBench         480  avgt   15    18446.591 ±    440.301  us/op
  [info] Bench.stepBoardBench         512  avgt   15    20441.050 ±    449.956  us/op
  [info] Bench.stepBoardBench        1024  avgt   15    77561.251 ±   4045.445  us/op
  [info] Bench.stepBoardBench        1280  avgt   15   131542.028 ±   6417.883  us/op
  [info] Bench.stepBoardBench        1536  avgt   15   182097.619 ±   2704.411  us/op
  [info] Bench.stepBoardBench        1792  avgt   15   251026.411 ±  21376.571  us/op
  [info] Bench.stepBoardBench        2048  avgt   15  1594271.271 ± 546924.223  us/op
   todo: should be roughly n^2.
  */
object Bench {
  @State(Scope.Benchmark)
  class BenchState {
    val boardSizes = Array(
      10,20,40,80,160,240,320,480,512,1024,1280,1536,1792,2048
    )
    val boards = boardSizes.map(n => (n,Board.randomRectBoard(n,n,0.1))).toMap

    @Param(Array("10","20","40","80","160","240","320","480","512","1024","1280","1536","1792","2048"))
    var boardSize: Int = _
  }

}

class Bench {
  import Bench._

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  def stepBoardBench(state:BenchState) = {
    state.boards(state.boardSize).step()
  }

//  @Benchmark
//  @BenchmarkMode(Array(Mode.Throughput))
//  @OutputTimeUnit(TimeUnit.SECONDS)
//  def neighboursBench(state:BenchState) = {
//    state.boards(10).neighbours(1,1)
//  }
//
//  @Benchmark
//  @BenchmarkMode(Array(Mode.AverageTime))
//  @OutputTimeUnit(TimeUnit.NANOSECONDS)
//  def stepCellBench(state:BenchState) = {
//    state.boards(10).stepCell(1,1)
//  }

}
