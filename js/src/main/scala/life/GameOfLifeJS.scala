package life

import life.Board.Live

import scala.scalajs.js
import scala.scalajs.js.Any._
import js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.KeyboardEvent
import org.scalajs.dom.ext.{Color, KeyCode}
import org.scalajs.dom.raw.{HTMLCanvasElement, HTMLInputElement}

@JSExport
object GameOfLifeJS extends js.JSApp {

  var run = true
  var board = Board.randomBoard(10,10,0.1)
  val canvas = dom.document.getElementById("life").asInstanceOf[dom.html.Canvas]
  canvas.width = canvas.offsetWidth.toInt
  canvas.height = canvas.offsetHeight.toInt
  val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  var cellWidth = canvas.width/board.m
  var cellHeight = canvas.height/board.n

  def init() = {
    cellWidth = canvas.width/board.m
    cellHeight= canvas.height/board.n
    ctx.strokeStyle = Color.Black.toString()
    ctx.lineWidth = 1
    ctx.moveTo(0,0)

    ctx.clearRect(0,0,canvas.width,canvas.height)
  }


  var stats = js.Dynamic.newInstance(js.Dynamic.global.Stats)()
  stats.showPanel( 1 ); // 0: fps, 1: ms, 2: mb, 3+: custom
  dom.document.body.appendChild( stats.dom.asInstanceOf[org.scalajs.dom.raw.Node] )

  def draw(): Unit = {
    stats.begin()
    val (boardArr,boardArr2) = board.currentAndPrev()
    for (row <- 0 until board.m) {
      for (col <- 0 until board.n) {
        val xcoord = col * cellWidth
        val ycoord = row * cellHeight
        if (boardArr(row)(col) != boardArr2(row)(col)) {
          if (boardArr(row)(col) == Live) {
            ctx.fillStyle = "rgba(255,0,0,1)"
          } else {
            ctx.fillStyle = "rgba(255,255,255,1)"
          }
          val startX = xcoord
          val startY = ycoord
          val width = cellWidth
          val height = cellHeight
          ctx.fillRect(startX,startY,width,height)
        }
      }
    }
    board.step()
    stats.end()

    if (run) dom.window.requestAnimationFrame((_:Double) => draw())
  }

  def main(): Unit = {
    println("hello from scalajs")

    dom.document.onclick = {(evt:dom.MouseEvent) =>
        run = !run
        if (run) {
          init()
          dom.window.requestAnimationFrame((_:Double) => draw())
        }
    }


    dom.document.onkeydown = (event:KeyboardEvent) => {
      if (event.keyCode == KeyCode.Right) draw()
      if (event.keyCode == KeyCode.Space) run = !run; if (run) { init(); dom.window.requestAnimationFrame((_:Double) => draw()) }
    }

    dom.document.getElementById("size").asInstanceOf[HTMLInputElement].onkeypress = {(evt:KeyboardEvent) =>
      run = false
      try {
        val newSize = dom.document.getElementById("size").asInstanceOf[HTMLInputElement].value.toInt
        dom.document.getElementById("sizeShow").innerHTML = newSize.toString
        board = Board.randomBoard(newSize, newSize)
        run = true
        init()
        dom.window.requestAnimationFrame((_: Double) => draw())
      } catch {
        case _ => ()
      }
    }

    init()
    dom.window.requestAnimationFrame((_:Double) => draw())
  }
}
