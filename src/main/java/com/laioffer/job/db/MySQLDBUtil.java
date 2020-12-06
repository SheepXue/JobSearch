package com.laioffer.job.db;

public class MySQLDBUtil {
    private static final String INSTANCE = "project.c0lwnfcn8omn.us-east-2.rds.amazonaws.com";
    private static final String PORT_NUM = "3306";
    public static final String DB_NAME = "JupiterDB";
    private static final String USERNAME = "****";
    private static final String PASSWORD = "******";
    public static final String URL = "jdbc:mysql://"
            + INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
            + "?user=" + USERNAME + "&password=" + PASSWORD
            + "&autoReconnect=true&serverTimezone=UTC";
}
