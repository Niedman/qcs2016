/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.BindingProvider;

/**
 *
 * @author niedman
 */
public class ClientService {
    
    private InsulinDoseCalculator proxy;
    private String url;
    
    
    public ClientService(String serviceURL) throws javax.xml.ws.WebServiceException,java.lang.NoSuchMethodError
    {
        try {
            InsulinDoseCalculatorService service = null;
            
            this.url = serviceURL;
            service = new InsulinDoseCalculatorService(new URL(serviceURL));
            proxy = service.getInsulinDoseCalculatorPort();
            
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(ClientService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public int standardInsulin(int carbohydrateAmount, int carbohydrateToInsulinRatio, int preMealBloodSugar, int targetBloodSugar, int personalSensitivity){
        int result;

        try {
            result = proxy.mealtimeInsulinDose(carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar, personalSensitivity);
        } catch (javax.xml.ws.WebServiceException e) {
            
            result = -1;
        }

        return result;
    }

    public int backgroundInsulin(int bodyWeight){
        int result;

        try {
            result = proxy.backgroundInsulinDose(bodyWeight);
        } catch (javax.xml.ws.WebServiceException e) {
           
            result = -1;
        }

        return result;
    }
    
    public int personalInsulin(int carbohydrateAmount, int carbohydrateToInsulinRatio, int preMealBloodSugar,int targetBloodSugar, int physicalActivityLevel, int[] physicalActivitySamples,int[] bloodSugarDropSamples ){
        int result;
        int sensitivity;

        try {
            sensitivity = proxy.personalSensitivityToInsulin(physicalActivityLevel, arrayToList(physicalActivitySamples), arrayToList(bloodSugarDropSamples));
            result = proxy.mealtimeInsulinDose(carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar, sensitivity);
        } catch (javax.xml.ws.WebServiceException e) {
            
            result = -1;
        }

        return result;
    }
    
    public String getUrl() {
        return url;
    }
    /**
	 * Instead of passing an int[] to a web service, pass a List<Integer>.
	 * <p>
	 * This method converts an array of int to a List of Integer. When a web
	 * service publishes a method that receives an int[] as parameter, the type
	 * gets converted to a "sequence" in XML, and the client should therefore
	 * submit, as parameter, a List<Integer> instead of an int[]. The web
	 * service will still receive an int[] on the server side.
	 *
	 * @param array the array of int, to be converted to List<Integer>
	 * @return a list containing the same numbers as the array
	 */
	static List<Integer> arrayToList(int[] array) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i : array) {
			list.add(i);
		}
		return list;
	}
    
    
}
