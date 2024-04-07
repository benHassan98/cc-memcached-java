package server;

import cache.Cache;
import factory.CommandFactory;
import parser.Parser;

import java.io.*;
import java.net.ServerSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;



public class Server {

    private final Cache cache;
    private final CommandFactory commandFactory;
    private final Parser parser;

    public Server(Long cacheSize){
        this.cache = new Cache(cacheSize);
        this.commandFactory = new CommandFactory();
        this.parser = new Parser();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void start(int port) {



        try(ServerSocket serverSocket = new ServerSocket(port)){

            while (true){

                CompletableFuture
                        .completedFuture(serverSocket.accept())
                        .thenAcceptAsync((clientSocket)->{
                            BufferedReader in;
                            PrintWriter out;
                            System.out.println(clientSocket.getInetAddress()+" "+clientSocket.getPort()+" is Connected");
                            try {
                                in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
                                out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

                                List<String> inputList = new ArrayList<>();

                                while(!clientSocket.isClosed()){
                                    String line = in.readLine();

                                    if("".equals(line.trim()))continue;

                                    inputList.add(line);
                                    try{
                                        var commandRecord = parser.parse(inputList);

                                        if(!parser.hasNextLine(line)){

                                            inputList.clear();

                                            var command = commandFactory.getCommandByType(commandRecord.commandType());
                                            command.setCache(cache);
                                            command.execute(commandRecord, out);

                                        }
                                    }
                                    catch (Exception exception){
                                        out.print(exception.getMessage()+"\n");
                                        inputList.clear();
                                    }


                                }

                                in.close();
                                out.close();
                                clientSocket.close();


                            } catch (IOException exception) {
                                exception.printStackTrace();

                            }



                        });



            }


        }
        catch (IOException exception){

            exception.printStackTrace();

        }





    }


}
