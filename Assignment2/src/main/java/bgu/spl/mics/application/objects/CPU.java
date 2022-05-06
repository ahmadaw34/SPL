package bgu.spl.mics.application.objects;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private final int cores;
    private final LinkedList<DataBatch> dataBatchCollection;
    private final Cluster cluster;
    private int numOfTicks;
    private long waitTime;
    private DataBatch dataToProcess;

    public CPU(){
        this.cores = 0;
        dataBatchCollection=new LinkedList<>();
        cluster = Cluster.getInstance();
        numOfTicks = 0;
        waitTime = 0;
    }

    public CPU(int cores){
        this.cores = cores;
        dataBatchCollection = new LinkedList<>();
        cluster = Cluster.getInstance();
        numOfTicks = 0;
        waitTime = 0;
    }

    public void increaseTicks(){
        numOfTicks++;
    }

    public void calculateTicks(DataBatch dataBatch){
        waitTime = 32 / cores;
        if(dataBatch.getData().getType().equals("Images"))
            waitTime = waitTime * 4;
        else if(dataBatch.getData().getType().equals("Text"))
            waitTime = waitTime * 2;
    }



    public int getNumOfTicks()
    {
        return numOfTicks;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setNumOfTicks(int numOfTicks) {
        this.numOfTicks = numOfTicks;
    }

    public DataBatch getDataToProcess() {
        return dataToProcess;
    }

    public void setDataToProcess(DataBatch dataToProcess) {
        this.dataToProcess = dataToProcess;
    }

    public void reset(){
        this.dataToProcess = null;
        this.waitTime = 0;
        this.numOfTicks = 0;
    }
}
