package com.sadrasamadi.untitled

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants
import kotlin.math.abs
import kotlin.random.Random

fun runGame() = runBlocking {
  launch {
    play()
  }
}

suspend fun play() = coroutineScope {
  val game = Game()
  val frame = JFrame()
  frame.apply {
    title = "Untitled"
    defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    contentPane = object : JPanel() {
      override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        game.render(g as Graphics2D)
      }
    }
    contentPane.preferredSize = Dimension(Game.WIDTH.toInt(), Game.HEIGHT.toInt())
    isResizable = false
    addKeyListener(object : KeyAdapter() {
      override fun keyPressed(e: KeyEvent?) {
        game.handle(e ?: return)
      }
    })
    pack()
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

data class Circle(val position: Vector, val speed: Vector, var radius: Float, var color: Color)

class Game {

  companion object {
    const val WIDTH = 800.0f
    const val HEIGHT = 600.0f
  }

  private val circles = mutableListOf<Circle>()

  fun setup() {
    val time = System.currentTimeMillis()
    val random = Random(time)
    for (i in 0..999) {
      val circle = Circle(
        Vector(
          random.nextDouble(0.25 * WIDTH, 0.75 * WIDTH).toFloat(),
          random.nextDouble(0.25 * HEIGHT, 0.75 * HEIGHT).toFloat()
        ),
        Vector(
          random.nextDouble(-64.0, 64.0).toFloat(),
          random.nextDouble(-64.0, 64.0).toFloat()
        ),
        random.nextDouble(1.0, 2.0).toFloat(),
        Color(random.nextInt())
      )
      circles.add(circle)
    }
  }

  fun handle(event: KeyEvent) {
    when (event.keyCode) {
      KeyEvent.VK_SPACE -> circles.removeFirstOrNull()
      KeyEvent.VK_BACK_SPACE -> circles.removeLastOrNull()
    }
  }

  fun render(g2d: Graphics2D) {
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2d.color = Color.BLACK
    g2d.fillRect(0, 0, WIDTH.toInt(), HEIGHT.toInt())
    for (circle in circles) {
      g2d.color = circle.color
      g2d.fillOval(
        (circle.position.x - circle.radius).toInt(),
        (circle.position.y - circle.radius).toInt(),
        (2 * circle.radius).toInt(),
        (2 * circle.radius).toInt()
      )
    }
  }

  fun update(delta: Float) {
    for (circle in circles) {
      if (abs(circle.position.x + delta * circle.speed.x - WIDTH / 2) > WIDTH / 2 - circle.radius)
        circle.speed.x *= -1
      if (abs(circle.position.y + delta * circle.speed.y - HEIGHT / 2) > HEIGHT / 2 - circle.radius)
        circle.speed.y *= -1
      circle.position.x += delta * circle.speed.x
      circle.position.y += delta * circle.speed.y
    }
  }

}
