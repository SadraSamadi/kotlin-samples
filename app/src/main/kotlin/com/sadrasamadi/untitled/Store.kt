package com.sadrasamadi.untitled

fun runStore() {
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
}

operator fun String.times(n: Int) = repeat(n)

fun <T> logger(name: String = "default"): Middleware<T> {
  return { store, action, next ->
    println("-" * 64)
    println("logger     : $name")
    println("action     : ${action.type}")
    println("payload    : ${action.payload}")
    println("prev state : ${store.state}")
    next()
    println("next state : ${store.state}")
    println("-" * 64)
  }
}

data class Action(val type: String, val payload: Any?)

typealias Reducer<T> = (state: T, action: Action) -> T

typealias Middleware<T> = (store: Store<T>, action: Action, next: () -> Unit) -> Unit

typealias Subscriber = () -> Unit

typealias Destroyer = () -> Unit

class Store<T>(initial: T, private val reducer: Reducer<T>) {

  var state = initial
    private set

  private val middlewares = mutableListOf<Middleware<T>>()

  private val subscribers = mutableListOf<Subscriber>()

  fun apply(middleware: Middleware<T>): Destroyer {
    middlewares.add(middleware)
    return { middlewares.remove(middleware) }
  }

  fun subscribe(subscriber: Subscriber): Destroyer {
    subscribers.add(subscriber)
    return { subscribers.remove(subscriber) }
  }

  fun dispatch(action: Action) {
    val queue = middlewares.toMutableList()
    dispatch(action, queue)
    for (subscriber in subscribers)
      subscriber()
  }

  private fun dispatch(action: Action, queue: MutableList<Middleware<T>>) {
    val middleware = queue.removeFirstOrNull()
    if (middleware == null)
      state = reducer(state, action)
    else
      middleware(this, action) { dispatch(action, queue) }
  }

}
