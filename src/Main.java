import server.Server;

import java.util.Arrays;
import java.util.Optional;

public class Main {

    public static Optional<Integer> getPort(String[] args){


        int portIndex = Arrays.asList(args).indexOf("-p");
        if(portIndex != -1 ){


            if(portIndex + 1 < args.length){

                try{
                   Integer port = Integer.parseInt(args[portIndex + 1]);
                   return Optional.of(port);
                }
                catch (NumberFormatException e){
                    return Optional.empty();
                }


            }

            return Optional.empty();
        }

        return Optional.of(11211);


    }
    public static Optional<Long> getCacheSizeInBytes(String[] args){

        int sizeIndex = Arrays.asList(args).indexOf("-m");
        if(sizeIndex != -1 ){


            if(sizeIndex + 1 < args.length){

                try{
                    Long size = Long.getLong(args[sizeIndex + 1]) * 1024L;
                    return Optional.of(size);
                }
                catch (NumberFormatException e){
                    return Optional.empty();
                }


            }

            return Optional.empty();
        }

        return Optional.of(1024L);

    }

    public static void main(String[] args){

        var port = getPort(args);
        var cacheSize = getCacheSizeInBytes(args);

        if(port.isEmpty()){
            System.out.println("Please Enter a valid Port");
            return;
        }
        if(cacheSize.isEmpty()){
            System.out.println("Please Enter a valid Size");
            return;
        }

        Server server = new Server(cacheSize.get());
        server.start(port.get());

    }
}