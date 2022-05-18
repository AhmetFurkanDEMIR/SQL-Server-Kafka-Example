![](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)  ![](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white) ![](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white) ![](https://img.shields.io/badge/Microsoft%20SQL%20Server-CC2927?style=for-the-badge&logo=microsoft%20sql%20server&logoColor=white)


## SQL Server Kafka Example

![index](https://user-images.githubusercontent.com/54184905/169161043-3a33ca1f-d267-40fd-83be-f1cd130e5508.jpeg)

Hello everyone, in this example, when a change is detected in SQL Server (Insert, Update and Delete), we will capture it with Debezium connect and write it to Kafka and then read it.

First of all, we need to run the images in the [docker-compose.yml](/docker-compose.yml) file. The images in this file are respectively zookeeper, broker (Confluent Kafka), schema-registry, Debezium Connect, Confluentinc Control-center and Ksql. Use the commands below to run Docker images.

[Installing Docker](https://docs.docker.com/engine/install/ubuntu/)


```terminal
# Cleaning up Docker images
docker rm -f $(docker ps -a -q)
docker volume rm $(docker volume ls -q)

# Running all images
docker-compose up
```

Now that we have all our images running smoothly, we can now switch to SQL Server months, if you want to use the tables I use, it will be enough to run the sql queries named [SQL_ServerExample.sql](/SQL_ServerExample.sql). Then run the following SQL query by modifying it according to your own tables.

```sql
# Command to turn on Change Data Capture on the selected database.
USE DB_Ecommerce
EXEC sys.sp_cdc_enable_db

# The command to turn on the Change Data Capture feature in the selected tables, modify this command according to how many tables you have and run it again.
EXEC sys.sp_cdc_enable_table @source_schema =  N'dbo', @source_name =  N'TBL_Users', @role_name =  NULL, @supports_net_changes =  0;
```

Now our settings in SQL server are finished, now we need to connect our tables with the Debezium connector at the address 0.0.0.0:8083, for this we will post it to this address with the following command, we need to establish a special connection for each table separately.


```terminal
# Table0
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" 0.0.0.0:8083/connectors/ -d '{ "name": "SqlServer_TBL_Users", 
"config": { 
"connector.class": "io.debezium.connector.sqlserver.SqlServerConnector",
"database.hostname": "192.168.1.8",
"database.port": "1433",
"database.user": "SA",
"database.password": "123456789Zz.",
"database.dbname": "DB_Ecommerce",
"database.server.name": "DB_EcommerceServerUsers",
"table.include.list": "dbo.TBL_Users",
"database.history.kafka.bootstrap.servers": "broker:29092", 
"database.history.kafka.topic": "dbhistory.DB_EcommerceServerUsers" } 
}';

# Table1
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" 0.0.0.0:8083/connectors/ -d '{ "name": "SqlServer_TBL_Order", 
"config": { 
"connector.class": "io.debezium.connector.sqlserver.SqlServerConnector",
"database.hostname": "192.168.1.8",
"database.port": "1433",
"database.user": "SA",
"database.password": "123456789Zz.",
"database.dbname": "DB_Ecommerce",
"database.server.name": "DB_EcommerceServerOrder",
"table.include.list": "dbo.TBL_Order",
"database.history.kafka.bootstrap.servers": "broker:29092", 
"database.history.kafka.topic": "dbhistory.DB_EcommerceServerOrder" } 
}';
```

When you go to [http://0.0.0.0:9021/](http://0.0.0.0:9021/) (Confluent Control Center) and click on the connect part, there should be as many Running as the number of tables. If you are not seeing a similar image, you may have made a mistake somewhere, please review the processes up to this point.

![Screenshot_2022-05-18_23-49-25](https://user-images.githubusercontent.com/54184905/169154008-9faeee42-b52f-42b6-bcd6-b03a36fdd7c8.png)
Since we haven't had any problems so far, we can add some data to our tables.

```sql
INSERT  INTO TBL_Users VALUES('Ahmet Furkan','DEMIR','552');
INSERT  INTO TBL_Users VALUES('Mustafa','Kalemci','554');

INSERT  INTO TBL_Order VALUES(1,'Pizza',200);
INSERT  INTO TBL_Order VALUES(1,'Kulaklik',600);
```

After adding data to the tables, go to Confluent Control Center again and examine the Topics section, something like the image below should meet you. It writes to these Topics as Debezium producer and we should read this data as Consumer with Java.

![Screenshot_2022-05-18_23-57-37](https://user-images.githubusercontent.com/54184905/169155066-3259f240-c19a-4a2f-9cb6-41ecb20278fa.png)
You can see the changes you have made in the tables through these topics, as in the example picture below.

![Screenshot_2022-05-19_00-03-41](https://user-images.githubusercontent.com/54184905/169155688-658fd4df-21d3-4915-ae95-072a79fd0cd3.png)
But reading these messages from the Confluent Control Center will not help us, instead we can read the messages with Java and print the changed data to the new database or transfer it to another place with the API. 

For this, we will use the application written in Java named [kafkaConsumer](/kafkaConsumer/) available in this repository. According to the comment lines in the code, modify your own project and run this application. From this moment, the selected topics in Kafka are listened and when any changes are detected, they are highlighted on the screen.

![Screenshot_2022-05-19_00-10-47](https://user-images.githubusercontent.com/54184905/169156803-60021196-8b93-4cbd-8af3-e532c4e3f114.png)

![Screenshot_2022-05-19_00-13-15](https://user-images.githubusercontent.com/54184905/169156797-90ce0139-2693-40db-be32-b543f9210530.png)

That's all I'm going to tell you, you can modify this architecture and use it for different purposes in your own projects, take care :).

[Ahmet Furkan DEMIR](https://www.ahmetfurkandemir.com/)




