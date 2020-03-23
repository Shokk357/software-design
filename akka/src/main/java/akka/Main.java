package akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actors.MasterSearchActor;
import akka.search.SearchClientStub;
import akka.util.Timeout;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;

public class Main {

    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("MySystem");

        final Scanner scanner = new Scanner(System.in);

        String searchRequest;

        int master_num = 0;
        while (true) {
            System.out.print("Type your search request: ");
            searchRequest = scanner.nextLine();
            if (searchRequest.equals("Break")) {
                break;
            }

            Timeout receiveTimeout = new Timeout(1, TimeUnit.MILLISECONDS);

            ActorRef master =
                    system.actorOf(Props.create(MasterSearchActor.class, receiveTimeout, new SearchClientStub(0)), "master" + master_num++);

            final Object response = ask(master, searchRequest, new Timeout(1, TimeUnit.MINUTES))
                    .toCompletableFuture()
                    .join();

            System.out.println(response);

            system.stop(master);
        }

        system.terminate();
    }
}
