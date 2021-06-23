import arrow.core.getOrHandle
import arrow.fx.IO
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

val exposedDatabaseV2 = Database.connect(
    url = "jdbc:postgresql://localhost:5432/kotlin-examples-db",
    driver = "org.postgresql.Driver",
    user = "postgres",
    password = "s3cr3t4"
).also { db ->
    transaction(db) {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(FilmsV2)
    }
}

object FilmsV2 : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 50)
    override val primaryKey = PrimaryKey(id)
}

suspend fun nonBlockingFunctionV2(number: Int):String{
    println("Started non blocking function $number")
    val result = blockingRepositoryV2(number).attempt().suspended().map {
        "${it.size}"
    }.getOrHandle {
        "error"
    }
    println("Finished non blocking function $number")

    return result
}

fun blockingRepositoryV2(number: Int): IO<List<String>> {
    println("Started blocking repository $number")

    val result: IO<List<String>> = dbResultOfV2(exposedDatabaseV2) {
        val filmsNameList = mutableListOf<String>()
        for (num in 1..8000) {
            FilmsV2.insert { it[name] = "Film $num" }
            filmsNameList.add("Film $num")
        }
        filmsNameList
    }

    println("Finished blocking repository $number")

    return result
}

fun <A> dbResultOfV2(database: Database, f: suspend Transaction.() -> A): IO<A> = IO {
    newSuspendedTransaction(Dispatchers.IO, database, Connection.TRANSACTION_READ_COMMITTED) {
        f()
    }
}

// Example using coroutines and accessing the database using Exposed ORM with non-blocking functionality, using arrow FX IO.
// When a database operation starts, thread does not block and starts executing another coroutine.

// The difference with Example 5 is that, in this case, the database operation gets executed when we call attempt().suspend()
// so the executing thread will switch between coroutines in line 42. What IO does is encapsulate the blocking call
// without executing it so we can execute it later.
suspend fun main(args: Array<String>) = withContext(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
    val startTime = Instant.now()

    val result = coroutineScope {
        launch { nonBlockingFunctionV2(1) }
        launch { nonBlockingFunctionV2(2) }
        launch { nonBlockingFunctionV2(3) }
        launch { nonBlockingFunctionV2(4) }
        launch { nonBlockingFunctionV2(5) }
        launch { nonBlockingFunctionV2(6) }
        "finalResult"
    }

    println("Final result: $result")

    println("Done executing in single thread. Time spent: ${getTimeSince(startTime)} seconds")
}

