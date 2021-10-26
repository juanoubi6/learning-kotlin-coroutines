import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant

// Context of test 1: There is only 1 thread and a list with 2 jobs to execute.
// Each job contains a blocking IO operation.
// Jobs cannot execute concurrently because they contain blocking IO operations.

// Context of test 2: There is only 1 thread and a list with 2 jobs to execute.
// Each job contains a non-blocking IO operation.
// Jobs execute concurrently because they contain non-blocking IO operations.
fun main() {
    println("////////////////// Test 1 //////////////////")
    val startTime = Instant.now()

    // runBlocking waits until all coroutines launched inside are finished. Only 1 thread executes all this code
    val result = runBlocking {
        val jobs = listOf(
            launch {
                println("Started blocking IO 1")
                Thread.sleep(5000)
                println("Finished blocking IO 1")
            },
            launch {
                println("Started blocking IO 2")
                Thread.sleep(5000)
                println("Finished blocking IO 2")
            }
        )
        jobs.joinAll()
        "Result"
    }

    println("Final result: $result")

    // Expected 10 seconds
    println("Done executing blocking IO jobs in single thread. Time spent: ${getTimeSince(startTime)} seconds")

    ////////////////////////////// Second test //////////////////////////////
    println("////////////////// Test 2 //////////////////")
    val startTime2 = Instant.now()

    // runBlocking waits until all coroutines launched inside are finished. Only 1 thread executes all this code
    val result2 = runBlocking {
        val jobs = listOf(
            launch {
                println("Started non-blocking IO 1")
                delay(5000)
                println("Finished non-blocking IO 1")
            },
            launch {
                println("Started non-blocking IO 2")
                delay(5000)
                println("Finished non-blocking IO 2")
            }
        )
        jobs.joinAll()
        "Result"
    }

    println("Final result: $result2")

    // Expected 5 seconds
    println("Done executing non-blocking IO jobs in single thread. Time spent: ${getTimeSince(startTime2)} seconds")
}

