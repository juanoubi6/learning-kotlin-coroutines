import java.time.Instant

fun getTimeSince(somePastTime: Instant): Long{
    return Instant.now().minusMillis(somePastTime.toEpochMilli()).toEpochMilli()/1000
}