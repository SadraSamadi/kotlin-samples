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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
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

data class Vector(var x: Float, var y: Float)

data class Circle(val position: Vector, val velocity: Vector, var radius: Float, var color: Color)

class Game(private var width: Float, private var height: Float) {

  private val circles = mutableListOf<Circle>()

  fun setup() {
    val time = System.currentTimeMillis()
    val random = Random(time)
    for (i in 0 until 3600) {
      val r = (i / 360) * 5f + 5f
      val a = (i % 360) * 1f
      val circle = Circle(
        Vector(width / 2, height / 2),
        Vector(r * sin(a), r * cos(a)),
        random.nextDouble(1.0, 2.0).toFloat(),
        Color(random.nextInt())
      )
      circles.add(circle)
    }
  }

  fun resize(width: Float, height: Float) {
    this.width = width
    this.height = height
    reset()
  }

  fun handle(event: KeyEvent) {
    when (event.keyCode) {
      KeyEvent.VK_SPACE -> circles.removeFirstOrNull()
      KeyEvent.VK_BACK_SPACE -> circles.removeLastOrNull()
      KeyEvent.VK_ENTER -> reset()
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
    for (circle in circles) {
      if (abs(circle.position.x + delta * circle.velocity.x - width / 2) > width / 2 - circle.radius)
        circle.velocity.x *= -1
      if (abs(circle.position.y + delta * circle.velocity.y - height / 2) > height / 2 - circle.radius)
        circle.velocity.y *= -1
      circle.position.x += delta * circle.velocity.x
      circle.position.y += delta * circle.velocity.y
    }
  }

  private fun reset() {
    circles.clear()
    setup()
  }

}
