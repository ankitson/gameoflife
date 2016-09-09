package life.core

sealed trait Cell {
  override def toString: String = this match {
    case Live => "*"
    case Dead => "-"
  }
}
case object Live extends Cell
case object Dead extends Cell
