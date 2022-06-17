package com.generalov.string.handler;

public class StringHandler {
    public static String deleteBotName(String command){
        return command.contains("@cat_1_simulator_bot ")? command.substring(21): command;
    }

    public static String toUpperCaseFirstChar(String message){
        return Character.toUpperCase(message.charAt(0)) + message.substring(1).toLowerCase();
    }
}
