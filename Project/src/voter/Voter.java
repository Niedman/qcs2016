/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voter;
    

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import javax.xml.ws.WebServiceException;

/**
 *
 * @author niedman
 */
public class Voter {

    static ConcurrentHashMap<String, Boolean> webServices = new ConcurrentHashMap<String, Boolean>();

    static String[] urls = {"http://qcs08.dei.uc.pt:8081/calculator?wsdl","http://qcsa1-beardsdei.rhcloud.com/qcsa1/InsulinDoseCalculator?wsdl",
        "http://qcs05.dei.uc.pt:8080/InsulinDoseCalculator?wsdl", "http://qcsproject1-qcsproject.rhcloud.com/InsulinDoseCalculator?wsdl",
        "http://insulincalculator-aybareon.rhcloud.com/InsulinCalculatorTomcat/InsulinCalculator?wsdl",
        "http://webservice-sqdcourse.rhcloud.com/InsulinDoseCalculator?wsdl",
        "http://qcs07.dei.uc.pt:8080/InsulinCalculator?wsdl","http://qcsassignment1-salvadorapps.rhcloud.com/InsulinDoseCalculatorWebService?wsdl","http://qcs01.dei.uc.pt:9000/InsulinDoseCalculator?wsdl"};
    
    //static String[] urls = {"http://qcs05.dei.uc.pt:8080/InsulinDoseCalculator?wsdl","http://qcs08.dei.uc.pt:8081/calculator?wsdl","http://webservice-sqdcourse.rhcloud.com/InsulinDoseCalculator?wsdl","http://insulincalculator-aybareon.rhcloud.com/InsulinCalculatorTomcat/InsulinCalculator?wsdl","http://qcsa1-beardsdei.rhcloud.com/qcsa1/InsulinDoseCalculator?wsdl"};

    public static String ourService = "http://qcs08.dei.uc.pt:8081/calculator?wsdl";
    int mealtimeInsulinDose;
    int backgroundInsulinDose;
    int personalSensitivityToInsulin;
    public static int numServices = urls.length;
    public static String detalhada;
    public static int NUM_THREADS = numServices;
    public static long timeout = 3500000000L;

    public static String getDetalhada() {
        return detalhada;
    }

    public Voter() {
        for (String url : urls) {
            webServices.put(url, false);
        }
        
    }
    
    public String testClient(int number){
        return "Result is " + number;
    }
    
    private static int handleServices(ArrayList parameters, String methods){
        ExecutorService threadPool;
        Set<Callable<Integer[]>> callables = new HashSet();
        int[] result;
        int validServices=0;
        int votedResult=0;
        
        ArrayList<ClientService> services = generateConnections();

        threadPool = Executors.newFixedThreadPool(NUM_THREADS);
        
        for(int i=0;i<services.size();i++){
            callables.add(new IntegerTask(i, services.get(i), parameters, methods));
            
        }
        
        try {
            List<Future<Integer[]>> futures = threadPool.invokeAll(callables, timeout, TimeUnit.NANOSECONDS);
            result = new int[futures.size()];
            
            Arrays.fill(result, -1);
            for(int i=0;i<futures.size();i++){
                System.out.println("Service:"+services.get(i).getUrl());
                if (!futures.get(i).isCancelled()){
                    result[futures.get(i).get()[0]] = futures.get(i).get()[1];
                    validServices++;
                }
            }
            shutdownServices(services);
            threadPool.shutdown();
            
            
            votedResult = votador(result);
   

            detalhada = getDetails(services, result, validServices, votedResult);
            
            return votedResult;
            
           
        } catch (InterruptedException ex ) {
            
            shutdownServices(services);
            return -1;
        } catch(ExecutionException e){
            shutdownServices(services);
            System.out.println("Execution exception"+e.getLocalizedMessage());
            return -1;
        } catch(javax.xml.ws.WebServiceException e){
            shutdownServices(services);
            System.out.println("Connection Timeout"+e.getLocalizedMessage());
            return -1;
        }catch( java.lang.NoSuchMethodError e){
             shutdownServices(services);
            System.out.println("No such method "+e.getLocalizedMessage());
             return -1;
        }
        
        
    } 
    
    public static String getDetails(ArrayList<ClientService> services, int[] result,int numberWebServices,int votedResult){
        StringBuilder sb = new StringBuilder();
        sb.append("Number of Available Web Services: ").append(numberWebServices).append("\n");
        for (int i=0;i<services.size();i++){
            sb.append("URL Service: "+services.get(i).getUrl()).append(" ");
            if (result[i] == -1){
                sb.append("Service Result: ").append("Timeout").append("\n");
            }else{
                sb.append("Service Result: ").append(result[i]).append("\n");
            }
            
        }
        sb.append("Voted Result: ").append(votedResult);
        
        return sb.toString();
    }
    
    public static int standardInsulin(int carbohydrateAmount, int carbohydrateToInsulinRatio, int preMealBloodSugar, int targetBloodSugar, int personalSensitivity){
        
        ArrayList<Object> parameters = new ArrayList();
        int result;

        parameters.add(carbohydrateAmount);
        parameters.add(carbohydrateToInsulinRatio);
        parameters.add(preMealBloodSugar);
        parameters.add(targetBloodSugar);
        parameters.add(personalSensitivity);

        while(true) {
            result = handleServices(parameters, "standardInsulin");
            if (result != -1)
                return result;
            else
                return -1;
        }
    }
    
    public static int backgroundInsulin(int weight){

        
        ArrayList<Object> parameters = new ArrayList();
        int result;

        parameters.add(weight);
        
        while(true) {
            result = handleServices(parameters, "backgroundInsulin");
            if (result != -1)
                return result;
            else
                return -1;
        }
    }
    
    public static int personalInsulin(int carbohydrateAmount, int carbohydrateToInsulinRatio, int preMealBloodSugar,int targetBloodSugar, int physicalActivityLevel, int[] physicalActivitySamples,int[] bloodSugarDropSamples){
        
        ArrayList<Object> parameters = new ArrayList();
        int result;

        parameters.add(carbohydrateAmount);
        parameters.add(carbohydrateToInsulinRatio);
        parameters.add(preMealBloodSugar);
        parameters.add(targetBloodSugar);
        parameters.add(physicalActivityLevel);
        parameters.add(physicalActivitySamples);
        parameters.add(bloodSugarDropSamples);

        while(true) {
            result = handleServices(parameters, "personalInsulin");
            if (result != -1)
                return result;
            else
                return -1;
        }
    }
    
    public static int votador(int [] result){
        
        Arrays.sort(result);
        
        int tmpMax=0;
        int contaMax=1;
        HashMap<Integer,Integer> map = new HashMap();
        
        
        for (int i=0;i<result.length;i++){
            
             
            if (!map.containsKey(result[i])){
                map.put(result[i], 1);
            
            }else{
                contaMax = map.get(result[i]) +1;
                map.replace(result[i], contaMax);
            
            }           
        }
        
        int temp =0;
        for (Entry<Integer, Integer> entry : map.entrySet()){
            
            for (int j=0;j<result.length;j++){
                if (valueIsEqual(entry.getKey(), result[j])){
                    temp = entry.getValue();
                    map.replace(entry.getKey(), temp++);
                }
                    
            }
            System.out.println("Chave:"+entry.getKey() + "Valor:"+entry.getValue());
        }
        int conta = 0;
        int maxValueAux =0;
        int maxValueInMap=(Collections.max(map.values()));  // This will return max value in the Hashmap
        for (Entry<Integer, Integer> entry : map.entrySet()) {  // Itrate through hashmap
            if (entry.getValue()==maxValueInMap) {
                conta++;
                maxValueAux = entry.getKey();
                
            }
        }
        if (conta >1){
            
            return -1;
        }

        return maxValueAux;
        
    }
    
    public static boolean valueIsEqual(int i1,int i2){
        int diferenca = i2 -i1;
        
        if (diferenca >= -1 || diferenca <= 1){
            return true;
        }
        
        return false;
    }
    public static ClientService getNewConnection() {
        ArrayList<String> availableServices;
        ClientService service = null;
        boolean status;
        int check = 0;

        availableServices = getServices();
        System.out.println("Available Services " + availableServices);

        if (availableServices.isEmpty()) {
            return null;
        }

        do {
            try{
                System.out.println("Check service:" + availableServices.get(check));
                service = new ClientService(availableServices.get(check));
                status = false;
                webServices.put(availableServices.get(check), true);
                
            }catch (WebServiceException | java.lang.NoSuchMethodError e) {
                
                status = true;
                if (++check >= availableServices.size()){
                    System.out.println("There are not services left");
                    
                    return null;
                }
                System.out.println("Connection Timeout");
                
            }
        } while (status);
        
        return service;
    }

    public static ArrayList<ClientService> generateConnections() {
        ArrayList<ClientService> services = new ArrayList<ClientService>();
        ClientService client;
        int count =0;
        for (int i = 0; i < numServices; i++) {
            client = getNewConnection();
            count++;
            if (client != null) {
                services.add(client);
            }
        }
        return services;
    }

    public static ArrayList<String> getServices() {
        ArrayList<String> services = new ArrayList<String>();
        //Collections.shuffle(webServices);
        Iterator it = webServices.entrySet().iterator();
        Map.Entry mapEntry;
        while (it.hasNext()) {
            mapEntry = (Map.Entry) it.next();
            if (mapEntry.getValue().equals(false)) {
                services.add((String) mapEntry.getKey());
            }
        }

        return services;

    }
    
    public static void shutdownServices(ArrayList<ClientService> services){
        
        for(int i=0;i<services.size();i++){
            webServices.put(services.get(i).getUrl(), false);
        }     
    }

}
