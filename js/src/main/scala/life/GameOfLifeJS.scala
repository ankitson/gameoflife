package life

import life.GameOfLifeJS.{ConfigOption, HighlightVisited}
import life.core.{Board, Cell, Dead, Live}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.ext.Color
import org.scalajs.dom.html.Div

import scala.collection._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.scalajs.js
import scala.scalajs.js.Any._
import scala.scalajs.js.annotation.JSExport

class GameOfLifeCanvas(gridCanvas: dom.html.Canvas, cellCanvas: dom.html.Canvas,board:Board,
                       options: mutable.Map[ConfigOption,Boolean] = mutable.Map(HighlightVisited -> false)
                      ) {
  var run = true
  var cellWidth: Double = _
  var cellHeight: Double = _
  val drawStats = js.Dynamic.newInstance(js.Dynamic.global.Stats)()
  val computeStats = js.Dynamic.newInstance(js.Dynamic.global.Stats)()
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
    initGrid(gridCanvas,board.numCols(),board.numRows())
    boardCtx = initBoard(boardCanvas)
    cellWidth = boardCtx.canvas.width.toDouble / board.numRows()
    cellHeight = boardCtx.canvas.height.toDouble / board.numCols()
    ()
  }

  def init(): Unit = {
    drawStats.showPanel(0); // 0: fps, 1: ms, 2: mb, 3+: custom
    computeStats.showPanel(1)

    dom.document.getElementById("life-ui").removeChild(dom.document.getElementById("stats"))
    val statsDiv = dom.document.createElement("div")
    statsDiv.id = "stats"
    dom.document.getElementById("life-ui").appendChild(statsDiv)

    val node1 = drawStats.dom.asInstanceOf[org.scalajs.dom.Element]
    node1.setAttribute("class","stats-monitor")
    node1.removeAttribute("style")
    dom.document.getElementById("stats").appendChild(node1)

    val node2 = computeStats.dom.asInstanceOf[org.scalajs.dom.Element]
    node2.setAttribute("class","stats-monitor-2")
    node2.removeAttribute("style")
    dom.document.getElementById("stats").appendChild(node2)

    visited = collection.mutable.Set[(Int,Int)]()
    initAll(cellCanvas,gridCanvas,board)
  }

  def drawCell(cell: Cell,row:Int,col:Int,cellWidth:Double,cellHeight:Double): Unit = {
    val LIVE_FILL = "rgba(255,0,0,1)"
    val VISITED_FILL = "rgba(0,200,50,0.3)"
    val DEAD_FILL = "rgba(255,255,255,1)"
    boardCtx.fillStyle = cell match {
      case Live => visited.add((row,col)); LIVE_FILL
      case Dead =>
        if (options(HighlightVisited) && visited.contains((row, col))) {
          VISITED_FILL
        }
        else
          DEAD_FILL
    }
    val startX = cellWidth * col
    val startY = cellHeight * row
    boardCtx.clearRect(startX,startY,cellWidth,cellHeight)
    boardCtx.fillRect(startX, startY, cellWidth, cellHeight)
  }

  def draw(timestamp:Double): Unit = {
    for (row <- 0 until board.numRows(); col <- 0 until board.numCols()) {
      if (board.cellChanged(row,col)) {
        drawCell(board.rows()(row)(col),row,col,cellWidth,cellHeight)
      }
    }
  }

  def step(time:Double): Unit = {
    if (run) {
      drawStats.begin()
      computeStats.begin()
      board.step()
      computeStats.end()
      draw(time)
      drawStats.end()
      ()
    }
  }

  def update(key: Event): Unit = key match {
    case kbd: KeyboardEvent => kbd.charCode match {
      case 'v' | 'V' => options(HighlightVisited) = !options(HighlightVisited)
      case 'p' | 'P' => run = !run
      case _ => ()
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
        handle = (dom.window.setInterval(() => fn(System.currentTimeMillis()),every.toMillis),true)
        cancel(oldHandle)
      } else {
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



  def main(): Unit = {
    val boardCanvas = dom.document.getElementById("life-board").asInstanceOf[dom.html.Canvas]
    val gridCanvas = dom.document.getElementById("life-grid").asInstanceOf[dom.html.Canvas]
    val fpsLimit = dom.document.getElementById("fpsLimit").asInstanceOf[dom.html.Input]
    val sizeSlider = dom.document.getElementById("size").asInstanceOf[dom.html.Input]
    var highlightVisited = true
    var board: Board = Board.randomRectBoard(sizeSlider.value.toInt,sizeSlider.value.toInt)
    var life: GameOfLifeCanvas = new GameOfLifeCanvas(gridCanvas,boardCanvas,board,mutable.Map(HighlightVisited -> highlightVisited))

    def stepTime(): Duration = {
      if (fpsLimit.value.toInt == 61) Duration.Inf else 1000/fpsLimit.value.toDouble millis
    }

    def renderBoard(size:Int) = {
      Scheduler.cancel()
      board = Board.randomRectBoard(size,size)
      life = new GameOfLifeCanvas(gridCanvas,boardCanvas,board,mutable.Map(HighlightVisited -> highlightVisited))
      Scheduler.schedule(life.step,stepTime())
    }

    def dispatchChangeEvent(elem:Element) = {
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
      case 'v' | 'V' =>
        highlightVisited = !highlightVisited; life.update(event)
      case 'z' | 'Z' =>
        event.preventDefault()
        fpsLimit.value = Math.max(fpsLimit.value.toInt - 1, 0).toString
        dispatchChangeEvent(fpsLimit)
      case 'x' | 'X' =>
        event.preventDefault()
        fpsLimit.value = Math.min(fpsLimit.value.toInt + 1, 61).toString
        dispatchChangeEvent(fpsLimit)
      case _ => life.update(event)
    }

    fpsLimit.onchange = (_:Event) => Scheduler.schedule(life.step,stepTime())

    sizeSlider.onchange = (_:Event) => renderBoard(sizeSlider.value.toInt)

    dom.document.getElementById("life-ui").asInstanceOf[Div].onmouseenter = { (_:MouseEvent) =>
      dom.document.getElementById("life-ui").asInstanceOf[Div].setAttribute("class","ui-show-translucent")
    }
    dom.document.getElementById("life-ui").asInstanceOf[Div].onmouseleave = { (_:MouseEvent) =>
      dom.document.getElementById("life-ui").asInstanceOf[Div].setAttribute("class","ui-hide")
    }

    renderBoard(sizeSlider.value.toInt)
  }

}

