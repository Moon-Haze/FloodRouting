package Core;

import edu.sdust.moon.Core.Address;
import edu.sdust.moon.Core.Node;

import java.io.IOException;
import java.util.ArrayList;

public class Try {
    private static ArrayList<Node> nodes = new ArrayList<>();
    private static ArrayList<Integer> posts = new ArrayList<>();

    static {
        posts.add(6857);
        posts.add(6869);
        posts.add(6870);
        posts.add(6871);
        posts.add(6872);
        posts.add(6873);
    }

    public static void main(String[] args) {
        {
            try {
                Node node = Node.createNode(new Address("127.0.0.1", posts.get(0)));
                node.connectNode(new Address("127.0.0.1", posts.get(1)));
                node.connectNode(new Address("127.0.0.1", posts.get(3)));
                nodes.add(node);
                node.start();
                System.out.println("Node 0 Over");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        {
            try {
                Node node = Node.createNode(new Address("127.0.0.1", posts.get(1)));
                node.connectNode(new Address("127.0.0.1", posts.get(0)));
                node.connectNode(new Address("127.0.0.1", posts.get(2)));
                nodes.add(node);
                node.start();
                System.out.println("Node 1 Over");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        {
            try {
                Node node = Node.createNode(new Address("127.0.0.1", posts.get(2)));
                node.connectNode(new Address("127.0.0.1", posts.get(1)));
                node.connectNode(new Address("127.0.0.1", posts.get(3)));
                nodes.add(node);
                node.start();
                System.out.println("Node 2 Over");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        {
            try {
                Node node = Node.createNode(new Address("127.0.0.1", posts.get(3)));
                node.connectNode(new Address("127.0.0.1", posts.get(0)));
                node.connectNode(new Address("127.0.0.1", posts.get(2)));
                node.connectNode(new Address("127.0.0.1", posts.get(4)));
                node.connectNode(new Address("127.0.0.1", posts.get(5)));
                nodes.add(node);
                node.start();
                System.out.println("Node 3 Over");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        {
            try {
                Node node = Node.createNode(new Address("127.0.0.1", posts.get(4)));
                node.connectNode(new Address("127.0.0.1", posts.get(3)));
                nodes.add(node);
                node.start();
                System.out.println("Node 4 Over");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        {
            try {
                Node node = Node.createNode(new Address("127.0.0.1", posts.get(5)));
                node.connectNode(new Address("127.0.0.1", posts.get(3)));
                nodes.add(node);
                node.start();
                System.out.println("Node 5 Over");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        nodes.get(0).sendData("Hello 6", "127.0.0.1:"+posts.get(5));
    }
}
