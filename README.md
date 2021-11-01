# Kotlin Samples

Kotlin sample codes.

#### HTML Builder

```kotlin
html {
  lang = "en"
  head {
    title { +"Kotlin HTML" }
  }
  body {
    p {
      +"This is the first line of paragraph."
      +"This is the second line of paragraph."
    }
    a {
      href = "https://kotlinlang.org/"
      +"Link"
    }
  }
}
```

#### State Management

```kotlin
val store = Store(0) { state, action ->
  when (action.type) {
    "increment" -> state + action.payload as Int
    "decrement" -> state + action.payload as Int
    else -> state
  }
}
store.apply(logger())
store.subscribe { println(store.state) }
store.dispatch(Action("increment", 1))
store.dispatch(Action("increment", 2))
store.dispatch(Action("increment", 3))
store.dispatch(Action("decrement", 4))
```

#### 2D Graphic

![Circles](circles.png)
