# sss.ancillary
Orthogonal to and depended on by many projects

[![Build Status](https://travis-ci.org/mcsherrylabs/sss.ancillary.svg?branch=master)](https://travis-ci.org/mcsherrylabs/sss.ancillary)
[![Coverage Status](https://coveralls.io/repos/mcsherrylabs/sss.ancillary/badge.svg?branch=master&service=github)](https://coveralls.io/github/mcsherrylabs/sss.ancillary?branch=master)

```
resolvers += "stepsoft" at "http://nexus.mcsherrylabs.com/nexus/content/groups/public"

libraryDependencies += "mcsherrylabs.com" %% "sss-ancillary" % "0.9.8"
```

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

Now contains a ServerLauncher - useful for starting an embedded Jetty server.
