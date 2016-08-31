package life

import life.Board.Live

import scala.scalajs.js
import scala.scalajs.js.Any._
import js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.{FocusEvent, KeyboardEvent}
import org.scalajs.dom.ext.{Color, KeyCode}
import org.scalajs.dom.raw.{HTMLCanvasElement, HTMLDivElement, HTMLInputElement}


@JSExport
object GameOfLifeJS extends js.JSApp {
  var run = true
  var board = Board.randomBoard(100,100,0.1)

  val canvas = dom.document.getElementById("life-board").asInstanceOf[dom.html.Canvas]
  canvas.width = canvas.offsetWidth.toInt
  canvas.height = canvas.offsetHeight.toInt
  val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  val gridCanvas = dom.document.getElementById("life-grid").asInstanceOf[dom.html.Canvas]
  gridCanvas.width = gridCanvas.offsetWidth.toInt
  gridCanvas.height = gridCanvas.offsetHeight.toInt
  val gridCtx = gridCanvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  var cellWidth = canvas.width/board.m
  var cellHeight = canvas.height/board.n

  def init() = {
    cellWidth = canvas.width/board.m
    cellHeight= canvas.height/board.n

    gridCtx.fillStyle = Color.White.toString()
    gridCtx.clearRect(0,0,canvas.width,canvas.height)

    gridCtx.strokeStyle = Color.Black.toString()
    gridCtx.lineWidth = 1
    for (startX <- 0 to canvas.width by cellWidth) {
      gridCtx.beginPath()
      gridCtx.moveTo(startX,0)
      gridCtx.lineTo(startX,canvas.height)
      gridCtx.closePath()
      gridCtx.stroke()
    }
    for (startY <- 0 to canvas.height by cellHeight) {
      gridCtx.beginPath()
      gridCtx.moveTo(0,startY)
      gridCtx.lineTo(canvas.width,startY)
      gridCtx.closePath()
      gridCtx.stroke()
    }

    ctx.fillStyle = Color.White.toString()
    ctx.clearRect(0,0,canvas.width,canvas.height)
    ctx.fillRect(0,0,canvas.width,canvas.height)
  }


  var stats = js.Dynamic.newInstance(js.Dynamic.global.Stats)()
  stats.showPanel( 1 ); // 0: fps, 1: ms, 2: mb, 3+: custom
  dom.document.body.appendChild( stats.dom.asInstanceOf[org.scalajs.dom.raw.Node] )

  var lastCall = 0d
  val fpsDisplay = dom.document.getElementById("fps")

  var animFrameHandle = 0

  def draw(timestamp:Double): Unit = {
    println("draw loop running")
    if (lastCall == 0) {
      lastCall = timestamp
    } else {
      val duration = timestamp - lastCall
      //println(f"setting duration to ${timestamp - lastCall}")
      fpsDisplay.innerHTML = duration.toString + "ms/frame"
      lastCall = timestamp
    }
    stats.begin()
    val (boardArr, boardArr2) = board.currentAndPrev()
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
          ctx.fillRect(startX, startY, width, height)
        }
      }
    }

    board.step()
    stats.end()

    if (run) animFrameHandle = dom.window.requestAnimationFrame(draw _)
  }

  def main(): Unit = {
    println("hello from scalajs")

    //var drawerHandle = 0

    //TODO: is setInterval faster than reqAnimFrame? (shouldnt be)

    //TODO: grid layout bug

    //TODO: Adding this reduces perf dramatically and screws up the fps monitor...??idonteven
//    dom.document.getElementById("life").asInstanceOf[HTMLDivElement].onkeypress = (event:KeyboardEvent) => {
//      if (event.keyCode == KeyCode.Right) draw(dom.window.performance.now())
//      if (event.keyCode == KeyCode.Space) toggleRun()
//      if (event.keyCode == KeyCode.N) board = Board.randomBoard(board.n,board.m,0.1); startRun()
//    }


    def toggleRun() = { if (run) stopRun() else animFrameHandle = startRun() }

    def startRun() = {
      run = true
      init()
      //drawerHandle = dom.window.setInterval(draw _, 0)
      dom.window.requestAnimationFrame(draw _)
    }

    def stopRun() = {
      //dom.window.clearInterval(drawerHandle)
      run = false
      dom.window.cancelAnimationFrame(animFrameHandle)
    }

    dom.document.getElementById("size").asInstanceOf[HTMLInputElement].onfocus = {(_:FocusEvent) =>
      dom.document.getElementById("size").asInstanceOf[HTMLInputElement].value =  ""
    }
    dom.document.getElementById("size").asInstanceOf[HTMLInputElement].onkeypress = {(evt:KeyboardEvent) =>
      evt.keyCode match {
        case KeyCode.Enter =>
          stopRun()
          try {
            val newSize = dom.document.getElementById("size").asInstanceOf[HTMLInputElement].value.toInt
            dom.document.getElementById("sizeShow").innerHTML = newSize.toString
            board = Board.randomBoard(newSize, newSize)
            startRun()
          } catch {
            case _ => ()
          }
        case _ => ()
      }

    }

    //dom.window.setInterval(draw _,1000/60)
    startRun()
  }
}
