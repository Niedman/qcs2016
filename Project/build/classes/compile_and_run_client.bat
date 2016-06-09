mkdir client\jaxws
wsimport -p client -keep http://0.0.0.0:8081/calculator?wsdl -s client/jaxws
javac client\Client.java -cp client\jaxws
java -cp . client.Client
