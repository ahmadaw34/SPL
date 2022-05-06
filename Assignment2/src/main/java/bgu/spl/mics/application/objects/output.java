package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class output {

    private LinkedList<Student> students;
    private LinkedList<ConfrenceInformation> conferences;
    private ConcurrentHashMap<GPU, LinkedBlockingQueue<DataBatch>> GPUS;
    private LinkedList<CPU> CPUS;
    private Cluster cluster;
    private LinkedList<Model> models;

    public output(LinkedList<Student> students, LinkedList<ConfrenceInformation> confrences,
                  ConcurrentHashMap<GPU, LinkedBlockingQueue<DataBatch>> GPUS, LinkedList<CPU> CPUS, LinkedList<Model> models) {
        this.students = students;
        this.conferences = confrences;
        this.GPUS = GPUS;
        this.CPUS = CPUS;
        cluster = Cluster.getInstance();
        this.models = models;

        //File output = new File("/home/spl211/Desktop/Assignment_2/");
        JsonArray studentsArray = new JsonArray();
        for (int i = 0; i < students.size(); i++) {
            JsonObject studentObject = new JsonObject();
            studentObject.addProperty("name", students.get(i).getName());
            studentObject.addProperty("department", students.get(i).getDepartment());
            if (students.get(i).getStatus().equals(Student.Degree.MSc))
                studentObject.addProperty("status", "MSc");
            else
                studentObject.addProperty("status", "PhD");
            studentObject.addProperty("publications", students.get(i).getPublications());
            studentObject.addProperty("papersRead", students.get(i).getPapersRead());
            JsonArray trainedModels = new JsonArray();
            for (int j = 0; j < models.size(); j++) {
                if (models.get(j).isTrainedOrTested()) {
                    JsonObject model = new JsonObject();
                    model.addProperty("name", models.get(j).getName());
                    JsonObject data = new JsonObject();
                    data.addProperty("type", models.get(j).getData().getType());
                    data.addProperty("size", models.get(j).getData().getSize());
                    model.add("data", data);
                    model.addProperty("status", models.get(j).getStatus());
                    model.addProperty("result", models.get(j).getResults());
                    trainedModels.add(model);
                }
            }
            studentObject.add("trainedModels", trainedModels);
            studentsArray.add(studentObject);
        }

        JsonArray conferenceArray = new JsonArray();
        for (int i = 0; i < confrences.size(); i++) {
            JsonObject conf = new JsonObject();
            conf.addProperty("name", confrences.get(i).getName());
            conf.addProperty("date", confrences.get(i).getDate());
            JsonArray publicationsArray = new JsonArray();
            LinkedList<Model> goodModels = conferences.get(i).getModels();
            for(int j = 0; j < goodModels.size(); j++) {
                JsonObject model = new JsonObject();
                model.addProperty("name", confrences.get(i).getModels().get(j).getName());
                JsonObject data = new JsonObject();
                data.addProperty("name", goodModels.get(j).getData().getType());
                data.addProperty("size", goodModels.get(j).getData().getSize());
                model.add("data", data);
                model.addProperty("status", goodModels.get(j).getStatus());
                model.addProperty("results", goodModels.get(j).getResults());
                publicationsArray.add(model);
            }
            conf.add("publications",publicationsArray);
            conferenceArray.add(conf);
        }
        JsonObject MainJson=new JsonObject();
        MainJson.add("students",studentsArray);
        MainJson.add("conferences",conferenceArray);
        MainJson.addProperty("cpuTimeUsed",cluster.getNumOfCPUTime());

//        int gpuTimeUsed=0;
//        for(int i=0;i<GPUS.size();i++)
//        {
//            gpuTimeUsed+=GPUS.get(i).getNumOfTicks();
//        }
        MainJson.addProperty("gpuTimeUsed",cluster.getNumOfGPUTime());

        MainJson.addProperty("batchesProcessed",cluster.getNumOfDataProcessed());
        Gson outputFile=new GsonBuilder().setPrettyPrinting().create();
        try
        {
            FileWriter file=new FileWriter("/home/spl211/Desktop/Assignment_2/output.json");
            outputFile.toJson(MainJson,file);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
