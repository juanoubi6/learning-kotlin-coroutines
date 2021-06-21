import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

// Context: There is only 1 thread and 4 functions to execute.
// Functions 1 and 2 execute sequentially because each of them block the thread
// Functions 3 and 4 execute concurrently because they do not block the thread
suspend fun main(args: Array<String>) = withContext(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
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

    println("Done with main")
}

