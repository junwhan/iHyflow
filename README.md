Compile:
mvn clean install dependency:copy-dependencies -DskipTests
Copy a config file:
cp src/main/java/org/infinispan/iHyflow/default.conf target/classes/org/infinispan/iHyflow/.
