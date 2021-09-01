package com.techgalery.reactiveupload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class BlockingUtils {
    /**
     * @param request example can be obtained from parameter injection
     * @return the URL format example: http://localhost:8080
     */
    public static String baseUrl(ServerHttpRequest request){
        URI uri = request.getURI();
        String port = "";
        if(uri.getPort()!=80){
            port = String.format(":%s", uri.getPort());
        }
        return String.format("%s://%s%s", uri.getScheme(), uri.getHost(), port);
    }

    /**
     * @param filePart example can be obtained from @RequestPart("file") FilePart
     * @param folder example :
     *               ./folder : parent folder,
     *               /folder : to system root folder
     * @return true if succeed false otherwise
     */
    public static Boolean createFile(FilePart filePart, String folder, String fileName) {
        try{
            String fullPath = folder+"/"+fileName;
            Path path = Files.createFile(Paths.get(fullPath).toAbsolutePath().normalize());
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE );
            DataBufferUtils.write(filePart.content(), channel, 0)
                    .doOnComplete(() -> {
                        try {
                            channel.close();
                        } catch ( Exception e ) {
                            e.printStackTrace();
                        }
                    })
                    .subscribe()
            ;
            return true;
        }catch (IOException e){
            log.error("ERROR CreateFile: {}", e.getMessage());
        }
        return false;
    }
}
