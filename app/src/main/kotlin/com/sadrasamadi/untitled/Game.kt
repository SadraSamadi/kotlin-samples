package com.sadrasamadi.untitled

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants
import kotlin.math.*
import kotlin.random.Random

fun runGame() = runBlocking {
  play()
}

suspend fun play() = coroutineScope {
  operator fun Int.not() = this.toFloat()
  val dimension = Dimension(800, 600)
  val game = Game(!dimension.width, !dimension.height)
  val frame = JFrame()
  frame.apply {
    title = "Untitled"
    defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    contentPane = object : JPanel() {
      override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        game.render(g as Graphics2D)
      }
    }
    contentPane.apply {
      preferredSize = dimension
      pack()
      addComponentListener(object : ComponentAdapter() {
        override fun componentResized(e: ComponentEvent) {
          game.resize(!e.component.width, !e.component.height)
        }
      })
    }
    addKeyListener(object : KeyAdapter() {
      override fun keyPressed(e: KeyEvent) {
        game.handle(e)
      }
    })
    setLocationRelativeTo(null)
  }
  game.setup()
  frame.isVisible = true
  launch {
    var time = System.currentTimeMillis()
    while (frame.isVisible) {
      delay(1000L / 60L)
      val curr = System.currentTimeMillis()
      val elapsed = curr - time
      time = curr
      frame.contentPane.repaint()
      game.update(elapsed / 1000f)
    }
  }
}

data class Vector(val x: Float, val y: Float) {

  operator fun times(f: Float) = Vector(f * x, f * y)

  operator fun plus(v: Vector) = Vector(x + v.x, y + v.y)

  infix fun distance(v: Vector) = sqrt((x - v.x).pow(2) + (y - v.y).pow(2))

  infix fun angle(v: Vector) = atan2(x - v.x, y - v.y)

}

data class Circle(var position: Vector, var velocity: Vector, var radius: Float, var color: Color)

class Game(private var width: Float, private var height: Float) {

  private var stopped = false

  private lateinit var circles: MutableList<Circle>

  fun setup() {
    val seed = System.currentTimeMillis()
    val random = Random(seed)
    circles = mutableListOf()
    for (i in 0 until 1000) {
      val r = (i / 100 + 1) * 5f
      val a = (i % 100) * 3.6f
      val circle = Circle(
        Vector(width / 2, height / 2),
        Vector(r * sin(a), r * cos(a)),
        3f - i / 500f,
        Color(random.nextInt())
      )
      circles.add(circle)
    }
  }

  fun resize(width: Float, height: Float) {
    this.width = width
    this.height = height
    setup()
  }

  fun handle(event: KeyEvent) {
    when (event.keyCode) {
      KeyEvent.VK_SPACE -> stopped = !stopped
      KeyEvent.VK_UP -> circles.forEach { it.velocity *= 1.1f }
      KeyEvent.VK_DOWN -> circles.forEach { it.velocity *= 0.9f }
      KeyEvent.VK_R -> setup()
    }
  }

  fun render(g2d: Graphics2D) {
    operator fun Float.not() = this.roundToInt()
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2d.color = Color.BLACK
    g2d.fillRect(0, 0, !width, !height)
    for (circle in circles) {
      g2d.color = circle.color
      g2d.fillOval(
        !(circle.position.x - circle.radius),
        !(circle.position.y - circle.radius),
        !(2 * circle.radius),
        !(2 * circle.radius)
      )
    }
  }

  fun update(delta: Float) {
    if (stopped)
      return
    for (circle in circles) {
      val center = Vector(width / 2, height / 2)
      val next = circle.velocity * delta + circle.position
      val dist = next distance center
      val angle = next angle center
      if (abs(dist * sin(angle)) >= center.x - circle.radius)
        circle.velocity = circle.velocity.copy(x = -1 * circle.velocity.x)
      if (abs(dist * cos(angle)) >= center.y - circle.radius)
        circle.velocity = circle.velocity.copy(y = -1 * circle.velocity.y)
      circle.position += circle.velocity * delta
    }
  }

}
