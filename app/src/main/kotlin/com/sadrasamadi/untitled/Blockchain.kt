package com.sadrasamadi.untitled

import com.google.gson.GsonBuilder
import org.apache.commons.codec.binary.Hex
import java.security.*
import javax.crypto.Cipher

class Blockchain {

  private val chain = mutableListOf<Block>()

  private val mempool = mutableListOf<Transaction>()

  fun createTransaction(sender: PublicKey?, receiver: PublicKey, amount: Double) = Transaction(
    sender = if (sender == null) "" else Hex.encodeHexString(sender.encoded),
    receiver = Hex.encodeHexString(receiver.encoded),
    amount = amount,
    timestamp = System.currentTimeMillis(),
    signature = ""
  )

  fun addTransaction(key: PrivateKey, transaction: Transaction): Transaction {
    val sign = signTransaction(key, transaction)
    val hex = Hex.encodeHexString(sign)
    val temp = transaction.copy(signature = hex)
    mempool.add(temp)
    return temp
  }

  fun signTransaction(key: PrivateKey, transaction: Transaction): ByteArray {
    val hash = hashTransaction(transaction)
    val rsa = Cipher.getInstance("RSA")
    rsa.init(Cipher.ENCRYPT_MODE, key)
    return rsa.doFinal(hash)
  }

  fun hashTransaction(transaction: Transaction): ByteArray {
    val map = mutableMapOf<String, Any?>()
    map["sender"] = transaction.sender
    map["receiver"] = transaction.receiver
    map["amount"] = transaction.amount
    map["timestamp"] = transaction.timestamp
    return hash(map)
  }

  fun createBlock(receiver: PublicKey, amount: Double): Block {
    val last = chain.lastOrNull()
    val transactions = mempool.toMutableList()
    val transaction = createTransaction(null, receiver, amount)
    transactions.add(transaction)
    return Block(
      previous = last?.hash ?: "",
      transactions = transactions,
      timestamp = System.currentTimeMillis(),
      nonce = if (last == null) 0 else last.nonce + 1,
      hash = ""
    )
  }

  fun mineBlock(block: Block): Block {
    var nonce = block.nonce
    while (true) {
      val hash = hashBlock(block, nonce)
      val pow = hash.startsWith("0000")
      if (pow)
        return block.copy(nonce = nonce, hash = hash)
      nonce++
    }
  }

  fun hashBlock(block: Block, nonce: Long? = null): String {
    val map = mutableMapOf<String, Any?>()
    map["previous"] = block.previous
    map["transactions"] = block.transactions
    map["timestamp"] = block.timestamp
    map["nonce"] = nonce ?: block.nonce
    val bytes = hash(map)
    return Hex.encodeHexString(bytes)
  }

  fun addBlock(block: Block): Block {
    chain.add(block)
    mempool.clear()
    return block
  }

  fun hash(map: Map<String, Any?>): ByteArray {
    val builder = GsonBuilder()
    val gson = builder.create()
    val json = gson.toJson(map)
    val bytes = json.toByteArray()
    val sha256 = MessageDigest.getInstance("SHA-256")
    return sha256.digest(bytes)
  }

  override fun toString(): String {
    val builder = GsonBuilder()
    builder.setPrettyPrinting()
    val gson = builder.create()
    val map = mutableMapOf<String, Any>()
    map["chain"] = chain
    map["transactions"] = mempool
    return gson.toJson(map)
  }

}

data class Block(
  val previous: String,
  val transactions: List<Transaction>,
  val timestamp: Long,
  val nonce: Long,
  val hash: String
)

data class Transaction(
  val sender: String,
  val receiver: String,
  val amount: Double,
  val timestamp: Long,
  val signature: String
)

fun makeWallet(): KeyPair {
  val generator = KeyPairGenerator.getInstance("RSA")
  generator.initialize(512)
  return generator.genKeyPair()
}

fun Blockchain.makeTransaction(a: KeyPair, b: KeyPair, amount: Double) {
  val transaction = createTransaction(a.public, b.public, amount)
  addTransaction(a.private, transaction)
}

fun Blockchain.makeBlock(receiver: KeyPair, amount: Double) {
  val temp = createBlock(receiver.public, amount)
  val block = mineBlock(temp)
  addBlock(block)
}

fun runBlockchain() {
  val a = makeWallet()
  val b = makeWallet()
  val c = makeWallet()
  val d = makeWallet()
  val e = makeWallet()
  val blockchain = Blockchain()
  with(blockchain) {
    makeTransaction(a, b, 40.0)
    makeTransaction(a, c, 40.0)
    makeBlock(a, 100.0)
    makeTransaction(b, d, 10.0)
    makeTransaction(b, e, 10.0)
    makeTransaction(c, d, 10.0)
    makeTransaction(c, e, 10.0)
    makeBlock(a, 100.0)
  }
  println(blockchain)
}
