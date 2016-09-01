package life

import Board._

import scala.util.Random
class Board(val rowsArr:Array[Array[Cell]]) {
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

  def currentAndPrev() = { if (flag) (rowsArr,rowsArr2) else (rowsArr2,rowsArr)}


  private val cacheArr = Array.ofDim[Array[(Int,Int)]](n,m)
//  private val cache = collection.mutable.Map.empty[(Int,Int), (List[(Int,Int)])]
  def neighbours(x:Int,y:Int) = {
    if (cacheArr(x)(y) == null) {
      cacheArr(x)(y) = Array(
        (x-1,y-1), (x-1,y), (x-1,y+1),
        (x,y-1), (x,y+1),
        (x+1,y-1), (x+1,y), (x+1,y+1)
      ).filter{ case (i,j) => i >= 0 && i < n && j >= 0 && j < m}
    }
    cacheArr(x)(y)
//    cache.getOrElseUpdate((x,y), List(
//      (x-1,y-1), (x-1,y), (x-1,y+1),
//      (x,y-1), (x,y+1),
//      (x+1,y-1), (x+1,y), (x+1,y+1)
//    ).filter{ case (i,j) => i >= 0 && i < n && j >= 0 && j < m} )
  }

  def step() = {
    val (board1,board2) = currentAndPrev()
    for (row <- 0 until n) {
      for (col <- 0 until m) {
        stepCell(row,col,board1,board2)
      }
    }
    toggle()
  }


  def stepCell(x:Int,y:Int,current:Array[Array[Cell]],next:Array[Array[Cell]])= {
    val cell = current(x)(y)
    val aliveNbrs = neighbours(x,y).count{case (i,j) => current(i)(j) == Live}
    (cell,aliveNbrs) match {
      case (Live,a) if a < 2 => next(x)(y) = Dead
      case (Live,a) if a == 2 || a == 3 => next(x)(y) = Live
      case (Live,a) if a > 3 => next(x)(y) = Dead
      case (Dead,a) if a == 3 => next(x)(y) = Live
      case _ => next(x)(y) = Dead
    }
  }

  override def equals(obj: scala.Any): Boolean = obj match {
    case (board2: Board) => (this.rowsArr sameElements board2.rowsArr) && (this.rowsArr2 sameElements board2.rowsArr2)
    case _ => false
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

  def randomBoard(n:Int,m:Int,density:Double = 0.1): Board = {
    def col() = {
      val colArr = Array.fill(m)(Dead: Cell)
      for (j <- 0 until m) {
        if (Random.nextFloat() <= density) {
          colArr(j) = Live
        }
      }
      colArr
    }
    val boardArr = Array.fill(n)(col())
    Board(boardArr)
  }


}