import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.time.Instant
import java.util.concurrent.Executors

val exposedDatabase = Database.connect(
    url = "jdbc:postgresql://localhost:5432/kotlin-examples-db",
    driver = "org.postgresql.Driver",
    user = "postgres",
    password = "s3cr3t4"
).also { db ->
    transaction(db) {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Films)
    }
}

object Films : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 50)
    override val primaryKey = PrimaryKey(id)
}

suspend fun nonBlockingFunction(number: Int): String {
    println("Started non blocking function $number")
    val result = blockingRepository(number)
    println("Finished non blocking function $number")

    return "${result.size}"
}

suspend fun blockingRepository(number: Int): List<String> {
    println("Started blocking repository $number")

    val result = dbResultOf(exposedDatabase) {
        val filmsNameList = mutableListOf<String>()
        for (num in 1..8000) {
            Films.insert { it[name] = "Film $num" }
            filmsNameList.add("Film $num")
        }
        filmsNameList
    }

    println("Finished blocking repository $number")

    return result
}

suspend fun <A> dbResultOf(database: Database, f: suspend Transaction.() -> A): A =
    try {
        newSuspendedTransaction(Dispatchers.IO, database, Connection.TRANSACTION_READ_COMMITTED) {
            f()
        }
    } catch (ex: Exception) {
        throw ex
    }

// Example using coroutines and accessing the database using Exposed ORM with non-blocking functionality.
// When a database operation starts, thread does not block and starts executing another coroutine.

// The difference with Example 6 is that, in this case, the database operation gets executed when the exposed suspend
// transaction is executed, so the executing thread will switch between coroutines in line 66
suspend fun main(args: Array<String>) = withContext(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
    val startTime = Instant.now()

    val result = coroutineScope {
        launch { nonBlockingFunction(1) }
        launch { nonBlockingFunction(2) }
        launch { nonBlockingFunction(3) }
        launch { nonBlockingFunction(4) }
        launch { nonBlockingFunction(5) }
        launch { nonBlockingFunction(6) }
        "finalResult"
    }

    println("Final result: $result")

    println("Done executing in single thread. Time spent: ${getTimeSince(startTime)} seconds")
}

