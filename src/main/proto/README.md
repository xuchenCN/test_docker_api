
To generate grpc-java classes:
mvn generate-sources 

Use CLI generate protobuf code 
```
cd src/main/proto
protoc --java_out=../grpc-gen/ *.proto
```
