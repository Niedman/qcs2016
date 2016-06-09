javac server/*.java
wsgen -cp . server.InsulineDoseCalculator
java -cp . server.InsulineDoseCalculatorEndpoint
