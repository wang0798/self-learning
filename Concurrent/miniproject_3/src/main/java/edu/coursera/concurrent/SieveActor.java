package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;

import static edu.rice.pcdp.PCDP.finish;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        //throw new UnsupportedOperationException();
        final SieveActorActor actorActor = new SieveActorActor(2);
        finish(()->{
            for(int i=3;i<=limit;i+=2){
                actorActor.send(i);
            }
            actorActor.send(0);
        });
        int numPrimes = 0;
        SieveActorActor loop = actorActor;
        while(loop!=null){
            numPrimes+=loop.getNumLocalPrimes();
            loop=loop.getNextActor();
        }
        return numPrimes;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        private static final int MAX_LOCAL_PRIMES = 1000;
        private final int[] localPrimes = new int[MAX_LOCAL_PRIMES];
        private int numLocalPrimes;
        private SieveActorActor nextActor;
        SieveActorActor(final int localPrime){
            this.localPrimes[0] = localPrime;
            this.numLocalPrimes = 1;
            this.nextActor = null;
        }

        public SieveActorActor getNextActor(){
            return this.nextActor;
        }
        public int getNumLocalPrimes(){
            return this.numLocalPrimes;
        }
        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            //throw new UnsupportedOperationException();
            final int candidate = (Integer) msg;
            if(candidate<=0){
                if(nextActor!=null){
                    nextActor.send(msg);
                }
                return;
            }else {
                if(isLocallyPrime(candidate)){
                    if(numLocalPrimes<MAX_LOCAL_PRIMES){
                        localPrimes[numLocalPrimes] = candidate;
                        numLocalPrimes++;
                    }else{
                        if(nextActor==null){
                            nextActor = new SieveActorActor(candidate);
                        }
                        nextActor.send(msg);
                    }
                }
            }
        }

        private boolean isLocallyPrime(final int candidate){
            for(int i=0;i<numLocalPrimes;i++){
                if(candidate%localPrimes[i]==0){
                    return false;
                }
            }
            return true;
        }
    }
}
