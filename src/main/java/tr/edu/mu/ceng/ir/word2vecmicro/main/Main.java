package tr.edu.mu.ceng.ir.word2vecmicro.main;

import tr.edu.mu.ceng.ir.word2vecmicro.client.Word2VecClient;
import tr.edu.mu.ceng.ir.word2vecmicro.registration.EurekaServer;
import tr.edu.mu.ceng.ir.word2vecmicro.server.Word2VecServer;

public class Main {
    public static void main(String[] args) {

        String appName = "";

        switch (args.length) {
            case 2:
                System.setProperty("server.port", args[1]);
            case 1:
                appName = args[0].toLowerCase();
                break;

            default:
                return;
        }

        if (appName.equals("eureka")) {
            EurekaServer.main(args);
        } else if (appName.equals("word2vec")) {
            Word2VecServer.main(args);
        } else if (appName.equals("client")) {
            Word2VecClient.main(args);
        } else {
            System.out.println("Unknown application type: " + appName);
        }
    }
}
