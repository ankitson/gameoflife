package life

import java.io.PrintWriter

import life.core.Board._

object GameOfLifeConsole {

  def main(args:Array[String]): Unit = {
    val board = randomRectBoard(10,10)

    val pw = new PrintWriter(System.out)

    def up(n:Int) = f"\u001b[${n}A"
    def down(n:Int) = f"\u001b[${n}B"
    def right(n:Int) = f"\u001b[${n}C"
    def left(n:Int) = f"\u001b[${n}D"

    while (true) {
      Thread.sleep(1000/1)
      pw.print(board)
      pw.print(up(board.m-1))
      pw.print(left(board.n))
      pw.flush()
      board.step()
    }
    pw.close()
  }

}
