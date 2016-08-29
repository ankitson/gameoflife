package life

import life.Board.Live

import scala.scalajs.js
import scala.scalajs.js._
import scala.scalajs.js.Any._
import js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.ext.Color

object GameOfLifeJS extends js.JSApp {
  @js.native
  trait EventName extends js.Object {
    type EventType <: dom.Event
  }

  object EventName {
    def apply[T <: dom.Event](name: String): EventName { type EventType = T } =
      name.asInstanceOf[EventName { type EventType = T }]

    val onmousedown = apply[dom.MouseEvent]("onmousedown")
  }

  @js.native
  trait ElementExt extends js.Object {
    def addEventListener(name: EventName)(
      f: js.Function1[name.EventType, _]): Unit
  }


  def main(): Unit = {
    println("hello from scalajs")

    val board = Board.board1
    def draw(): Unit = {
      println("drawing board: \n" + board)
      val canvas = dom.document.getElementById("life").asInstanceOf[dom.html.Canvas]
      val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

      canvas.width = canvas.offsetWidth.toInt
      canvas.height = canvas.offsetHeight.toInt
      //println(s"$cwidth x $cheight canvas")

      val cellWidth = 100
      val cellHeight = 100

      val totalWidth = cellWidth * board.m
      val totalHeight = cellHeight * board.n

      ctx.fillStyle = Color.White.toString()
      ctx.fillRect(0, 0, totalWidth, totalHeight)
      ctx.strokeStyle = Color.Black.toString
      ctx.lineWidth = 3
      ctx.moveTo(0,0)

      for (row <- 0 until board.m) {
        ctx.moveTo(0,0)
        for (col <- 0 until board.n) {
          val xcoord = col * cellWidth
          val ycoord = row * cellHeight
          ctx.moveTo(xcoord, ycoord)
          if (board.rows()(row)(col) == Live) {
            ctx.fillStyle = Color.Red.toString()
          } else {
            ctx.fillStyle = Color.White.toString
          }
          val endX = xcoord+cellWidth
          val endY = ycoord+cellHeight
          val size = (endX-xcoord,endY-ycoord)
          ctx.fillRect(xcoord, ycoord, endX, endY)
          ctx.strokeRect(xcoord, ycoord, endX,endY)
        }
      }
      board.step()
    }
    //draw()
    js.timers.setInterval(1000) {
      draw()
    }


  }
}
