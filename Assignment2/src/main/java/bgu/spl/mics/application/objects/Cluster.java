package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
    private final ConcurrentHashMap< GPU, LinkedBlockingQueue<DataBatch> > GPUS;
    private final LinkedBlockingQueue<CPU> CPUS;
    private final AtomicInteger time;
    private final LinkedBlockingQueue<DataBatch> unprocessedData;
    private int numOfDataProcessed;
    private int numOfCPUTime;
    private int numOfGPUTime;
    private boolean Working;

    /**
     * Retrieves the single instance of this class.
     */

    private Cluster() {
        GPUS = new ConcurrentHashMap<>();
        CPUS = new LinkedBlockingQueue<>();
        numOfDataProcessed = 0;
        numOfCPUTime = 0;
        time = new AtomicInteger(1);
        unprocessedData = new LinkedBlockingQueue<>();
        Working = true;
        numOfGPUTime=0;
    }

    private static class SingletonHolder {
        private static Cluster instance = new Cluster();
    }

    public static Cluster getInstance() {
        return Cluster.SingletonHolder.instance;
    }

    public synchronized void increaseCpuTime(){
        this.numOfCPUTime++;
    }
    public int getNumOfCPUTime()
    {
        return numOfCPUTime;
    }
    public synchronized void increaseGpuTime(int x){
        this.numOfGPUTime+=x;
    }
    public int getNumOfGPUTime()
    {
        return numOfGPUTime;
    }

    public AtomicInteger getTime() {
        return time;
    }

    public boolean getWorking(){
        return this.Working;
    }
    public void setNotWorking(){
        this.Working = false;
    }
    public void increaseNumOfDataProcessed()
    {
        numOfDataProcessed++;
    }
    public int getNumOfDataProcessed() {
        return numOfDataProcessed;
    }

    public ConcurrentHashMap<GPU, LinkedBlockingQueue<DataBatch>> getGPUS() {
        return GPUS;
    }

    public LinkedList<CPU> getCPUS() {
        LinkedList<CPU> CPUSList = new LinkedList<>();
        int size = CPUS.size();
        for (int i = 0; i < size; i++) {
            CPU cpu = CPUS.poll();
            CPUSList.add(cpu);
            CPUS.add(cpu);
        }
        return CPUSList;
    }

    public synchronized void addUnprocessedData(DataBatch dataBatch){
            this.unprocessedData.add(dataBatch);
    }

    public synchronized void addProcessedData(DataBatch batch) {//cpu
        GPUS.putIfAbsent(batch.getOwner() , new LinkedBlockingQueue<>());
        GPUS.get(batch.getOwner()).add(batch);
    }

    public synchronized DataBatch getUnprocessedData(){
            return unprocessedData.poll();
    }

    public DataBatch getProcessedData(GPU gpu){
        try {
            return GPUS.get(gpu).poll();
        }catch (Exception e){
            return null;
        }
    }


    public int getDataSize(GPU gpu){
        return GPUS.get(gpu).size();
    }
}