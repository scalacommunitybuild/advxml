# Attributes

### Key
Key is a case class that wrap a String instance and is used to describe the key concept. 
It exists to avoid unnamed string.

We can create it directly or using StringContext syntax ops.
```scala
import advxml.core.data.Key
import advxml.syntax._

val key : Key = Key("key")
val keySyntax : Key = k"key"
```

### AttributeData
`AttributeData` is a case class to bind a `Key` and a `String` instance. It's used in advxml to contains write information
for attributes.
Advxml provides a fluent syntax to define an `AttributeData` instance.

```scala
import advxml.core.data.{Key, AttributeData}
import advxml.syntax._

val data : AttributeData = AttributeData(Key("key"), "value")
val dataWithSyntax : AttributeData = k"key" := "value"
```

### Key-Value predicate
`KeyValuePredicate` is a case class to bind a `Key` and a predicate function `T => Boolean` related to value.
We can create it directly or using fluent syntax.

```scala
import advxml.core.data._
import advxml.syntax._

val p : KeyValuePredicate[String] = KeyValuePredicate(Key("key"), _ == "value")
val pWithSyntax : KeyValuePredicate[String] = k"key" -> (_ == "value")
```

Moreover, advxml provides multiple syntax method to easily create a `KeyValuePredicate` for the most common predicates.
Each of these methods use an implicit `StringTo` converter of `Id` to convert `String` into other compared instance type
and `PartialOrder` from cats for comparisons (except for `===` and `=!=` that use `Eq` from cats).
Please, keep in mind that these conversions *ARE NOT SAFE* and this code will throw an exception at runtime if conversion fail.

```scala
import advxml.core.data._
import advxml.syntax._

val eq : KeyValuePredicate[String] = k"key" === 1
val neq : KeyValuePredicate[String] = k"key" =!= 1

val le : KeyValuePredicate[String] = k"key" < 1
val leEq : KeyValuePredicate[String] = k"key" <= 1
val gt : KeyValuePredicate[String] = k"key" > 1
val gtEq : KeyValuePredicate[String] = k"key" >= 1
```
