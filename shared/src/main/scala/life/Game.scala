package life

import org.scalajs._
import org.scalajs.dom.{Event, KeyboardEvent}

abstract class Game(canvases: List[dom.html.Canvas]) {

  def step(time:Double): Unit

  def update(event: Event): Unit

}

