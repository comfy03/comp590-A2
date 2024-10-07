## Dining Philosophers Problem Solution in Java

This Java solution to the **Dining Philosophers Problem** 

### Key Concepts:
- **Philosophers** are represented as threads alternating between thinking and eating.
- **Forks** are shared resources that are synchronized to ensure only one philosopher can use each fork at a time.
- A **Semaphore** controls philosopher access to forks, allowing only `NUM_PHILOSOPHERS - 1` philosophers to attempt eating simultaneously, preventing deadlock.

