import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

// Context: There is only 1 thread and 4 functions to execute.
// Function 1 is created but not executed because of the LAZY start.
// Function 2 is created but not executed because of the LAZY start.
// Functions 3 and 4 start concurrently. After that, function 1 is also started because 3 and 4 are not blocking. Function 1 blocks the thread until completed
// After function 1 finises, functions 3 and 4 can continue concurrently.
// Function 2 never runs because it's never awaited.
suspend fun main(args: Array<String>) = withContext(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
    val result1 = async(start = CoroutineStart.LAZY) {
        println("Started blocking IO 1")
        Thread.sleep(10000)
        println("Finished blocking IO 1")

        "Done blocking IO 1"
    }
    async(start = CoroutineStart.LAZY) {
        println("Started blocking IO 2")
        Thread.sleep(2000)
        println("Finished blocking IO 2")

        "Done blocking IO 2"
    }
    async {
        println("Started non blocking IO 3")
        delay(5000)
        println("Finished non blocking IO 3")

        "Done non blocking IO 1"
    }
    async {
        println("Started non blocking IO 4")
        delay(5000)
        println("Finished non blocking IO 4")

        "Done non blocking IO 2"
    }

    result1.await()

    println("Done with main")
}

