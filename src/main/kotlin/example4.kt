import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.util.concurrent.Executors

// Multi thread execution should spent less time executing.
fun main(args: Array<String>) {
    val sleepTimeList = listOf<Long>(3000, 3000, 3000)
    lateinit var startTime: Instant

    println("////////////// Multi thread execution //////////////")
    startTime = Instant.now()
    syncFunctionWithDefaultDispatcher(sleepTimeList)
    println("Done executing in multi thread. Time spent: ${getTimeSince(startTime)} seconds")

    println("////////////// Single thread execution //////////////")
    startTime = Instant.now()
    syncFunctionWithSingleThread(sleepTimeList)
    println("Done executing in single thread. Time spent: ${getTimeSince(startTime)} seconds")
}

// Default dispatchers has many threads, so each operation can execute concurrently.
// If the number of threads in the IO dispatcher >= 3 then the maximum time spent here will be 3 seconds.
// Take into account that IO dispatchers creates threads if necessary
fun syncFunctionWithDefaultDispatcher(sleepTimeList: List<Long>): String {
    runBlocking(Dispatchers.IO) {
        sleepTimeList.forEach { sleepTime ->
            // You can use launch instead of async too
            async {
                println("Started sleep time job")
                Thread.sleep(sleepTime)
                println("Ended sleep time job")
            }

        }
    }

    return "Finished"
}

// There is only 1 thread, so each operation will execute sequentially
fun syncFunctionWithSingleThread(sleepTimeList: List<Long>): String {
    runBlocking(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
        sleepTimeList.forEach { sleepTime ->
            launch {
                println("Started sleep time job")
                Thread.sleep(sleepTime)
                println("Ended sleep time job")
            }
        }
    }

    return "Finished"
}


