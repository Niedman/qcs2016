package server;

import javax.jws.WebService;
import javax.jws.WebMethod;

@WebService
public class InsulinDoseCalculator {

	@WebMethod
	public int mealtimeInsulinDose(int carbohydrateAmount, int carbohydrateToInsulinRatio, int preMealBloodSugar, int targetBloodSugar, int personalSensitivity) {
		double carbohydrateDose = 0.0, hbloodsugar = 0.0;

		if (carbohydrateAmount < 60 || carbohydrateAmount > 120) {
			return 0;
		}
		if (carbohydrateToInsulinRatio < 10 || carbohydrateToInsulinRatio > 15) {
			return 0;
		}
		if (preMealBloodSugar < 120 || preMealBloodSugar > 250) {
			return 0;
		}
		if (targetBloodSugar < 80 || targetBloodSugar > 120) {
			return 0;
		}
		if (personalSensitivity < 15 || personalSensitivity > 100) {
			return 0;
		}
		
		hbloodsugar = (double)(preMealBloodSugar - targetBloodSugar) /(double) personalSensitivity;

		carbohydrateDose = (double)carbohydrateAmount / (double)carbohydrateToInsulinRatio /(double) personalSensitivity * 50.0;

		return (int) Math.round(hbloodsugar + carbohydrateDose);
	}

	@WebMethod
	public int backgroundInsulinDose(int bodyWeight) {
		if (bodyWeight > 40 && bodyWeight < 130) {
			return (int) Math.round(0.55 * bodyWeight / 2);
		} else {
			return -1;
		}
	}

	@WebMethod
	public int personalSensitivityToInsulin(int physicalActivityLevel, int[] physicalActivitySamples, int[] bloodSugarDropSamples) {

		//Se o physicalActivitySamples != bloodSugarDropSamples ..... Temos retornar -1 (inputs invalidos)
		int n = physicalActivitySamples.length;
		if (physicalActivityLevel < 0 || physicalActivityLevel > 10) {
			return -1;
		}
		if (n < 2 || n > 10) {
			return -1;
		}

		int i = 0;
		double Sp = 0, Sb = 0, Spp = 0, Spb = 0;
		/*sum of values*/
		for (i = 0; i < n; i++) {
			if (physicalActivitySamples[i] < 0 || physicalActivitySamples[i] > 10) {
				return -1;
			}
			if (bloodSugarDropSamples[i] < 15 || bloodSugarDropSamples[i] > 100) {
				return -1;
			}
			Sp = Sp + physicalActivitySamples[i];
			Sb = Sb + bloodSugarDropSamples[i];
		}
		/*mean*/
		double pm, bm;
		pm = (double) Sp / n;
		bm = (double) Sb / n;
		/*linear regression*/
		for (i = 0; i < n; i++) {
			Spb = Spb + (physicalActivitySamples[i] - pm) * (bloodSugarDropSamples[i] - bm);
			Spp = Spp + (physicalActivitySamples[i] - pm) * (physicalActivitySamples[i] - pm);
		}

		double beta = Spb / Spp;
		double alfa = bm - beta * pm;

		return (int) Math.round(alfa + beta * physicalActivityLevel);
	}

}
