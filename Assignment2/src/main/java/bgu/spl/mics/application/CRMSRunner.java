package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;


/**
 * This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {

    private static LinkedList<Student> studentsList;
    private static LinkedList<ConfrenceInformation> conferencesList;
    private static Cluster cluster;
    private static LinkedList<Model> modelsList;

    public static void main(String[] args) throws FileNotFoundException {

        studentsList = new LinkedList<>();
        conferencesList = new LinkedList<>();
        modelsList = new LinkedList<>();

        File input = new File("/home/spl211/Desktop/Assignment_2/example_input.json");

        JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
        JsonObject fileObject = fileElement.getAsJsonObject();


        JsonArray students = fileObject.get("Students").getAsJsonArray();
        LinkedList<Thread> studentThreads = initializeStudents(students);


        JsonArray GPUS = fileObject.get("GPUS").getAsJsonArray();
        LinkedList<Thread> GPUThreads = initializeGPUS(GPUS);


        JsonArray CPUS = fileObject.get("CPUS").getAsJsonArray();
        LinkedList<Thread> CPUThreads = initializeCPUS(CPUS);


        JsonArray conferences = fileObject.get("Conferences").getAsJsonArray();
        LinkedList<Thread> ConferencesThreads = initializeConferences(conferences);


        int tickTime = fileObject.get("TickTime").getAsInt();
        long duration = fileObject.get("Duration").getAsInt();
        TimeService timeService = new TimeService(tickTime, duration);
        Thread TimeThread = new Thread(timeService);
        System.out.println(CPUThreads.size());
        System.out.println(GPUThreads.size());
        System.out.println(ConferencesThreads.size());
        System.out.println(studentThreads.size());
        for (int i = 0; i < GPUThreads.size(); i++) {
            GPUThreads.get(i).start();
        }


        for (int i = 0; i < CPUThreads.size(); i++) {
            CPUThreads.get(i).start();
        }


        for (int i = 0; i < ConferencesThreads.size(); i++) {
            ConferencesThreads.get(i).start();
        }
        TimeThread.start();

        for (int i = 0; i < studentThreads.size(); i++) {
            studentThreads.get(i).start();
        }
        try {
            TimeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cluster = Cluster.getInstance();
        output out = new output(studentsList, conferencesList, cluster.getGPUS(), cluster.getCPUS(), modelsList);
        System.exit(0);
        System.out.println("finish");
    }

    public static LinkedList<Thread> initializeStudents(JsonArray students) {
        LinkedList<Thread> studentThread = new LinkedList<>();
        for (int i = 0; i < students.size(); i++) {
            JsonObject student = students.get(i).getAsJsonObject();
            Student studentInfo = new Student(student.get("name").getAsString(), student.get("department").getAsString(), student.get("status").getAsString());
            studentsList.add(studentInfo);
            JsonArray models = student.get("models").getAsJsonArray();
            for (int j = 0; j < models.size(); j++) {
                JsonObject modelInfo = models.get(j).getAsJsonObject();
                Data data = new Data(modelInfo.get("type").getAsString(), modelInfo.get("size").getAsInt());
                Model model = new Model(modelInfo.get("name").getAsString(), data, studentInfo);
                modelsList.add(model);
            }
            StudentService service = new StudentService(studentInfo.getName(), studentInfo, modelsList);
            Thread thread = new Thread(service);
            studentThread.add(thread);
        }
        return studentThread;
    }

    public static LinkedList<Thread> initializeGPUS(JsonArray GPUS) {
        LinkedList<Thread> GPUThread = new LinkedList<>();
        for (int i = 0; i < GPUS.size(); i++) {
            GPU gpu = new GPU(GPUS.get(i).getAsString());
            GPUService service = new GPUService("gpu", gpu);
            Thread thread = new Thread(service);
            GPUThread.add(thread);
        }
        return GPUThread;
    }

    public static LinkedList<Thread> initializeCPUS(JsonArray CPUS) {
        LinkedList<Thread> CPUThread = new LinkedList<>();
        for (int i = 0; i < CPUS.size(); i++) {
            CPU cpu = new CPU(CPUS.get(i).getAsInt());
            CPUService service = new CPUService("cpus", cpu);
            Thread thread = new Thread(service);
            CPUThread.add(thread);
        }
        return CPUThread;
    }

    public static LinkedList<Thread> initializeConferences(JsonArray Conferences) {
        LinkedList<Thread> ConferencesThread = new LinkedList<>();
        for (int i = 0; i < Conferences.size(); i++) {
            JsonObject c = Conferences.get(i).getAsJsonObject();
            ConfrenceInformation conference = new ConfrenceInformation(c.get("name").getAsString(), c.get("date").getAsInt());
            conferencesList.add(conference);
            ConferenceService service = new ConferenceService("conference", conference);
            Thread thread = new Thread(service);
            ConferencesThread.add(thread);
        }
        return ConferencesThread;
    }
}