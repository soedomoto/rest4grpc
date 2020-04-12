package rest4grpc;

import org.springframework.http.MediaType;

public class GrpcMediaType {
    public static final MediaType GRPC_WEB_TEXT = MediaType.valueOf(GrpcMediaType.GRPC_WEB_TEXT_VALUE);
    public static final String GRPC_WEB_TEXT_VALUE = "application/grpc-web-text";
}
