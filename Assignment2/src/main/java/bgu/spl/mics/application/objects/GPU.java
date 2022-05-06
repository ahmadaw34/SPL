package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {

    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private final Type type;
    private Model model;
    private final Cluster cluster;
    private final int VRAM;
    private final int numOfTicks;
    private int wanted_time;
    private int trainedData;
    private int total_data;
    private String result_of_test;

    public GPU(String type) {
        trainedData = 0;
        total_data = 0;
        if (type.equals("RTX3090")) {
            this.type = Type.RTX3090;
            numOfTicks = 1;
            VRAM = 32;
        } else if (type.equals("RTX2080")) {
            this.type = Type.RTX2080;
            VRAM = 16;
            numOfTicks = 2;
        } else {
            this.type = Type.GTX1080;
            VRAM = 8;
            numOfTicks = 4;
        }
        this.model = null;
        cluster = Cluster.getInstance();
        wanted_time = 0;
    }

    public void train() {
        // should wait for the process to end.
        DataBatch batch = cluster.getProcessedData(this);
        if(batch != null) {

            while (cluster.getTime().intValue() <= wanted_time) {
                if (!cluster.getWorking()) {
                    break;
                }
            }

            if (cluster.getWorking()) {
                trainedData++;
            }
        }
    }

    public void test(Double pro) {
        if (model.getStudent().getStatus().equals(Student.Degree.MSc)) {
            if (pro < 0.6) {
                model.setResult(Model.Result.Good);
                this.result_of_test = "GOOD";
            } else{
                model.setResult(Model.Result.Bad);
                this.result_of_test = "BAD";
            }
        } else {
            if (pro < 0.8) {
                model.setResult(Model.Result.Good);
                this.result_of_test = "GOOD";
            } else {
                model.setResult(Model.Result.Bad);
                this.result_of_test = "BAD";
            }
        }
    }

    public String getResult_of_test() {
        return result_of_test;
    }

    public void createDataBatches() {
        int size = model.getData().getSize();
        Data data = model.getData();
        for (int i = 0; i < size; i += 1000) {
            cluster.addUnprocessedData(new DataBatch(data, i, this));
            total_data++;
        }
    }

    public void increaseTicks() {
        wanted_time += numOfTicks;
    }

    public int getNumOfTicks() {
        return numOfTicks;
    }
    public int getTrainedData(){
        return trainedData;
    }

    public int getTotal_data() {
        return total_data;
    }

    public void setModel(Model model) {
        this.total_data = 0;
        this.trainedData = 0;
        this.wanted_time = cluster.getTime().intValue() + this.numOfTicks;
        this.model = model;
        createDataBatches();
    }

    /*public void setProcessedData(DataBatch dataBatch){

    }

    public void takeFromCluster(DataBatch dataBatch) {

    }*/

    public boolean checkCapacity() {
        int size = cluster.getDataSize(this);
        if (size <= VRAM)
            return true;
        return false;
    }

}
