mkdir voter/jaxws
wsimport -p voter -keep http://10.16.0.88:8081/calculator?wsdl -s voter/jaxws
javac voter/Voter.java -cp voter/jaxws
java -cp . voter.Voter
