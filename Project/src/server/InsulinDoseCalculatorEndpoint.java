package server;

import javax.xml.ws.Endpoint;

public class InsulinDoseCalculatorEndpoint {
	public static void main(String[] args) {
		InsulinDoseCalculator dosecalc = new InsulinDoseCalculator();
		Endpoint endpoint = Endpoint.publish("http://0.0.0.0:8081/calculator", dosecalc);
	}
}