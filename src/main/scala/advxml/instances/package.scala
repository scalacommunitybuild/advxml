package advxml

/*
 * In order to keep project clean keep in mind the following rules:
 * - Each object represent a feature
 * - Each feature must provide a trait containing all feature instances named `[feature_name]Instances`
 * - For each object must be exist a package with the same name under `advxml`
 */
/** This object is the entry point to access to all features instances provided by Advxml.
  *
  * You can import all instances using:
  * {{{
  *   import advxml.instances._
  * }}}
  *
  * Otherwise you can import only a specific part of instances using:
  * {{{
  *   //import advxml.implicits.[feature_name]._
  *   //example
  *   import advxml.instances.transform._
  * }}}
  */
package object instances extends AllCommonInstances {

  // format: off
  object all        extends AllInstances
  //******************** FEATURES ********************
  object transform  extends AllTransforInstances {
    object predicates extends XmlPredicateInstances
    object modifiers  extends XmlModifierInstances
  }
  object convert    extends ConverterInstances
  object validated  extends ValidatedInstances
  // format: on
}
