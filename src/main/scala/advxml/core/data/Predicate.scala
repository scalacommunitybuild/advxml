package advxml.core.data

import cats.⊤

object Predicate {

  /** Always true predicate.
    */
  lazy val alwaysTrue: ⊤ => Boolean = _ => true

  /** Always false predicate.
    */
  lazy val alwaysFalse: ⊤ => Boolean = _ => false

  /** Combine two predicates(`T => Boolean`) with `And` operator.
    *
    * @param p1 First predicate
    * @param p2 Second predicate
    * @tparam T Subject type of predicates
    * @return Result of combination of `p1` and `p2` using `And` operator.
    */
  def and[T](p1: T => Boolean, p2: T => Boolean): T => Boolean = t => p1(t) && p2(t)

  /** Combine two predicates(`T => Boolean`) with `Or` operator.
    *
    * @param p1 First predicate
    * @param p2 Second predicate
    * @tparam T Subject type of predicates
    * @return Result of combination of `p1` and `p2` using `Or` operator.
    */
  def or[T](p1: T => Boolean, p2: T => Boolean): T => Boolean = t => p1(t) || p2(t)
}

object XmlPredicate {
  def apply(f: XmlPredicate): XmlPredicate = f
}
