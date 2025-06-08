package MonteCarloPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class MonteCarloPi {

    static final long NUM_POINTS = 50_000_000L;
    static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    public static void main(String[] args) throws InterruptedException, ExecutionException
    {
        // Without Threads
        System.out.println("Single threaded calculation started: ");
        long startTime = System.nanoTime();
        double piWithoutThreads = estimatePiWithoutThreads(NUM_POINTS);
        long endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (single thread): " + piWithoutThreads);
        System.out.println("Time taken (single threads): " + (endTime - startTime) / 1_000_000 + " ms");

        // With Threads
        System.out.printf("Multi threaded calculation started: (your device has %d logical threads)\n",NUM_THREADS);
        startTime = System.nanoTime();
        double piWithThreads = estimatePiWithThreads(NUM_POINTS, NUM_THREADS);
        endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (Multi-threaded): " + piWithThreads);
        System.out.println("Time taken (Multi-threaded): " + (endTime - startTime) / 1_000_000 + " ms");
    }

    // Monte Carlo Pi Approximation without threads
    public static double estimatePiWithoutThreads(long numPoints)
    {
        Random random = new Random();
        long insideCircle = 0;

        for (long i = 0; i < numPoints; i++) {
            double x = random.nextDouble() * 2 - 1; // Value between -1.0 and 1.0
            double y = random.nextDouble() * 2 - 1;

            if ((x * x) + (y * y) <= 1) {
                insideCircle++;
            }
        }
        return 4.0 * insideCircle / numPoints;
    }

    // Monte Carlo Pi Approximation with threads
    public static double estimatePiWithThreads(long numPoints, int numThreads) throws InterruptedException, ExecutionException
    {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Long>> futures = new ArrayList<>();

        long pointsPerThread = numPoints / numThreads;

        // Submit one task per thread
        for (int i = 0; i < numThreads; i++) {
            Callable<Long> task = () -> {
                Random random = new Random();
                long insideCircle = 0;
                for (long j = 0; j < pointsPerThread; j++) {
                    double x = random.nextDouble() * 2 - 1;
                    double y = random.nextDouble() * 2 - 1;
                    if (x * x + y * y <= 1) {
                        insideCircle++;
                    }
                }
                return insideCircle;
            };
            futures.add(executor.submit(task));
        }

        // Gather results
        long totalInsideCircle = 0;
        for (Future<Long> future : futures) {
            totalInsideCircle += future.get(); // get() blocks until the result is ready
        }

        executor.shutdown(); // No more tasks will be accepted
        executor.awaitTermination(1, TimeUnit.MINUTES); // Wait for all tasks to finish

        return 4.0 * totalInsideCircle / numPoints;
    }
}