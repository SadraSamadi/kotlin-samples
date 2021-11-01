package com.sadrasamadi.untitled

import kotlin.reflect.full.createInstance

fun runDocument() {
  val doc = html {
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
  println(doc)
}

interface Element {

  fun render(builder: StringBuilder, indent: String)

}

typealias Initializer<T> = T.() -> Unit

class Text(val content: String) : Element {

  override fun toString() = content

  override fun render(builder: StringBuilder, indent: String) {
    builder.append("$indent$this\n")
  }

}

@DslMarker
annotation class TagMarker

@TagMarker
sealed class Tag(val name: String) : Element {

  val children = mutableListOf<Element>()

  val attributes = mutableMapOf<String, String>()

  inline fun <T : Element> append(t: T, init: Initializer<T>) {
    t.init()
    children.add(t)
  }

  inline fun <reified T : Element> append(crossinline init: Initializer<T>) {
    val t = T::class.createInstance()
    append(t, init)
  }

  operator fun String.unaryPlus() {
    val text = Text(this)
    children.add(text)
  }

  override fun toString(): String {
    val builder = StringBuilder()
    render(builder, "")
    return builder.toString()
  }

  override fun render(builder: StringBuilder, indent: String) {
    builder.append("$indent<$name")
    for ((key, value) in attributes)
      builder.append(" $key=\"$value\"")
    builder.append(">\n")
    for (child in children)
      child.render(builder, "$indent  ")
    builder.append("$indent</$name>\n")
  }

}

fun html(init: Initializer<HTML>): HTML {
  val html = HTML()
  html.init()
  return html
}

class HTML : Tag("html") {

  var lang: String by attributes

  fun head(init: Initializer<Head>) = append(init)

  fun body(init: Initializer<Body>) = append(init)

}

class Head : Tag("head") {

  fun title(init: Initializer<Title>) = append(init)

}

class Title : Tag("title")

class Body : Tag("body") {

  fun p(init: Initializer<P>) = append(init)

  fun a(init: Initializer<A>) = append(init)

}

class P : Tag("p")

class A : Tag("a") {
  var href: String by attributes
}
