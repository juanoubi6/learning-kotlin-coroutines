import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

fun blockingIO(number: String): String {
    println("Started blocking IO $number")
    Thread.sleep(2000)
    println("Finished blocking IO $number")

    return "Done blocking IO"
}

suspend fun nonBlockingIOUsingDispatcher(number: String): String {
    withContext(Dispatchers.IO) {
        println("Started non blocking IO $number")
        Thread.sleep(5000)
        println("Finished non blocking IO $number")
    }

    return "Done non blocking IO"
}

suspend fun nonBlockingIOUsingPrimitives(number: String): String {
    println("Started non blocking IO $number")
    delay(5000)
    println("Finished non blocking IO $number")

    return "Done non blocking IO"
}

// Context: There is only 1 thread and 6 functions to execute.
// Functions 1,2,3 and 4 functions execute sequentially because they block the thread.
// Functions 5 and 6 execute concurrently because they do not block the thread
suspend fun main(args: Array<String>) = withContext(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
    async { blockingIO("1") }
    async { blockingIO("2") }
    async { blockingIO("3") }
    async { blockingIO("4") }
    async { nonBlockingIOUsingDispatcher("5") }
    async { nonBlockingIOUsingPrimitives("6") }

    println("Done with main")
}

