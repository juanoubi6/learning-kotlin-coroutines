import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.concurrent.Executors

// Context: There is only 1 thread and 4 functions to execute.
// Functions 1 and 2 execute sequentially because each of them block the thread
// Functions 3 and 4 execute concurrently because they do not block the thread
suspend fun main(args: Array<String>) = withContext(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
    val startTime = Instant.now()

    // CoroutineScope waits until all coroutines launched inside it finish
    val result = coroutineScope {
        launch {
            println("Started blocking IO 1")
            Thread.sleep(5000)
            println("Finished blocking IO 1")
        }
        launch {
            println("Started blocking IO 2")
            Thread.sleep(5000)
            println("Finished blocking IO 2")
        }
        launch {
            println("Started non blocking IO 3")
            delay(5000)
            println("Finished non blocking IO 3")
        }
        launch {
            println("Started non blocking IO 4")
            delay(5000)
            println("Finished non blocking IO 4")
        }
        "finalResult"
    }

    println("Final result: $result")

    // Expected 15 seconds
    println("Done executing in single thread. Time spent: ${getTimeSince(startTime)} seconds")
}

