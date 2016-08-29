package life

import Board._

import scala.util.Random
class Board(private val rowsArr:Array[Array[Cell]]) {
  val rowsArr2 = rowsArr.clone().map(_.clone())
  var flag = true
  val n = rowsArr.length
  val m = rowsArr(0).length

  override def toString: String = {
    rows().map(row => row.mkString("")).mkString("\n")
  }

  def toggle() = {flag = !flag}

  def rows() = {
    if (flag) rowsArr else rowsArr2
  }

  def neighbours(x:Int,y:Int) = {
    List(
      (x-1,y-1), (x-1,y), (x-1,y+1),
      (x,y-1), (x,y+1),
      (x+1,y-1), (x+1,y), (x+1,y+1)
    ).filter{ case (i,j) => i >= 0 && i < n && j >= 0 && j < m}
  }

  def step() = {
    for (row <- 0 until n) {
      for (col <- 0 until m) {
        stepCell(row,col)
      }
    }
    toggle()
  }


  def stepCell(x:Int,y:Int)= {
    val (board1,board2) = if (flag) (rowsArr,rowsArr2) else (rowsArr2,rowsArr)
    val cell = board1(x)(y)
    val aliveNbrs = neighbours(x,y).count{case (x,y) => board1(x)(y) == Live}
    (cell,aliveNbrs) match {
      case (Live,a) if a < 2 => board2(x)(y) = Dead
      case (Live,a) if a == 2 || a == 3 => board2(x)(y) = Live
      case (Live,a) if a > 3 => board2(x)(y) = Dead
      case (Dead,a) if a == 3 => board2(x)(y) = Live
      case _ => ()
    }
  }

}

object Board {
  sealed trait Cell {
    override def toString: String = this match {
      case Live => "*"
      case Dead => "-"
    }
  }
  case object Live extends Cell
  case object Dead extends Cell

  def apply(rows:Array[Array[Cell]]) = new Board(rows)

  def apply(string:String):Board = {
    val rowStrings = string.trim.split("\n")

    Board(rowStrings.map { rowString =>
      val cellStrings = rowString.trim.toList
      val rowValue: List[Cell] = cellStrings.map {
        case '*' => Live
        case _ => Dead
      }
      rowValue.toArray
    })
  }

  def randomBoard(n:Int,m:Int): Board = {
    def col() = {
      val colArr = Array.fill(m)(Dead: Cell)
      for (j <- 0 until m) {
        if (Random.nextFloat() > 0.5) {
          colArr(j) = Live
        }
      }
      colArr
    }
    val boardArr = Array.fill(n)(col())
    Board(boardArr)
  }

  def board1: Board = {
    val boardStr =
      """
        |*--*-
        |----*
        |-----
        |-----
        |*---*
      """.stripMargin
    Board(boardStr)
  }

}