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
  is setInterval faster than reqAnimFrame? (shouldnt be)

  seeing multiple animFrame calls in 1 frame in chrome is bad. means its slow. why?

  Adding this reduces perf dramatically and screws up the fps monitor...??
    dom.document.getElementById("life").asInstanceOf[HTMLDivElement].onkeypress = (event:KeyboardEvent) => {
      if (event.keyCode == KeyCode.Right) draw(dom.window.performance.now())
      if (event.keyCode == KeyCode.Space) toggleRun()
      if (event.keyCode == KeyCode.N) board = Board.randomBoard(board.n,board.m,0.1); startRun()
    }
**/


@JSExport
object GameOfLifeJS extends js.JSApp {
  def initGrid(rows: Int, cols: Int) = {
    val gridCanvas = dom.document.getElementById("life-grid").asInstanceOf[dom.html.Canvas]
    gridCanvas.width = gridCanvas.offsetWidth.toInt
    gridCanvas.height = gridCanvas.offsetHeight.toInt
    val gridCtx = gridCanvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    gridCtx.fillStyle = Color.White.toString()
    gridCtx.strokeStyle = "rgba(220,220,220,1)"
    gridCtx.lineWidth = 2

    gridCtx.clearRect(0, 0, gridCanvas.width, gridCanvas.height)
    for (start <- 0.0 to gridCanvas.height by gridCanvas.height.toDouble/rows) {
      gridCtx.beginPath()
      gridCtx.moveTo(0, start)
      gridCtx.lineTo(gridCanvas.width, start)
      gridCtx.closePath()
      gridCtx.stroke()
    }
    for (start <- 0.0 to gridCanvas.width by gridCanvas.width.toDouble/cols) {
      gridCtx.beginPath()
      gridCtx.moveTo(start, 0)
      gridCtx.lineTo(start, gridCanvas.height)
      gridCtx.closePath()
      gridCtx.stroke()
    }
    gridCtx
  }

  def initBoard() = {
    val canvas = dom.document.getElementById("life-board").asInstanceOf[dom.html.Canvas]
    canvas.width = canvas.offsetWidth.toInt
    canvas.height = canvas.offsetHeight.toInt
    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    ctx.fillStyle = Color.White.toString()
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    ctx.fillRect(0, 0, canvas.width, canvas.height)
    ctx
  }

  def init(board:Board) = {
    val gridCtx = initGrid(board.m,board.n)
    val boardCtx = initBoard()
    cellWidth = boardCtx.canvas.width.toDouble / board.n
    cellHeight = boardCtx.canvas.height.toDouble / board.m
  }

  var cellWidth: Double = _
  var cellHeight: Double = _

  def main() = {
    var run = true
    var lastCall = 0d
    val fpsDisplay = dom.document.getElementById("fps")
    var animFrameHandle = 0

    var board = Board.randomBoard(10, 10, 0.1)
    initGrid(board.m, board.n)
    val ctx = initBoard()

    val stats = js.Dynamic.newInstance(js.Dynamic.global.Stats)()
    stats.showPanel(1); // 0: fps, 1: ms, 2: mb, 3+: custom
    dom.document.body.appendChild(stats.dom.asInstanceOf[org.scalajs.dom.raw.Node])

    def draw(cellWidth: Double, cellHeight: Double)(timestamp: Double): Unit = {
      stats.begin()
      val duration = timestamp - lastCall
      fpsDisplay.innerHTML = duration.toString + "ms/frame" + "<br>" + (1000 / duration) + "frames/sec"
      lastCall = timestamp

      val (boardArr, boardArr2) = board.currentAndPrev()
      for (row <- 0 until board.m; col <- 0 until board.n) {
        val cellChanged = boardArr(row)(col) != boardArr2(row)(col)
        if (cellChanged) {
          if (boardArr(row)(col) == Live) {
            ctx.fillStyle = "rgba(255,0,0,1)"
          } else {
            ctx.fillStyle = "rgba(255,255,255,1)"
          }

          val startX = (cellWidth * col) + 1
          val startY = (cellHeight * row) + 1

          ctx.fillRect(startX, startY, cellWidth, cellHeight)
        }
      }
      board.step()
      if (run) animFrameHandle = dom.window.requestAnimationFrame(draw(cellWidth, cellHeight) _)
      stats.end()
    }

    def bindListeners() = {
      dom.document.getElementById("size").asInstanceOf[HTMLInputElement].onkeypress = { (evt: KeyboardEvent) =>
        evt.keyCode match {
          case KeyCode.Enter =>
            Try(dom.document.getElementById("size").asInstanceOf[HTMLInputElement].value.toInt) match {
              case Failure(_) => ()
              case Success(size) =>
                dom.document.getElementById("sizeShow").innerHTML = size.toString
                board = Board.randomBoard(size, size)
                refreshBoard(board)
            }
          case _ => ()
        }
      }

      def refreshBoard(board:Board) = {
        dom.window.cancelAnimationFrame(animFrameHandle)
        init(board)
        dom.window.requestAnimationFrame(draw(cellWidth,cellHeight) _)
      }

      dom.window.onkeypress = { (evt: KeyboardEvent) =>
        evt.keyCode match {
          case KeyCode.Space =>
            evt.preventDefault()
            run = !run
            if (run) dom.window.requestAnimationFrame(draw(cellWidth, cellHeight) _)
            else dom.window.cancelAnimationFrame(animFrameHandle)
          case KeyCode.N | 110 => //upper or lowercase N
            evt.preventDefault()
            if (!run) draw(cellWidth,cellHeight)(System.currentTimeMillis())
          case _ => ()
        }
      }
    }

    bindListeners()

    dom.window.requestAnimationFrame(draw(cellWidth, cellHeight) _)
  }

}
