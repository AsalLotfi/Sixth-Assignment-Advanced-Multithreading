## Atomic Variables

### code

```java
import java.util.concurrent.atomic.AtomicInteger;
public class AtomicDemo {
  private static AtomicInteger atomicCounter = new AtomicInteger(0);
  private static int normalCounter = 0;
  public static void main(String[] args) throws InterruptedException {
    Runnable task = () -> {
      for (int i = 0; i < 1_000_000; i++) {
        atomicCounter.incrementAndGet();
        normalCounter++;
      }
    };

    Thread t1 = new Thread(task);
    Thread t2 = new Thread(task);
    t1.start();
    t2.start();
    t1.join();
    t2.join();

    System.out.println("Atomic Counter: " + atomicCounter);
    System.out.println("Normal Counter: " + normalCounter);
  }
}
  
```

### Questions:
### 1- What output do you get from the program? Why?
The output will consistently show:

```
Atomic Counter: 2000000
Normal Counter: <a value less than 2000000, and different each time>
```
This happens because `AtomicInteger` ensures thread-safe atomic operations, whereas the regular `int` increment (`normalCounter++`) is not 
synchronized and suffers from race conditions when accessed concurrently by multiple threads.

### 2- What is the purpose of AtomicInteger in this code?
The purpose of `AtomicInteger` in this code is to provide a thread-safe way to increment a counter (`atomicCounter`) without using explicit 
synchronization like `synchronized` blocks or locks.

### 3- What thread-safety guarantees does atomicCounter.incrementAndGet() provide?
`atomicCounter.incrementAndGet()` guarantees that the increment is atomic, thread-safe, and visible to all threads immediately. Even when 
called by multiple threads at the same time, no updates are lost, and each increment is correctly applied. This eliminates race conditions 
without using locks.

### 4- In which situations would using a lock be a better choice than an atomic variable?
Use a lock instead of an atomic variable when:

- You need to protect multiple variables together.
- You’re performing a compound operation (check-then-act, read-modify-write).
- You need thread coordination (e.g. waiting or signaling).
- The logic is too complex for a single atomic operation.

Use atomic variables when you’re just doing simple, thread-safe changes to a single value.

### 5- Besides AtomicInteger, what other data types are available in the java.util.concurrent.atomic package?
Besides `AtomicInteger`, the `java.util.concurrent.atomic` package includes:

- Other atomic primitives: `AtomicBoolean`, `AtomicLong`
- Atomic arrays: `AtomicIntegerArray`, `AtomicLongArray`, `AtomicReferenceArray`
- Atomic references: `AtomicReference`, `AtomicStampedReference`, `AtomicMarkableReference`
- Accumulators for high-performance: `LongAdder`, `LongAccumulator`

All of these allow threads to safely update shared data without locks, making them essential tools in concurrent programming.


---

## Monte Carlo Algorithm's Question:

### Was the multi-threaded implementation always faster than the single-threaded one?

- If yes, why?
- If not, what factors are the cause and what can you do to mitigate these issues?

answer: No, the multi-threaded version is not always faster.
While it benefits from parallelism, performance can be affected 
by thread overhead, limited CPU cores, inefficient random number usage, and memory contention. To make it 
faster, use the right number of threads, `ThreadLocalRandom`, and ensure each thread works independently without interfering with others.