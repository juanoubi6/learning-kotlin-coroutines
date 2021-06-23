import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.concurrent.Executors

fun blockingIO(number: String): String {
    println("Started blocking IO $number")
    Thread.sleep(2000)
    println("Finished blocking IO $number")

    return "Done blocking IO"
}

// "suspend" prefix is not relevant on this function because all the code inside is blocking
suspend fun blockingIOWithSuspendFunction(number: String): String {
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

// If we change the dispatcher for the Default dispatcher and our computer has more than one core, then blocking
// operations will execute concurrently as they will be mapped to different threads.
suspend fun main(args: Array<String>) = withContext(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
    val startTime = Instant.now()

    // CoroutineScope waits until all coroutines launched inside it finish
    val result = coroutineScope {
        async { blockingIO("1") }
        async { blockingIO("2") }
        async { blockingIOWithSuspendFunction("3") }
        async { blockingIOWithSuspendFunction("4") }
        async { nonBlockingIOUsingDispatcher("5") }
        async { nonBlockingIOUsingPrimitives("6") }
        "finalResult"
    }
    println("Final result: $result")

    // Expected 13 seconds
    println("Done executing in single thread. Time spent: ${getTimeSince(startTime)} seconds")
}

