package life

import life.Board.{Dead, Live}
import utest._
object TestBoard extends TestSuite {

  var boardStr =
    """
      |-*------*-
      |-*------*-
      |-*------*-
    """.stripMargin
  var board = Board(boardStr)

  val tests = this {

    'split {
      val line = "123"
      val splits = line.split("").toList
      //println("splits: " + splits) //JVM => ("1","2","3"). JS => ("","1","2","3")
      //assert (splits.length == 3) //fails on JS

      val chars = line.toList
      assert (chars == List('1','2','3'))
    }

    'step {
      board.step()
      for (col <- 0 until board.rows()(0).length) {
        assert (board.rows()(0)(col) == Dead)
        assert (board.rows()(2)(col) == Dead)
        if (col != 0 && col != 1 && col != 2 && col != 7 && col !=8 && col != 9)
          assert (board.rows()(1)(col) == Dead)
        else
          assert (board.rows()(1)(col) == Live)
      }
      board.step()
      board.rows().zipWithIndex.foreach{case (row,idx) => assert ( row sameElements Board(boardStr).rows()(idx) ) }

      boardStr =
        """
          |*--*-
          |----*
          |-----
          |-----
          |*---*
        """.stripMargin
      board = Board(boardStr)
      board.step()
      println("after step: \n"+board)
    }
  }

}
