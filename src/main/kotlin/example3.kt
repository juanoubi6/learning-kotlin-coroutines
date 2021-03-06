import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.concurrent.Executors

// Context: There is only 1 thread and 4 functions to execute.
// Function 1 is created but not executed because of the LAZY start.
// Functions 2 and 3 start concurrently. After that, function 1 is also started because we awaited it and functions
// 2 and 3 are not blocking. Function 1 blocks the thread until completed
// After function 1 finishes, functions 2 and 3 can continue concurrently (because thread is not blocked anymore).
suspend fun main(args: Array<String>) = withContext(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
    val startTime = Instant.now()

    val result = coroutineScope {
        val result1 = async(start = CoroutineStart.LAZY) {
            println("Started blocking IO 1")
            Thread.sleep(10000)
            println("Finished blocking IO 1")

            "Done blocking IO 1"
        }
        async {
            println("Started non blocking IO 2")
            delay(5000)
            println("Finished non blocking IO 2")

            "Done non blocking IO 2"
        }
        async {
            println("Started non blocking IO 3")
            delay(5000)
            println("Finished non blocking IO 3")

            "Done non blocking IO 3"
        }

        result1.await()
    }

    println("Final result: $result")

    // Expected 10 seconds
    println("Done executing in single thread. Time spent: ${getTimeSince(startTime)} seconds")
}

