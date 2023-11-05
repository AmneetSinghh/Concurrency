package PrimeNumbers;

public class PrimeNumbers {

    static Long MAX_LIMIT = 100000000L;
    static Long totalPrimeNumbers = 1L;
    /// Sqrt(MAX_LIMIT) complexity.
    static void checkPrime(int x){
        if(x%2==0){
            return ;
        }
        for(int i=3;i<=Math.sqrt(x);i++){
            if(x%i==0) {
                return;
            }
        }
        totalPrimeNumbers++;
    }

    public static void main(String args[]){
        System.out.println("Executing Prime Numbers started");
        Long startTime = System.currentTimeMillis();
        for(int i=3;i<=MAX_LIMIT;i++){
            checkPrime(i);
        }
        double endTime = (System.currentTimeMillis() - startTime)/1000.0;
        System.out.println("Checking till -> "+ MAX_LIMIT + "Found -> " + totalPrimeNumbers + " Prime numbers. took  -> " + endTime);
    }

}

/*
1. Normal chrome is open - took 81.115 seconds to run this program.
Executing Prime Numbers started
Checking till -> 100000000 Found -> 5761455 Prime numbers. took  -> 81.464
*/


/* PC

 Model Name:	MacBook Pro
  Model Identifier:	MacBookPro18,1
  Model Number:	MK183HN/A
  Chip:	Apple M1 Pro
  Total Number of Cores:	10 (8 performance and 2 efficiency)
  Memory:	16 GB
 */