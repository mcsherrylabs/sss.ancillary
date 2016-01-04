# sss.ancillary
Orthogonal to and needed by many projects

[![Build Status](https://travis-ci.org/mcsherrylabs/sss.ancillary.svg?branch=master)](https://travis-ci.org/mcsherrylabs/sss.ancillary)

Contains a few useful traits including Logging and typesafe config helpers as well as a few Reflection utilities. 

For example given the name of a Typesafe config it can map the values in that config to a pure trait.

```
  trait MimicInterface {
    val name: String
    val ingredients: java.lang.Iterable[String]
  }

  dish {
  	name = "SomeCompany"
  	ingredients = ["potato", "bacon", "onion", "salt", "pepper"]
  }

  val sut = DynConfig[MimicInterface]("dish")
  assert(sut.name == "SomeCompany")
  assert(sut.ingredients.toSet == Set("potato", "bacon", "onion", "salt", "pepper"))
```  
