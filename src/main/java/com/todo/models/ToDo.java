package com.todo.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName="ToDo")
public class ToDo {
    @DynamoDBHashKey
    private String id;

    @DynamoDBAttribute
    private String description;

    @DynamoDBAttribute
    private int priority;

    @DynamoDBAttribute
    private Date dueDate;

    @DynamoDBAttribute
    private String cognito_sub;

    @DynamoDBAttribute
    private String cognito_username;

    @DynamoDBAttribute
    private String cognito_email;
}
