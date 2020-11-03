package edu.sdust.moon.Core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;

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
            config.getNodes().forEach(item -> {
                try {
                    node.connectNode(item);
                } catch (IOException e) {
                    logger.error("connect the node( " + item + " ) failed");
                }
            });
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
                            System.out.println("type in \"connect\"                        Connect other nodes again");
                            System.out.println("type in \"connect <host>:<port>\"          Connect a new node by host and port.");
                            System.out.println("type in \"disconnect <host>:<port>\"       Disconnect the node");
                            System.out.println("type in \"sendData <data> <host>:<port>\"  Send data to one node( host:port )");
                        }
                        case "sendData" -> {
                            if (arg.length >= 3) {
                                for (String s : arg) {
                                    System.out.println(s);
                                }
                                StringBuilder sb = new StringBuilder();
                                try {
                                    for (int i = 1; i < arg.length - 1; i++) {
                                        sb.append(arg[i]);
                                        sb.append(" ");
                                    }
                                }catch (ArrayIndexOutOfBoundsException e){
                                    e.printStackTrace();
                                }
                                node.sendData(sb.toString(), arg[arg.length-1]);
                            } else {
                                logger.error("Incorrect instruction");
                            }
                        }
                        case "connect" -> {
                            if (arg.length == 1) {
                                config.getNodes().forEach(item -> {
                                    try {
                                        node.connectNode(item);
                                    } catch (IOException e) {
                                        logger.error("connect the node( " + item + " ) failed");
                                    }
                                });
                            } else if (arg.length == 3) {
                                Address address = new Address(arg[1], Integer.parseInt(arg[2]));
                                try {
                                    node.connectNode(address);
                                    config.getNodes().add(address);
                                    ConfigRead.createReader().Save();
                                    logger.info("connect the node( " + address + " ) successfully");
                                } catch (IOException e) {
                                    logger.error("connect the node( " + address + " ) failed");
                                }
                            } else {
                                logger.error("Incorrect instruction");
                            }
                        }
                        case "list" -> {
                            if (config.getNodes().size() != 0) {
                                System.out.println("The list of node's address:");
                                config.getNodes().forEach(System.out::println);
                            } else {
                                System.out.println("The list of node's address is null.");
                            }
                        }
                        case "disconnect" -> {
                            if (arg.length < 3) {
                                logger.error("Incorrect instruction");
                            } else {
                                Address address = new Address(arg[1], Integer.parseInt(arg[2]));
                                node.disconnectNode(address);
                                config.getNodes().remove(address);
                                ConfigRead.createReader().Save();
                                logger.info(" Disconnected the node ( " + address + " )");
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