package life

import life.Board.Live
import life.GameOfLifeJS.{ConfigOption, HighlightVisited}
import org.scalajs.dom
import org.scalajs.dom.ext.Color
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.{Event, KeyboardEvent}

import scala.collection._
import scala.concurrent.duration._
import scala.scalajs.js
import scala.scalajs.js.Any._
import scala.scalajs.js.annotation.JSExport

class GameOfLifeCanvas(gridCanvas: dom.html.Canvas, cellCanvas: dom.html.Canvas,board:Board,
                       options: mutable.Map[ConfigOption,Boolean] = mutable.Map(HighlightVisited -> false)
                      ) {

  var cellWidth: Double = _
  var cellHeight: Double = _
  val stats = js.Dynamic.newInstance(js.Dynamic.global.Stats)()
  var lastCall = 0d
  val fpsDisplay = dom.document.getElementById("fps")
  var boardCtx: dom.CanvasRenderingContext2D = _
  var visited: collection.mutable.Set[(Int,Int)] = _

  private def initGrid(canvas: dom.html.Canvas, rows: Int, cols: Int) = {
    canvas.width = canvas.offsetWidth.toInt
    canvas.height = canvas.offsetHeight.toInt
    val renderContext = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    renderContext.fillStyle = Color.White.toString()
    renderContext.strokeStyle = "rgba(0,0,0,1)"
    renderContext.lineWidth = 1

    renderContext.clearRect(0, 0, canvas.width, canvas.height)
    renderContext.beginPath()
    for (start <- 0.0 to canvas.height by canvas.height.toDouble/rows) {
      renderContext.moveTo(0, start)
      renderContext.lineTo(canvas.width, start)
    }
    renderContext.moveTo(0,canvas.height); renderContext.lineTo(canvas.width,canvas.height)
    for (start <- 0.0 to canvas.width by canvas.width.toDouble/cols) {
      renderContext.moveTo(start, 0)
      renderContext.lineTo(start, canvas.height)
    }
    renderContext.moveTo(canvas.width,0); renderContext.lineTo(canvas.width,canvas.height)
    renderContext.closePath()
    renderContext.stroke()

    renderContext
  }

  private def initBoard(canvas:dom.html.Canvas) = {
    canvas.width = canvas.offsetWidth.toInt
    canvas.height = canvas.offsetHeight.toInt
    val renderContext = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    renderContext.fillStyle = Color.White.toString()
    renderContext.clearRect(0, 0, canvas.width, canvas.height)
    renderContext.fillRect(0, 0, canvas.width, canvas.height)

    renderContext

  }

  private def initAll(boardCanvas:dom.html.Canvas,gridCanvas:dom.html.Canvas,board:Board) = {
    initGrid(gridCanvas,board.m,board.n)
    boardCtx = initBoard(boardCanvas)
    cellWidth = boardCtx.canvas.width.toDouble / board.n
    cellHeight = boardCtx.canvas.height.toDouble / board.m
    stats.showPanel(1); // 0: fps, 1: ms, 2: mb, 3+: custom
    dom.document.body.appendChild(stats.dom.asInstanceOf[org.scalajs.dom.raw.Node])
  }

  def init(): Unit = {
    visited = collection.mutable.Set[(Int,Int)]()
    initAll(cellCanvas,gridCanvas,board)
  }


  def draw(timestamp: Double): Unit = {
    stats.begin()
    val duration = timestamp - lastCall
    val fps = 1000 / duration
    fpsDisplay.innerHTML = duration.toString + "ms/frame" + "<br>" + f"${fps}%1.2f frames/sec"
    lastCall = timestamp

    val (boardArr, boardArr2) = board.currentAndPrev()
    for (row <- 0 until board.n; col <- 0 until board.m) {
      val cellChanged = boardArr(row)(col) != boardArr2(row)(col)
      if (cellChanged) {
        if (boardArr(row)(col) == Live) {
          visited.add((row, col))
          boardCtx.fillStyle = "rgba(255,0,0,1)"
        } else if (options(HighlightVisited) && visited.contains((row, col))) {
          boardCtx.fillStyle = "rgba(0,200,50,0.3)"
        } else {
          boardCtx.fillStyle = "rgba(255,255,255,1)"
        }

        val startX = cellWidth * col
        val startY = cellHeight * row

        boardCtx.clearRect(startX, startY, cellWidth, cellHeight)
        boardCtx.fillRect(startX, startY, cellWidth, cellHeight)
      }
    }
    stats.end()
  }

  def step(time:Double): Unit = {
    draw(time)
    board.step()
  }

  def update(key: Event): Unit = key match {
    case kbd: KeyboardEvent => kbd.charCode match {
      case 'v' | 'V' => options(HighlightVisited) = !options(HighlightVisited)
    }
  }

  init()
}

@JSExport
object GameOfLifeJS extends js.JSApp {

  sealed trait ConfigOption
  case object HighlightVisited extends ConfigOption

  object Scheduler {
    var handle = (0,true)
    def schedule[A](fn: Double=>A, every: Duration): Int = {
      val oldHandle = handle
      if (every.isFinite()) {
        println("scheduling setInterval")
        handle = (dom.window.setInterval(() => fn(System.currentTimeMillis()),every.toMillis),true)
        cancel(oldHandle)
      } else {
        println("scheduling requestAnimFram")
        handle = (dom.window.requestAnimationFrame{(ts:Double) =>
          def x(t:Double): Unit = {fn(t); handle = (dom.window.requestAnimationFrame(x _),false)}
          x(ts)
        },false)
        cancel(oldHandle)
      }
      handle._1
    }
    def cancel(handle:(Int,Boolean)=handle) = {
      if (handle._2) { dom.window.clearInterval(handle._1)}
      else { dom.window.cancelAnimationFrame(handle._1)}
    }
  }

  var board: Board = _

  def main(): Unit = {
    val boardCanvas = dom.document.getElementById("life-board").asInstanceOf[dom.html.Canvas]
    val gridCanvas = dom.document.getElementById("life-grid").asInstanceOf[dom.html.Canvas]
    val fpsLimit = dom.document.getElementById("fpsLimit").asInstanceOf[dom.html.Input]
    val sizeSlider = dom.document.getElementById("size").asInstanceOf[dom.html.Input]

    board = Board.randomBoard(sizeSlider.value.toInt,sizeSlider.value.toInt)
    var life = new GameOfLifeCanvas(gridCanvas,boardCanvas,board,mutable.Map(HighlightVisited -> true))

    def stepTime(): Duration = {
      if (fpsLimit.value.toInt == 61) Duration.Inf else 1000/fpsLimit.value.toDouble millis
    }

    Scheduler.schedule(life.step,stepTime())

    def renderBoard(size:Int) = {
      Scheduler.cancel()
      board = Board.randomBoard(size,size)
      life = new GameOfLifeCanvas(gridCanvas,boardCanvas,board,mutable.Map(HighlightVisited -> true))
      Scheduler.schedule(life.step,stepTime())
    }

    def dispatchChangeEvent(elem:HTMLElement) = {
      val evt = js.Dynamic.newInstance(js.Dynamic.global.Event)("change").asInstanceOf[Event]
      elem.dispatchEvent(evt)
    }

    //keyCode doesnt work with both uppercase and lowercase on chrome
    dom.window.onkeypress = (event:KeyboardEvent) => event.charCode match {
      case 'n' | 'N' => event.preventDefault(); renderBoard(sizeSlider.value.toInt)
      case '>' | '.' =>
        event.preventDefault();
        sizeSlider.value = Math.min(sizeSlider.value.toInt + 1,sizeSlider.max.toInt).toString
        dispatchChangeEvent(sizeSlider)
      case '<' | ',' =>
        event.preventDefault()
        sizeSlider.value = Math.max(sizeSlider.value.toInt - 1,sizeSlider.min.toInt).toString
        dispatchChangeEvent(sizeSlider)
      case 'f' | 'F' =>
        life.step(System.currentTimeMillis())
      case _ => life.update(event)

    }

    fpsLimit.onchange = (_:Event) => Scheduler.schedule(life.step,stepTime())

    sizeSlider.onchange = (_:Event) => renderBoard(sizeSlider.value.toInt)

  }

}
