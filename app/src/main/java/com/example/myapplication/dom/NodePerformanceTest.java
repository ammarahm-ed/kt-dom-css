package com.example.myapplication.dom;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NodePerformanceTest {

    public static void test() {
        NodePerformanceTest test = new NodePerformanceTest();
        test.runPerformanceTest();
    }

    public void runPerformanceTest() {
        int numNodes = 100000;

        // Measure Node creation time
        long startTime = System.nanoTime();
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            nodes.add(new Node("test"));
        }
        long endTime = System.nanoTime();
        long creationTime = endTime - startTime;
        System.out.println("Node creation time for " + numNodes + " nodes: " + creationTime / 1_000_000 + " ms");

        // Measure appendChild operation time
        Node root = new Node("test");
        startTime = System.nanoTime();
        for (Node node : nodes) {
            root.appendChild(node);
        }
        endTime = System.nanoTime();
        long appendChildTime = endTime - startTime;
        System.out.println("appendChild operation time for " + numNodes + " nodes: " + appendChildTime / 1_000_000 + " ms");

        // Measure insertBefore operation time
        Node newNode = new Node("test");
        startTime = System.nanoTime();
        root.insertBefore(newNode, root.getFirstChild());
        endTime = System.nanoTime();
        long insertBeforeTime = endTime - startTime;
        System.out.println("insertBefore operation time: " + insertBeforeTime / 1_000_000 + " ms");

        // Measure removeChild operation time
        startTime = System.nanoTime();
        root.removeChild(newNode);
        endTime = System.nanoTime();
        long removeChildTime = endTime - startTime;
        System.out.println("removeChild operation time: " + removeChildTime / 1_000_000 + " ms");

        // Measure getChildNodes operation time
        startTime = System.nanoTime();
        List<Node> childNodes = root.getChildNodes();
        endTime = System.nanoTime();
        long getChildNodesTime = endTime - startTime;
        System.out.println("getChildNodes operation time: " + getChildNodesTime / 1_000_000 + " ms");

        // Measure addEventListener operation time
        startTime = System.nanoTime();
        for (Node node : nodes) {
            node.addEventListener("click", (EventListener) evt -> {
//                Log.d("EVENT", "click");
            }, AddEventListenerOptions.create(false, false, false));
        }
        endTime = System.nanoTime();
        long addEventListenerTime = endTime - startTime;
        System.out.println("addEventListener operation time for " + numNodes + " nodes: " + addEventListenerTime / 1_000_000 + " ms");

        // Measure dispatchEvent operation time

        startTime = System.nanoTime();
        for (Node node : nodes) {
            Event testEvent = new Event("click", new EventInit());
            node.dispatchEvent(testEvent);
        }
        endTime = System.nanoTime();
        long dispatchEventTime = endTime - startTime;
        System.out.println("dispatchEvent operation time for " + numNodes + " nodes: " + dispatchEventTime / 1_000_000 + " ms");
    }
}
