package edu.sdust.moon.Core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

public class Start {
    private static Logger logger = LogManager.getLogger("FloodRouting");
    private static ConfigObj config;

    public static Logger getLogger() {
        return logger;
    }

    public static ConfigObj getConfig() {
        return config;
    }

    public static void setConfig(ConfigObj config) {
        Start.config = config;
    }

    public static void main(String[] args) {
        String RunDir = System.getProperty("user.dir");
        if (ConfigRead.createReader().ReadStart(RunDir)) {
            logger.info("Please modify the configuration file and restart.");
            System.exit(0);
        }
        System.out.println(config.getName() + "   " + config.getLocalAddress());
        if (config.getNodes().size() != 0) {
            System.out.println("The list of node's address:");
            config.getNodes().forEach(System.out::println);
        } else {
            System.out.println("The list of node's address is null.");
        }
        logger = LogManager.getLogger(config.getName());
        logger.info("Starting...");
        Node node;
        try {
            node = Node.createNode(config.getLocalAddress());
            node.start();
            config.getNodes().forEach(node::connectNode);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
            return;
        }
        Scanner scanner = new Scanner(System.in);
        String data;
        while ((data = scanner.nextLine()) != null) {
            try {
                var arg = data.split(" ");
                if (arg.length != 0) {
                    switch (arg[0]) {
                        case "" -> {}
                        case "help" -> {
                            System.out.println("type in \"stop\"                           Turn off the node");
                            System.out.println("type in \"list\"                           Get a list of node's address");
                            System.out.println("type in \"count <num>\"                    Sets times a package  forwarded");
                            System.out.println("type in \"connect <host>:<port>\"          Connect a new node by host and port.");
                            System.out.println("type in \"disconnect <host>:<port>\"       Disconnect the node");
                            System.out.println("type in \"sendData <data> <host>:<port>\"  Send data to one node( host:port )");
                        }
                        case "count"->{
                            if (arg.length==2){
                                config.setPkgCount(Integer.parseInt(arg[1]));
                            }
                        }
                        case "sendData" -> {
                            if (arg.length >= 3) {
                                StringBuilder sb = new StringBuilder();
                                try {
                                    for (int i = 1; i < arg.length - 1; i++) {
                                        sb.append(arg[i]).append(" ");
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                                node.sendData(sb.toString(), arg[arg.length - 1]);
                            } else {
                                logger.error("Incorrect instruction");
                            }
                        }
                        case "connect" -> {
                            if (arg.length == 2) {
                                node.connectNode(Address.createAddress(arg[1]));
                            } else {
                                logger.error("Incorrect instruction");
                            }
                        }
                        case "list" -> {
                            Set<Address> addressSet = node.getLinkNodes();
                            if (addressSet.isEmpty()) {
                                System.out.println("No node connecting with.");
                            } else {
                                System.out.println("The list of node's address:");
                                node.getLinkNodes().forEach(System.out::println);
                            }
                        }
                        case "disconnect" -> {
                            if (arg.length > 2) {
                                logger.error("Incorrect instruction");
                            } else {
                                node.disconnectNode(Address.createAddress(arg[1]));
                            }
                        }
                        case "stop" -> {
                            System.out.println("Stopping...");
                            node.stop();
                            return;
                        }
                        default -> logger.error("Incorrect instruction");
                    }
                }
            } catch (NumberFormatException e) {
                logger.error("Incorrect instruction");
            } catch (Exception e) {
                logger.error("Error in console: " + e);
            }
        }
    }
}