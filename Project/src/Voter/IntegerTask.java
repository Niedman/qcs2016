/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voter;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 *
 * @author niedman
 */
public class IntegerTask implements Callable<Integer[]>{
    
    private int id;
    private ClientService service;
    private ArrayList<Object> parameters;
    private String methods;

    public IntegerTask(int id, ClientService service, ArrayList<Object> parameters, String methods) {
        this.id = id;
        this.service = service;
        this.parameters = parameters;
        this.methods = methods;
    }
    
    @Override
    public Integer[] call() throws Exception {
        //Thread.sleep(4000);
        if (methods.equals("standardInsulin")){
            return new Integer[]{this.id,service.standardInsulin((Integer) parameters.get(0), (Integer)parameters.get(1),(Integer) parameters.get(2),(Integer) parameters.get(3),(Integer) parameters.get(4))};   
        }else if (methods.equals("backgroundInsulin")){
            return new Integer[]{this.id,service.backgroundInsulin((Integer) parameters.get(0))};
        }else if (methods.equals("personalInsulin")){
            return new Integer[]{this.id,service.personalInsulin((Integer) parameters.get(0), (Integer)parameters.get(1),(Integer) parameters.get(2),(Integer) parameters.get(3),(Integer) parameters.get(4),(int[]) parameters.get(5),(int[]) parameters.get(6))};
        }else{
            return new Integer[]{-1};
        }  
    }
    
    
}
