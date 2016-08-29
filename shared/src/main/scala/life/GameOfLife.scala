package life

import java.io.PrintWriter

import Board._

object GameOfLife {

  def main(args:Array[String]): Unit = {
    val board = randomBoard(10,10)

    val pw = new PrintWriter(System.out)
    var fps = 60
    if (args.length > 0) {
      fps = args(0).toInt
    }
    def waitTime() = 1000/fps

    println(f"$fps FPS. Starting board: ")
    println(board)

    while (true) {
      pw.println(board)
      pw.print(f"\u001b[${board.n}A")
      pw.print(f"\u001b[${board.m}D")
      pw.flush()
      board.step()
    }
    pw.close()
  }

}
