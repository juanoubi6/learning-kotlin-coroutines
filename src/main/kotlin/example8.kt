import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.concurrent.Executors

// Context: There is only 1 thread, in which we launch 2 coroutines in the same coroutine scope
// The first coroutine will finish after 5 seconds
// The second coroutine is tied to the GlobalScope, so it should keep running even when coroutineScope finishes
suspend fun main(args: Array<String>) = withContext(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
    val startTime = Instant.now()

    // CoroutineScope waits until all coroutines launched inside it finish
    coroutineScope {
        launch {
            println("Started blocking IO ")
            delay(5000)
            println("Finished blocking IO")
        }
        GlobalScope.launch {
            for (i in 1..10) {
                println("Doing some tasks on coroutine launched on GlobalScope")
                delay(1000)
            }
        }
    }

    delay(2000)
    println("Finished executing coroutineScope")

    // Expected 7 seconds
    println("Done executing in single thread. Time spent: ${getTimeSince(startTime)} seconds")
}

