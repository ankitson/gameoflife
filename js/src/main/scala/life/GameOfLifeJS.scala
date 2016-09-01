package life

import life.Board.Live
import org.scalajs.dom
import org.scalajs.dom.ext.{Color, KeyCode}
import org.scalajs.dom.raw.HTMLInputElement
import org.scalajs.dom.KeyboardEvent

import scala.scalajs.js
import scala.scalajs.js.Any._
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success, Try}


/**
  seeing multiple animFrame calls in 1 frame in chrome is bad. means its slow. why?
**/


@JSExport
object GameOfLifeJS extends js.JSApp {
  def initGrid(canvas: dom.html.Canvas, rows: Int, cols: Int) = {
    canvas.width = canvas.offsetWidth.toInt
    canvas.height = canvas.offsetHeight.toInt
    val gridCtx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    gridCtx.fillStyle = Color.White.toString()
    gridCtx.strokeStyle = "rgba(220,220,220,1)"
    gridCtx.lineWidth = 2

    gridCtx.clearRect(0, 0, canvas.width, canvas.height)
    for (start <- 0.0 to canvas.height by canvas.height.toDouble/rows) {
      gridCtx.beginPath()
      gridCtx.moveTo(0, start)
      gridCtx.lineTo(canvas.width, start)
      gridCtx.closePath()
      gridCtx.stroke()
    }
    for (start <- 0.0 to canvas.width by canvas.width.toDouble/cols) {
      gridCtx.beginPath()
      gridCtx.moveTo(start, 0)
      gridCtx.lineTo(start, canvas.height)
      gridCtx.closePath()
      gridCtx.stroke()
    }
    gridCtx
  }

  def initBoard(canvas:dom.html.Canvas) = {
    canvas.width = canvas.offsetWidth.toInt
    canvas.height = canvas.offsetHeight.toInt
    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    ctx.fillStyle = Color.White.toString()
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    ctx.fillRect(0, 0, canvas.width, canvas.height)
    ctx
  }


  var cellWidth: Double = _
  var cellHeight: Double = _
  var animFrameHandle = 0
  val stats = js.Dynamic.newInstance(js.Dynamic.global.Stats)()
  var run = true
  var lastCall = 0d
  val fpsDisplay = dom.document.getElementById("fps")

  def drawer(board: Board,boardCtx: dom.CanvasRenderingContext2D,cellWidth: Double,cellHeight: Double) = {
    def draw(timestamp: Double): Unit = {
      stats.begin()
      val duration = timestamp - lastCall
      fpsDisplay.innerHTML = duration.toString + "ms/frame" + "<br>" + (1000 / duration) + "frames/sec"
      lastCall = timestamp

      val (boardArr, boardArr2) = board.currentAndPrev()
      for (row <- 0 until board.m; col <- 0 until board.n) {
        val cellChanged = boardArr(row)(col) != boardArr2(row)(col)
        if (cellChanged) {
          if (boardArr(row)(col) == Live) {
            boardCtx.fillStyle = "rgba(255,0,0,1)"
          } else {
            boardCtx.fillStyle = "rgba(255,255,255,1)"
          }

          val startX = (cellWidth * col) + 1
          val startY = (cellHeight * row) + 1

          boardCtx.fillRect(startX, startY, cellWidth, cellHeight)
        }
      }
      board.step()
      if (run) animFrameHandle = dom.window.requestAnimationFrame(draw _)
      stats.end()
    }
    draw _
  }

  def initAll(boardCanvas:dom.html.Canvas,gridCanvas:dom.html.Canvas,board:Board) = {
    val gridCtx = initGrid(gridCanvas,board.m,board.n)
    val boardCtx = initBoard(boardCanvas)
    run = false
    dom.window.cancelAnimationFrame(animFrameHandle)
    cellWidth = boardCtx.canvas.width.toDouble / board.n
    cellHeight = boardCtx.canvas.height.toDouble / board.m
    val draw = drawer(board,boardCtx,cellWidth,cellHeight)
    animFrameHandle = dom.window.requestAnimationFrame(draw)
    run = true
    draw
  }

  def bindListeners() = {
    dom.document.getElementById("size").asInstanceOf[HTMLInputElement].onkeypress = { (evt: KeyboardEvent) =>
      evt.keyCode match {
        case KeyCode.Enter =>
          Try(dom.document.getElementById("size").asInstanceOf[HTMLInputElement].value.toInt) match {
            case Failure(_) => ()
            case Success(size) =>
              dom.document.getElementById("sizeShow").innerHTML = size.toString
              val board = Board.randomBoard(size, size)
              initAll(
                dom.document.getElementById("life-board").asInstanceOf[dom.html.Canvas],
                dom.document.getElementById("life-grid").asInstanceOf[dom.html.Canvas],
                board
              )
          }
        case _ => ()
      }
    }

    dom.window.onkeypress = (event:KeyboardEvent) => {
      if (event.keyCode == KeyCode.N || event.keyCode == 110) {
        val size = dom.document.getElementById("size").asInstanceOf[HTMLInputElement].value.toInt
        val board = Board.randomBoard(size,size,0.1)
        initAll(
          dom.document.getElementById("life-board").asInstanceOf[dom.html.Canvas],
          dom.document.getElementById("life-grid").asInstanceOf[dom.html.Canvas],
          board
        )
      }
    }


  }
  def main(): Unit = {
    val boardCanvas = dom.document.getElementById("life-board").asInstanceOf[dom.html.Canvas]
    val gridCanvas = dom.document.getElementById("life-grid").asInstanceOf[dom.html.Canvas]

    bindListeners()
    stats.showPanel(1); // 0: fps, 1: ms, 2: mb, 3+: custom
    dom.document.body.appendChild(stats.dom.asInstanceOf[org.scalajs.dom.raw.Node])
    animFrameHandle = dom.window.requestAnimationFrame(initAll(boardCanvas,gridCanvas,Board.randomBoard(10,10,0.1)))
  }

}
