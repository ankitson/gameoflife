package life

import life.Board.Live

import scala.scalajs.js
import scala.scalajs.js._
import scala.scalajs.js.Any._
import js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.KeyboardEvent
import org.scalajs.dom.ext.Color
import org.scalajs.dom.raw.MouseEvent

import scala.collection.mutable

object GameOfLifeJS extends js.JSApp {

  var run = true
  var draws = 0
  val n = 60
  val m = 60
  val board = Board.randomBoard(n,m,0.1)

  val canvas = dom.document.getElementById("life").asInstanceOf[dom.html.Canvas]
  val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
  canvas.width = canvas.offsetWidth.toInt
  canvas.height = canvas.offsetHeight.toInt
  val cellWidth = (canvas.width-2)/board.m
  val cellHeight = (canvas.height-2)/board.n

  val totalWidth = cellWidth * board.m
  val totalHeight = cellHeight * board.n
  ctx.strokeStyle = Color.Black.asInstanceOf[Any]
  ctx.lineWidth = 3
  ctx.moveTo(0,0)

  dom.document.onkeydown = (event:KeyboardEvent) => {
    if (event.keyCode == 39) draw()
  }

  val visited = mutable.Set[(Int,Int)]()
  def draw(): Unit = {
    for (row <- 0 until board.m) {
      for (col <- 0 until board.n) {
        val xcoord = col * cellWidth
        val ycoord = row * cellHeight
        if (board.rows()(row)(col) == Live) {
          ctx.fillStyle = Color.Red.asInstanceOf[Any]
          visited.add((row, col))
        } else {
          if (visited contains (row,col)) {
            ctx.fillStyle = "rgba(0,255,0,0.2)"
          }
          else {
            ctx.fillStyle = Color.White.asInstanceOf[Any]
          }
        }
        val endX = xcoord+cellWidth
        val endY = ycoord+cellHeight
        val size = (endX-xcoord,endY-ycoord)

        ctx.fillRect(xcoord, ycoord, endX, endY)
        ctx.strokeRect(xcoord, ycoord, endX,endY)
      }
    }
    board.step()

    draws += 1
    if (run) dom.window.requestAnimationFrame((_:Double) => draw())
  }

  def main(): Unit = {
    println("hello from scalajs")

    dom.document.onclick = {(evt:dom.MouseEvent) =>
        run = !run
        if (run) dom.window.requestAnimationFrame((_:Double) => draw())
    }

    dom.window.setInterval({ () =>
      println(s"$draws draws per second")
      draws = 0
    },1000)

    dom.window.requestAnimationFrame((d:Double) => draw())
  }
}
