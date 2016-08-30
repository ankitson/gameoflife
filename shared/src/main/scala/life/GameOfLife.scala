package life

import java.io.PrintWriter

import Board._

object GameOfLife {

  def main(args:Array[String]): Unit = {
    val board = randomBoard(10,10)

    val pw = new PrintWriter(System.out)

    println(f"Starting board: ")
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
