package life

import java.util.concurrent.TimeUnit

import life.Board.{Cell, Dead, Live}
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
*/
object Bench {
  @State(Scope.Benchmark)
  class BenchState {
    val boardStr =
      """
        |-*------*-
        |-*------*-
        |-*------*-
      """.stripMargin
    var board = Board(boardStr)
    val n = board.n
    val m = board.m

    val arr = Array.fill(n)(Array.fill(m)(Dead: Cell))
  }
}

class Bench {
  import Bench._

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def setArray(state:BenchState) = {
    state.arr(0)(0) = Live
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def setBoardIdx(state:BenchState) = {
    state.board.rows()(0)(0) = Live
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def compareBoardIdx(state:BenchState) = {
    state.board.rows()(0)(0) == Live
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def neighboursBench(state:BenchState) = {
    state.board.neighbours(0,0)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def stepCellBench(state:BenchState) = {
    state.board.stepCell(0,0)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def stepBoardBench(state:BenchState) = {
    state.board.step()
  }

}
